//package com.vmo.core.scheduler.job;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.vmo.core.scheduler.step.RepeatableStepHandler;
//import lombok.Getter;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobInterruptedException;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.StartLimitExceededException;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.StepExecution;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.job.SimpleJob;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.Trigger;
//import org.springframework.scheduling.support.CronExpression;
//import org.springframework.scheduling.support.CronSequenceGenerator;
//import org.springframework.scheduling.support.CronTrigger;
//import org.springframework.scheduling.support.PeriodicTrigger;
//
//import javax.annotation.PostConstruct;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public abstract class BaseStepJob<T extends BaseJobParameters> extends SimpleJob implements BaseJob<T> {
//    @Getter
//    private Class<T> paramsModel;
//    private List<Step> steps = new ArrayList<>();
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private RepeatableStepHandler stepHandler;
//    @Autowired
//    protected StepBuilderFactory stepBuilderFactory;
//    @Autowired
//    private JobRepository jobRepository;
//
//    public BaseStepJob(Class<T> paramsModel) {
//        this.paramsModel = paramsModel;
//    }
//
//    @PostConstruct
//    public void setBeans() {
//        setJobRepository(jobRepository);
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        addSteps(steps);
//        setSteps(steps);
//
//        super.afterPropertiesSet();
//    }
//
//    @Override
//    public String getName() {
//        return getClass().getSimpleName();
//    }
//
//    @Override
//    public T convertParameters(JobParameters jobParameters) {
//        return objectMapper.convertValue(jobParameters.toProperties(), getParamsModel());
//    }
//
//    @Override
//    public Trigger buildTrigger(T jobParameters) {
//        switch (jobParameters.getRepeatType()) {
//            case FIX_DELAY:
//                if (jobParameters.getWaitSeconds() != null) {
//                    return new PeriodicTrigger(jobParameters.getWaitSeconds(), TimeUnit.SECONDS);
//                }
//                break;
//            case FIX_RATE:
//                if (jobParameters.getWaitSeconds() != null) {
//                    PeriodicTrigger trigger = new PeriodicTrigger(jobParameters.getWaitSeconds(), TimeUnit.SECONDS);
//                    trigger.setFixedRate(true);
//
//                    return trigger;
//                }
//                break;
//            case CRON:
//                if (!StringUtils.isBlank(jobParameters.getCron()) &&
//                        CronSequenceGenerator.isValidExpression(jobParameters.getCron())
//                ) {
//                    return new CronTrigger(jobParameters.getCron());
//                }
//                break;
//        }
//        return null;
//    }
//
//    protected void doExecute(JobExecution execution) throws JobInterruptedException, JobRestartException, StartLimitExceededException {
//        //TODO store status running for job instance in RAM
//
//        try {
//            StepExecution stepExecution = null;
//
//            for (Step step : steps) {
//                stepExecution = executeStep(step, execution);
//                if (stepExecution.getStatus() != BatchStatus.COMPLETED) {
//                    break;
//                }
//            }
//
//            if (stepExecution != null) {
//                if (logger.isDebugEnabled()) {
//                    logger.debug("Upgrading JobExecution status: " + stepExecution);
//                }
//
//                execution.upgradeStatus(stepExecution.getStatus());
//                execution.setExitStatus(stepExecution.getExitStatus());
//            }
//
//        } finally {
//            //TODO set job not running
//        }
//    }
//
//    private StepExecution executeStep(
//            Step step, JobExecution execution
//    ) throws JobInterruptedException, JobRestartException, StartLimitExceededException {
//        return stepHandler.handleStep(step, execution);
//    }
//}
