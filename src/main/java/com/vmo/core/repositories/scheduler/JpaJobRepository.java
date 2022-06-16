package com.vmo.core.repositories.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmo.core.common.config.scheduler.JobSchedulerProperties;
import com.vmo.core.common.utils.SchedulerUtils;
import com.vmo.core.scheduler.job.BaseJobParameters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JdbcStepExecutionDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class JpaJobRepository extends SimpleJobRepository {
    private static final Log LOG = LogFactory.getLog(JpaJobRepository.class);
    private JpaJobInstanceRepository jobInstanceDao;
    private JobExecutionDao jobExecutionDao;
    private StepExecutionDao stepExecutionDao;
    private ExecutionContextDao ecDao;
    private ObjectMapper objectMapper;
    private JobSchedulerProperties schedulerProperties;

    public JpaJobRepository(
            JpaJobInstanceRepository jobInstanceDao, JobExecutionDao jobExecutionDao,
            StepExecutionDao stepExecutionDao, ExecutionContextDao ecDao,
            ObjectMapper objectMapper, JobSchedulerProperties schedulerProperties
    ) {
        super(jobInstanceDao, jobExecutionDao, stepExecutionDao, ecDao);
        this.jobInstanceDao = jobInstanceDao;
        this.jobExecutionDao = jobExecutionDao;
        this.stepExecutionDao = stepExecutionDao;
        this.ecDao = ecDao;
        this.objectMapper = objectMapper;
        this.schedulerProperties = schedulerProperties;

        if (this.stepExecutionDao instanceof JdbcStepExecutionDao) {
            ((JdbcStepExecutionDao) this.stepExecutionDao).setExitMessageLength(schedulerProperties.getMaxExitMessageLength());
        }
    }

    @Override
    public JobExecution createJobExecution(
            String jobName, JobParameters jobParameters
    ) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Assert.notNull(jobName, "Job name must not be null.");
        Assert.notNull(jobParameters, "JobParameters must not be null.");
        JobInstance jobInstance = this.jobInstanceDao.getJobInstance(jobName, jobParameters);
        ExecutionContext executionContext;
        if (jobInstance == null) {
            jobInstance = this.jobInstanceDao.createJobInstance(jobName, jobParameters);
            executionContext = new ExecutionContext();
        } else {
            List<JobExecution> executions = this.jobExecutionDao.findJobExecutions(jobInstance);
            if (executions.isEmpty()) {
                throw new IllegalStateException("Cannot find any job execution for job instance: " + jobInstance);
            }

            BaseJobParameters baseJobParameters = objectMapper.convertValue(jobParameters.toProperties(), BaseJobParameters.class);

            for (JobExecution execution : executions) {
                if (!execution.isRunning() && !execution.isStopping()) {
                    BatchStatus status = execution.getStatus();
                    if (status == BatchStatus.UNKNOWN && !schedulerProperties.isForceRestart()) {
                        throw new JobRestartException("Cannot restart job from UNKNOWN status. The last execution ended with a failure that could not be rolled back, so it may be dangerous to proceed. Manual intervention is probably necessary.");
                    }

                    Collection<JobParameter> allJobParameters = execution.getJobParameters().getParameters().values();
                    long identifyingJobParametersCount = allJobParameters.stream().filter(JobParameter::isIdentifying).count();
                    if (identifyingJobParametersCount <= 0L || status != BatchStatus.COMPLETED && status != BatchStatus.ABANDONED) {
                        continue;
                    }

                    if (!SchedulerUtils.canRepeat(baseJobParameters)) {
                        throw new JobInstanceAlreadyCompleteException("A job instance already exists and is complete for parameters=" + jobParameters + ".  If you want to run this job again, change the parameters.");
                    }
                }

                if (!SchedulerUtils.canRepeat(baseJobParameters) && !schedulerProperties.isForceRestart()) {
                    throw new JobExecutionAlreadyRunningException("A job execution for this job is already running: " + jobInstance);
                }
            }

            executionContext = this.ecDao.getExecutionContext(this.jobExecutionDao.getLastJobExecution(jobInstance));
        }

        JobExecution jobExecution = new JobExecution(jobInstance, jobParameters, (String)null);
        jobExecution.setExecutionContext(executionContext);
        jobExecution.setLastUpdated(new Date(System.currentTimeMillis()));
        this.jobExecutionDao.saveJobExecution(jobExecution);
        this.ecDao.saveExecutionContext(jobExecution);
        return jobExecution;
    }

    public void updateJobInstanceKey(JobInstance instance, JobParameters newParameters) {
        String jobKey = jobInstanceDao.getJobInstanceKey(newParameters);
        jobInstanceDao.updateJobKey(instance, jobKey);
    }
}
