package com.vmo.core.repositories.scheduler;

import org.springframework.batch.core.DefaultJobKeyGenerator;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobKeyGenerator;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.dao.JdbcJobInstanceDao;

public class JpaJobInstanceRepository extends JdbcJobInstanceDao {
    private static final String UPDATE_JOB_KEY_QUERY = "UPDATE %PREFIX%JOB_INSTANCE SET JOB_KEY = ?, VERSION = ? WHERE JOB_INSTANCE_ID = ? ";

    private JobKeyGenerator<JobParameters> jobKeyGenerator = new DefaultJobKeyGenerator();

    public String getJobInstanceKey(JobParameters jobParameters) {
        return jobKeyGenerator.generateKey(jobParameters);
    }

    public void updateJobKey(JobInstance jobInstance, String jobKey) {
        jobInstance.incrementVersion();

        this.getJdbcTemplate().update(
                this.getQuery(UPDATE_JOB_KEY_QUERY),
                jobKey, jobInstance.getVersion(), jobInstance.getInstanceId()
        );
    }
}
