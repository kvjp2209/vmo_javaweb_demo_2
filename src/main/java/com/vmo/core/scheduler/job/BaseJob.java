package com.vmo.core.scheduler.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.scheduling.Trigger;

import java.util.List;

public interface BaseJob<T extends BaseJobParameters> extends Job {
    Class<T> getParamsModel();

    T convertParameters(JobParameters jobParameters);

    Trigger buildTrigger(T jobParameters);

    void addSteps(List<Step> steps);
}
