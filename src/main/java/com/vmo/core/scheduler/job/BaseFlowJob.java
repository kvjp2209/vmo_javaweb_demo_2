package com.vmo.core.scheduler.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseFlowJob<T extends BaseJobParameters> extends FlowJob implements BaseJob {
    @Autowired
    private ObjectMapper objectMapper;

    private Class<T> paramsModel;

    public BaseFlowJob(Class<T> paramsModel) {
        this.paramsModel = paramsModel;
    }

    @Override
    public Class<T> getParamsModel() {
        return paramsModel;
    }

    @Override
    public T convertParameters(JobParameters jobParameters) {
        return objectMapper.convertValue(jobParameters.toProperties(), getParamsModel());
    }
}
