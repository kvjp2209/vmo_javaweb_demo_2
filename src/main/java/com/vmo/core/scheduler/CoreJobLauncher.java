//package com.vmo.core.scheduler;
//
//import com.vmo.core.common.config.scheduler.JobSchedulerProperties;
//import com.vmo.core.common.utils.SchedulerUtils;
//import com.vmo.core.scheduler.job.BaseJob;
//import com.vmo.core.scheduler.job.BaseJobParameters;
//import com.vmo.core.scheduler.job.JobExecutor;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.ExitStatus;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersInvalidException;
//import org.springframework.batch.core.StepExecution;
//import org.springframework.batch.core.converter.DefaultJobParametersConverter;
//import org.springframework.batch.core.converter.JobParametersConverter;
//import org.springframework.batch.core.launch.support.SimpleJobLauncher;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.core.task.TaskRejectedException;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.ScheduledFuture;
//
//@Component
//public class CoreJobLauncher extends SimpleJobLauncher {
//    private JobRepository jobRepository;
//    private JobSchedulerProperties jobSchedulerProperties;
//    /**
//     * job instance id - schedule
//     */
//    private static ConcurrentMap<Long, ScheduledFuture> queuedJobs = new ConcurrentHashMap<>();
//    private JobParametersConverter jobParametersConverter = new DefaultJobParametersConverter();
//
//    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
//
//
//    public CoreJobLauncher(JobRepository jobRepository, JobSchedulerProperties jobSchedulerProperties) {
//        setJobRepository(jobRepository);
//        this.jobSchedulerProperties = jobSchedulerProperties;
//
//        threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
//        threadPoolTaskScheduler.setPoolSize(jobSchedulerProperties.getPoolSize());
//        threadPoolTaskScheduler.setThreadNamePrefix("JobsThreadPool-");
////        threadPoolTaskScheduler.setErrorHandler(jobFailHandler);
//        threadPoolTaskScheduler.initialize();
//        setTaskExecutor(threadPoolTaskScheduler);
//    }
//
//    public void setJobRepository(JobRepository jobRepository) {
//        this.jobRepository = jobRepository;
//        super.setJobRepository(jobRepository);
//    }
//
//
//    public JobExecution start(
//            Job job, JobParameters jobParameters
//    ) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException{
//        if (job instanceof BaseJob) {
//            BaseJobParameters baseParams = ((BaseJob) job).convertParameters(jobParameters);
//            if (baseParams.isRepeatable()) {
//                return scheduleRun((BaseJob) job, jobParameters, baseParams, true);
//            }
//        }
//
//        return super.run(job, jobParameters);
//    }
//
//    @Override
//    public JobExecution run(
//            Job job, JobParameters jobParameters
//    ) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
//        if (job instanceof BaseJob) {
//            BaseJobParameters baseParams = ((BaseJob) job).convertParameters(jobParameters);
//            if (baseParams.isRepeatable()) {
//                return scheduleRun((BaseJob) job, jobParameters, baseParams, false);
//            }
//        }
//
//        return super.run(job, jobParameters);
//    }
//
//    public JobExecution scheduleRun(
//            BaseJob job, JobParameters jobParameters, BaseJobParameters baseParams, boolean firstStart
//    ) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
//        Assert.notNull(job, "The Job must not be null.");
//        Assert.notNull(jobParameters, "The JobParameters must not be null.");
//        JobExecution lastExecution = this.jobRepository.getLastJobExecution(job.getName(), jobParameters);
//        if (lastExecution != null) {
//            if (!job.isRestartable()) {
//                throw new JobRestartException("JobInstance already exists and is not restartable");
//            }
//
//            for (StepExecution execution : lastExecution.getStepExecutions()) {
//                BatchStatus status = execution.getStatus();
//
//                if (!SchedulerUtils.canRepeat(baseParams)) {
//                    if (status.isRunning() || status == BatchStatus.STOPPING) {
//                        throw new JobExecutionAlreadyRunningException("A job execution for this job is already running: " + lastExecution);
//                    }
//
//                    if (status == BatchStatus.UNKNOWN) {
//                        throw new JobRestartException("Cannot restart step [" + execution.getStepName() + "] from UNKNOWN status. The last execution ended with a failure that could not be rolled back, so it may be dangerous to proceed. Manual intervention is probably necessary.");
//                    }
//                }
//            }
//        }
//
//        job.getJobParametersValidator().validate(jobParameters);
//
//        final JobExecution jobExecution = this.jobRepository.createJobExecution(job.getName(), jobParameters);
//
//        try {
//            if (firstStart && !SchedulerUtils.shouldAutoRun(baseParams)) {
//                jobExecution.setStatus(BatchStatus.ABANDONED);
//                jobExecution.setExitStatus(ExitStatus.NOOP);
//
//                jobRepository.update(jobExecution);
//            } else {
//                JobExecutor executor = new JobExecutor(jobSchedulerProperties, job, jobParameters, jobExecution);
//                ScheduledFuture scheduled = threadPoolTaskScheduler.schedule(executor, executor);
//                queuedJobs.put(jobExecution.getJobId(), scheduled);
//            }
//        } catch (TaskRejectedException e) {
//            jobExecution.upgradeStatus(BatchStatus.FAILED);
//            if (jobExecution.getExitStatus().equals(ExitStatus.UNKNOWN)) {
//                jobExecution.setExitStatus(ExitStatus.FAILED.addExitDescription(e));
//            }
//
//            this.jobRepository.update(jobExecution);
//        }
//
//        return jobExecution;
//    }
//}
