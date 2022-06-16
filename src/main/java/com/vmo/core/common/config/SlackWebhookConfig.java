package com.vmo.core.common.config;

import com.vmo.core.common.CommonConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = CommonConstants.CONFIG_PREFIX + ".common.slack-webhook")
@Data
public class SlackWebhookConfig {
    private Boolean isEnable;
    private String webhookUrl;
    //instant alert if errors exceed this limit
    private Integer clientErrorAlert;
    private Integer serverErrorAlert;
    //if doesnt exceed, send notification for each cycle of:
    private Integer notificationCycleSeconds;
    private Integer delaySecondsErrorAlert;
    private List<String> reviewerIds;
}
