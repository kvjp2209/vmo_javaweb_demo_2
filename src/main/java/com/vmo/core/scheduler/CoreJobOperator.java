//package com.vmo.core.scheduler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.vmo.core.common.config.scheduler.JobSchedulerProperties;
//import com.vmo.core.common.utils.SchedulerUtils;
//import com.vmo.core.models.database.types.job.JobRepeatType;
//import com.vmo.core.repositories.scheduler.JpaJobRepository;
//import com.vmo.core.scheduler.annotation.JobSetting;
//import com.vmo.core.scheduler.exception.WrappedJobException;
//import com.vmo.core.scheduler.job.BaseJob;
//import com.vmo.core.scheduler.job.BaseJobParameters;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.joda.time.DateTime;
//import org.joda.time.LocalDateTime;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobInstance;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersInvalidException;
//import org.springframework.batch.core.UnexpectedJobExecutionException;
//import org.springframework.batch.core.configuration.DuplicateJobException;
//import org.springframework.batch.core.configuration.JobFactory;
//import org.springframework.batch.core.configuration.JobRegistry;
//import org.springframework.batch.core.configuration.ListableJobLocator;
//import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
//import org.springframework.batch.core.converter.DefaultJobParametersConverter;
//import org.springframework.batch.core.converter.JobParametersConverter;
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.launch.NoSuchJobException;
//import org.springframework.batch.core.launch.support.SimpleJobOperator;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.batch.support.PropertiesConverter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
//@Component("jobOperator")
//public class CoreJobOperator extends SimpleJobOperator {
//    private CoreJobLauncher jobLauncher;
//    private JobRegistry jobRegistry;
//    private JobExplorer jobExplorer;
//    private JpaJobRepository jobRepository;
//    private JobParametersConverter jobParametersConverter = new DefaultJobParametersConverter();
//    private final Log LOG = LogFactory.getLog(this.getClass());
//    private boolean loadedJobs = false;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private JobSchedulerProperties jobSchedulerProperties;
//    @Autowired(required = false)
//    private List<Job> jobs;
//
//    public CoreJobOperator(CoreJobLauncher jobLauncher, JobRegistry jobRegistry, JobExplorer jobExplorer, JpaJobRepository jobRepository) {
//        this.jobLauncher = jobLauncher;
//        this.jobRegistry = jobRegistry;
//        this.jobExplorer = jobExplorer;
//        this.jobRepository = jobRepository;
//        setJobLauncher(jobLauncher);
//        setJobRegistry(jobRegistry);
//        setJobExplorer(jobExplorer);
//        setJobRepository(jobRepository);
//    }
//
////    @Autowired
////    @Override
////    public void setJobRegistry(ListableJobLocator jobRegistry) {
////        this.jobRegistry = jobRegistry;
////        super.setJobRegistry(jobRegistry);
////    }
////
////    @Autowired
////    @Override
////    public void setJobExplorer(JobExplorer jobExplorer) {
////        this.jobExplorer = jobExplorer;
////        super.setJobExplorer(jobExplorer);
////    }
////
////    @Autowired
////    @Override
////    public void setJobRepository(JobRepository jobRepository) {
////        this.jobRepository = jobRepository;
////        super.setJobRepository(jobRepository);
////    }
//
//    public void setJobParametersConverter(JobParametersConverter jobParametersConverter) {
//        this.jobParametersConverter = jobParametersConverter;
//        super.setJobParametersConverter(jobParametersConverter);
//    }
//
//    @EventListener(ApplicationReadyEvent.class)
//    @Transactional
//    protected void loadJobs() {
//        if (!jobSchedulerProperties.isEnabled()) return;
//
//        if (jobs != null) {
//            for (Job job : jobs) {
//                if (job instanceof BaseJob) {
//                    loadJob((BaseJob) job);
//                }
//            }
//        }
//        loadedJobs = true;
//    }
//
//    private List<JobInstance> loadJob(BaseJob job) {
//        String name = job.getName(); //getJobName(job.getClass());
//
//        try {
//            int count = jobExplorer.getJobInstanceCount(name);
//            if (count > 0) {
//                List<JobInstance> instances = jobExplorer.getJobInstances(name, 0, count);
//                for (JobInstance instance : instances) {
//                    JobExecution lastExecution = jobExplorer.getLastJobExecution(instance);
//                    JobExecution executionDetail = jobExplorer.getJobExecution(lastExecution.getId());
//
//                    //check if params were changed -> update job instance instead of letting it create new instance
//                    if (!jobRepository.isJobInstanceExists(name, executionDetail.getJobParameters())) {
//                        jobRepository.updateJobInstanceKey(instance, executionDetail.getJobParameters());
//                    }
//
//                    BaseJobParameters jobParameters = job.convertParameters(executionDetail.getJobParameters());
//                    if (SchedulerUtils.shouldAutoRun(jobParameters)) {
//                        jobLauncher.run(job, executionDetail.getJobParameters());
//                    }
//                }
//            } else {
//                JobSetting setting = job.getClass().getAnnotation(JobSetting.class);
//                BaseJobParameters baseParameters = new BaseJobParameters();
//                baseParameters.setEnabled(setting.enableByDefault());
//                baseParameters.setRepeatType(setting.defaultTimeType());
//                baseParameters.setWaitSeconds(setting.defaultWaitSeconds());
//                baseParameters.setCron(StringUtils.defaultIfBlank(setting.defaultCron(), null));
//
//                Map<String, Object> mapProperties = objectMapper.convertValue(baseParameters, Map.class);
////                mapProperties.entrySet().forEach(entry -> entry.setValue(entry.getValue().toString()));
//                Properties properties = new Properties();
//                //TODO move to new converter
//                mapProperties.entrySet().forEach(entry -> {
//                    Object value = entry.getValue();
//                    String key = entry.getKey().equals("enabled") ? "-enabled" : entry.getKey();
//
//                    if (value instanceof Double || value instanceof Float) {
////                        properties.put(entry.getKey() + "(double)", ((Number) value).doubleValue());
//                        properties.put(key + "(double)", value.toString());
////                        DefaultJobParametersConverter.DATE_TYPE is Date with only day, without time
////                    } else if (value instanceof LocalDateTime) {
////                        properties.put(entry.getKey() + DefaultJobParametersConverter.DATE_TYPE, ((LocalDateTime) value).toDate());
////                    } else if (value instanceof DateTime) {
////                        properties.put(entry.getKey() + DefaultJobParametersConverter.DATE_TYPE, ((DateTime) value).toDate());
////                    } else if (value instanceof Date) {
////                        properties.put(entry.getKey() + DefaultJobParametersConverter.DATE_TYPE, value);
//                    } else if (value instanceof Long || value instanceof Integer) {
////                        properties.put(entry.getKey() + DefaultJobParametersConverter.LONG_TYPE, ((Number) value).longValue());
//                        properties.put(key + DefaultJobParametersConverter.LONG_TYPE, value.toString());
//                    } else {
//                        properties.put(key, value.toString());
//                    }
//                });
//
//                jobRegistry.register(new ReferenceJobFactory(job));
//                start(job.getName(), properties);
//            }
//        } catch (NoSuchJobException e) {
//            throw new WrappedJobException(e, "Could not find job");
//        } catch (DuplicateJobException e) { //register
//            throw new WrappedJobException(e);
//        } catch (JobInstanceAlreadyExistsException | JobParametersInvalidException e) { //create in DB
//            throw new WrappedJobException(e);
//        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException e) { //run
//            throw new WrappedJobException(e);
//        }
//
//        return null;
//    }
//
//    /**
//     * same as SimpleJobOperator.start() but use "Properties parameters"
//     * @param jobName
//     * @param parameters
//     * @return
//     * @throws NoSuchJobException
//     * @throws JobInstanceAlreadyExistsException
//     * @throws JobParametersInvalidException
//     */
//    protected JobExecution start(
//            String jobName, Properties parameters
//    ) throws NoSuchJobException, JobInstanceAlreadyExistsException, JobParametersInvalidException {
//        if (LOG.isInfoEnabled()) {
//            LOG.info("Checking status of job with name=" + jobName);
//        }
//
//        JobParameters jobParameters = this.jobParametersConverter.getJobParameters(parameters);
//        if (this.jobRepository.isJobInstanceExists(jobName, jobParameters)) {
//            throw new JobInstanceAlreadyExistsException(String.format("Cannot start a job instance that already exists with name=%s and parameters=%s", jobName, parameters));
//        } else {
//            Job job = this.jobRegistry.getJob(jobName);
//            if (LOG.isInfoEnabled()) {
//                LOG.info(String.format("Attempting to launch job with name=%s and parameters=%s", jobName, parameters));
//            }
//
//            try {
//                return this.jobLauncher.start(job, jobParameters);
//            } catch (JobExecutionAlreadyRunningException var6) {
//                throw new UnexpectedJobExecutionException(String.format("Illegal state (only happens on a race condition): %s with name=%s and parameters=%s", "job execution already running", jobName, parameters), var6);
//            } catch (JobRestartException var7) {
//                throw new UnexpectedJobExecutionException(String.format("Illegal state (only happens on a race condition): %s with name=%s and parameters=%s", "job not restartable", jobName, parameters), var7);
//            } catch (JobInstanceAlreadyCompleteException var8) {
//                throw new UnexpectedJobExecutionException(String.format("Illegal state (only happens on a race condition): %s with name=%s and parameters=%s", "job already complete", jobName, parameters), var8);
//            }
//        }
//    }
//
//    private String getJobName(Class<? extends Job> classz) {
//        return classz.getSimpleName();
//    }
//}
