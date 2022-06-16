//package com.vmo.core.scheduler.step;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.vmo.core.scheduler.job.BaseJobParameters;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.StartLimitExceededException;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.StepExecution;
//import org.springframework.batch.core.job.SimpleStepHandler;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RepeatableStepHandler extends SimpleStepHandler {
//    private static final Log LOG = LogFactory.getLog(RepeatableStepHandler.class);
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    private JobRepository jobRepository;
//
//    public RepeatableStepHandler(JobRepository jobRepository) {
//        super(jobRepository);
//        this.jobRepository = jobRepository;
//    }
//
//    @Override
//    protected boolean shouldStart(StepExecution lastStepExecution, JobExecution jobExecution, Step step) throws JobRestartException, StartLimitExceededException {
//        BatchStatus stepStatus;
//        if (lastStepExecution == null) {
//            stepStatus = BatchStatus.STARTING;
//        } else {
//            stepStatus = lastStepExecution.getStatus();
//        }
//
//        if (stepStatus == BatchStatus.UNKNOWN) {
//            throw new JobRestartException("Cannot restart step from UNKNOWN status. The last execution ended with a failure that could not be rolled back, so it may be dangerous to proceed. Manual intervention is probably necessary.");
//        } else if ((stepStatus != BatchStatus.COMPLETED || step.isAllowStartIfComplete()) && stepStatus != BatchStatus.ABANDONED) {
//            if (this.jobRepository.getStepExecutionCount(jobExecution.getJobInstance(), step.getName()) < step.getStartLimit()) {
//                return true;
//            } else {
//                throw new StartLimitExceededException("Maximum start limit exceeded for step: " + step.getName() + "StartMax: " + step.getStartLimit());
//            }
//        } else {
//            JobParameters jobParameters = jobExecution.getJobParameters();
//            if (jobParameters != null && !jobParameters.isEmpty()) {
//                BaseJobParameters baseJobParameters = objectMapper.convertValue(jobParameters.toProperties(), BaseJobParameters.class);
//                if (baseJobParameters.isRepeatable()) {
//                    return true;
//                }
//            }
//
//            if (LOG.isInfoEnabled()) {
//                LOG.info("Step already complete or not restartable, so no action to execute: " + lastStepExecution);
//            }
//
//            return false;
//        }
//    }
//}
