package com.vmo.core.common.utils;

import com.vmo.core.models.database.types.job.JobRepeatType;
import com.vmo.core.scheduler.job.BaseJobParameters;

public class SchedulerUtils {
    public static boolean canRepeat(BaseJobParameters jobParameter) {
        return jobParameter != null && jobParameter.isRepeatable();
    }

    public static boolean shouldAutoRun(BaseJobParameters jobParameter) {
        return jobParameter != null && jobParameter.isEnabled()
                && jobParameter.isRepeatable();
    }
}
