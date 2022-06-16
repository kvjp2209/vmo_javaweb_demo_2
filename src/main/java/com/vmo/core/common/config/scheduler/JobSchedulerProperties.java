package com.vmo.core.common.config.scheduler;

import com.vmo.core.common.CommonConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = CommonConstants.CONFIG_SCHEDULER)
public class JobSchedulerProperties {
    private boolean enabled;
    private Integer poolSize = 10;
    /**
     * delay before run on application start up
     */
    //TODO
    private Integer delaySecond;
    /**
     * restart from unknown status or old job was running but halt unexpectedly
     */
    private boolean forceRestart = false;
    private int maxExitMessageLength = 2500;
}
