package com.vmo.core.scheduler.job;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmo.core.models.database.types.job.JobRepeatType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class BaseJobParameters {
    @JsonAlias("enabled")
    private boolean isEnabled;
    private JobRepeatType repeatType;
    private Long waitSeconds;
    private String cron;

    @JsonIgnore
    public boolean isRepeatable() {
        return repeatType != null && !JobRepeatType.NONE.equals(repeatType)
                && (JobRepeatType.CRON.equals(repeatType) ? StringUtils.isNotBlank(cron) : waitSeconds != null && waitSeconds > 0);
    }
}
