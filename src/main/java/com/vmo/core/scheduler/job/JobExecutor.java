package com.vmo.core.scheduler.job;

import com.vmo.core.common.config.scheduler.JobSchedulerProperties;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.metrics.BatchMetrics;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.time.Duration;
import java.util.Date;

public class JobExecutor implements Runnable, Trigger {
    private static Logger LOG = LoggerFactory.getLogger(JobExecutor.class);

    private JobSchedulerProperties jobSchedulerProperties;
    private BaseJob job;
    private JobParameters batchJobParameters;
    private BaseJobParameters baseJobParameters;
    private JobExecution jobExecution;

    private Trigger internalTrigger;

    public JobExecutor(
            JobSchedulerProperties jobSchedulerProperties,
            BaseJob job, JobParameters batchJobParameters,
            JobExecution jobExecution
    ) {
        this.jobSchedulerProperties = jobSchedulerProperties;
        this.job = job;
        this.batchJobParameters = batchJobParameters;
        this.baseJobParameters = job.convertParameters(batchJobParameters);
        this.jobExecution = jobExecution;
    }


    @Override
    public final Date nextExecutionTime(TriggerContext triggerContext) {
        if (internalTrigger == null) {
            internalTrigger = job.buildTrigger(baseJobParameters);
        }

        Date nextRunAt = internalTrigger.nextExecutionTime(triggerContext);

        if (triggerContext.lastActualExecutionTime() == null //first run
                && jobSchedulerProperties.getDelaySecond() != null
                && jobSchedulerProperties.getDelaySecond() > 0
        ) {
            nextRunAt = LocalDateTime.fromDateFields(nextRunAt)
                    .plusSeconds(jobSchedulerProperties.getDelaySecond())
                    .toDate();
        }
        return nextRunAt;
    }

    public void run() {
        try {
            LOG.info("Job: [" + job + "] launched with the following parameters: [" + batchJobParameters + "]");

            job.execute(jobExecution);

            Duration jobExecutionDuration = BatchMetrics.calculateDuration(jobExecution.getStartTime(), jobExecution.getEndTime());
            LOG.info("Job: [" + job + "] completed with the following parameters: [" + batchJobParameters + "] and the following status: [" + jobExecution.getStatus() + "]" + (jobExecutionDuration == null ? "" : " in " + BatchMetrics.formatDuration(jobExecutionDuration)));
        } catch (Throwable t) {
            LOG.info("Job: [" + job + "] failed unexpectedly and fatally with the following parameters: [" + batchJobParameters + "]", t);

            this.rethrow(t);
        }

    }

    private void rethrow(Throwable t) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        } else {
            throw new IllegalStateException(t);
        }
    }
}
