package com.vmo.core.integration.slack;

import com.slack.api.model.Attachment;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.TextObject;
import com.slack.api.webhook.Payload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmo.core.common.config.CommonConfig;
import com.vmo.core.common.config.SlackWebhookConfig;
import com.vmo.core.common.utils.action.ActionResult;
import com.vmo.core.models.responses.ErrorResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class SlackWebhookService {
    private final String colorRed = "#FF0000";
    private final String colorYellow = "#FFFF00";
    private final String colorGreen = "#00FF00";
    private final String colorBlue = "#0000FF";

    private CommonConfig commonConfig;
    private SlackWebhookConfig slackWebhookConfig;
    private MySlackApi slackApi;

    private LocalDateTime lastAlertTime;
    private LocalDateTime startCycleTime;
    @Getter
    private int totalClientErrors;
    @Getter @Setter
    private int serverErrors;
    private Integer lastReportedClientErrors;
    private Integer lastReportedServerErrors;

    private Map<String, Integer> clientErrorApi = new HashMap<>();

    @Autowired
    public SlackWebhookService(CommonConfig commonConfig, SlackWebhookConfig slackWebhookConfig, ObjectMapper objectMapper) {
        this.commonConfig = commonConfig;
        this.slackWebhookConfig = slackWebhookConfig;
        startCycleTime = LocalDateTime.now();
        slackApi = new MySlackApi(slackWebhookConfig.getWebhookUrl(), objectMapper);
    }

    @Async
    public void alertErrorAsync(ActionResult callback) {
        alertErrorSync(false, callback);
    }

    private synchronized void alertErrorSync(boolean ignoreCycle, ActionResult callback) {
        if (!isEnable()) {
            return;
        }

        //nothing to alert
        if (totalClientErrors < 1 && serverErrors < 1) {
            if (callback != null) {
                callback.call();
            }
            return;
        }

        boolean urgent = false;
        if (slackWebhookConfig.getClientErrorAlert() != null
                && slackWebhookConfig.getClientErrorAlert() <= totalClientErrors
        ) {
            urgent = true;
        }
        if (slackWebhookConfig.getServerErrorAlert() != null
                && slackWebhookConfig.getServerErrorAlert() <= serverErrors
        ) {
            urgent = true;
        }
        if (!urgent) {
            if (serverErrors < 1) {
                //only alert server errors if its not urgent
                return;
            }
            if (!ignoreCycle && !isNewCycle()) {
                return;
            }
        }

        //dont spam too much
        if (!isNewCycle()) { //if its new cycle, its ok to report for one more time for previous cycle
            //if error increase too fast, its ok to quickly report
            boolean forced = false;
            if (urgent && (lastReportedClientErrors != null || lastReportedServerErrors != null)) {
                if (lastReportedClientErrors != null && slackWebhookConfig.getClientErrorAlert() != null
                        && (totalClientErrors - lastReportedClientErrors) >= slackWebhookConfig.getClientErrorAlert()
                        && (totalClientErrors / (float)lastReportedClientErrors) >= 1.4f
                ) {
                    forced = true;
                }
                if (lastReportedServerErrors != null && slackWebhookConfig.getServerErrorAlert() != null
                        && (serverErrors - lastReportedServerErrors) >= slackWebhookConfig.getServerErrorAlert()
                        && (serverErrors / (float)lastReportedServerErrors) >= 1.4f
                ) {
                    forced = true;
                }
            }
            if (!forced && lastAlertTime != null && slackWebhookConfig.getDelaySecondsErrorAlert() != null) {
                if (Seconds.secondsBetween(lastAlertTime, LocalDateTime.now()).getSeconds() < slackWebhookConfig.getDelaySecondsErrorAlert()) {
                    return;
                }
            }
        }

        List<Attachment> attachments = new ArrayList<>();
        if (urgent) {
            String reviewerText = "Reviewer: ";
            if (slackWebhookConfig.getReviewerIds() != null && !slackWebhookConfig.getReviewerIds().isEmpty()) {
                boolean isFirst = true;
                for (String userId : slackWebhookConfig.getReviewerIds()) {
                    if (isFirst) {
                        isFirst = false;
                        reviewerText += "<@" + userId + ">";
                    } else {
                        reviewerText += ", <@" + userId + ">";
                    }
                }
            }

            Attachment attachmentReviewer = Attachment.builder().text(reviewerText).color(colorBlue).build();
            attachments.add(attachmentReviewer);
        }

        String apis = "";
        if (totalClientErrors >= slackWebhookConfig.getClientErrorAlert()) {
            apis = clientErrorApi.keySet().stream()
                    .filter(api -> (clientErrorApi.get(api) * 1f) / slackWebhookConfig.getClientErrorAlert() >= 0.05)
                    .sorted(Comparator.comparingInt(api -> -clientErrorApi.get(api)))
                    .limit(Math.min(5, Math.floorDiv(totalClientErrors, slackWebhookConfig.getClientErrorAlert()) * 2))
                    .map(api -> api + " - " + clientErrorApi.get(api) + " error(s)")
                    .collect(Collectors.joining(" \n"));
            if (StringUtils.isNotBlank(apis)) {
                apis = " \n" +
                        "Top client error: \n" +
                        apis;
            }
        }
        String errorInfoText = "Errors during " + getDuringCycle() + " \n"
                + totalClientErrors + " client error(s) and " + serverErrors + " server error(s)"
                + apis;
        Attachment attachmentErrorInfo = Attachment.builder().text(errorInfoText).color(colorRed).build();
        attachments.add(attachmentErrorInfo);

        sendWithAttachment(attachments);
        lastAlertTime = LocalDateTime.now();
        lastReportedClientErrors = totalClientErrors;
        lastReportedServerErrors = serverErrors;

        if (callback != null) {
            callback.call();
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void alertReady() {
        try {
            if (!isEnable()) {
                return;
            }

            Attachment attachment = Attachment.builder().text("Service is ready").color(colorGreen).build();
            sendWithAttachment(Collections.singletonList(attachment));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void alertShutdown() {
        try {
            if (!isEnable()) {
                return;
            }

            try {
                alertErrorSync(true, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Attachment attachment = Attachment.builder().text("Service shutdown").color(colorYellow).build();
            sendWithAttachment(Collections.singletonList(attachment));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isEnable() {
        if (slackWebhookConfig != null
                && slackWebhookConfig.getIsEnable() != null
        ) {
            return slackWebhookConfig.getIsEnable();
        }

        return false;
    }

    private String getDuringCycle() {
        if (startCycleTime == null) return null;

        LocalDateTime endTime = null;

        if (slackWebhookConfig == null
                || slackWebhookConfig.getNotificationCycleSeconds() == null
        ) {
            endTime = LocalDateTime.now();
        } if (isNewCycle()) {
            endTime = startCycleTime.plusSeconds(slackWebhookConfig.getNotificationCycleSeconds());
        } else {
            endTime = LocalDateTime.now();
        }

        String timeFormat = "yyyy-MM-dd HH:mm";

        return startCycleTime.toString(timeFormat) + " - " + endTime.toString(timeFormat);
    }

    public boolean isNewCycle() {
        if (slackWebhookConfig != null
                && slackWebhookConfig.getNotificationCycleSeconds() != null
        ) {
            if (startCycleTime == null) {
                startCycleTime = LocalDateTime.now();
            }

            long cycle = slackWebhookConfig.getNotificationCycleSeconds();

            if (Seconds.secondsBetween(startCycleTime, LocalDateTime.now()).getSeconds() > cycle) {
                return true;
            }
        }

        return false;
    }

    public LocalDateTime getStartCycleTime() {
        return startCycleTime;
    }

    public void setStartCycleTime(LocalDateTime startCycleTime) {
        this.startCycleTime = startCycleTime;
    }

    private String serviceLabel() {
        return "*Service*: " + commonConfig.getService();
    }

    private String environmentLabel() {
        return "*Environment*: " + commonConfig.getEnv().getValue();
    }

    public void increaseClientError(HttpServletRequest request, ErrorResponse e) {
        totalClientErrors++;
        String api = request.getMethod() + " - "
                + request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) + " - "
                + e.getErrorCode().getCode();
        if (!clientErrorApi.containsKey(api)) {
            clientErrorApi.put(api, 0);
        }
        clientErrorApi.put(api, clientErrorApi.get(api) + 1);
    }

    public void resetCounter() {
        totalClientErrors = 0;
        serverErrors = 0;
        lastReportedClientErrors = null;
        lastReportedServerErrors = null;
        clientErrorApi.clear();
        startCycleTime = LocalDateTime.now();
    }

    public void sendWithAttachment(List<Attachment> attachments) {
        List<TextObject> texts = new ArrayList<>();
        TextObject service = MarkdownTextObject.builder().text(serviceLabel()).build();
        TextObject environment = MarkdownTextObject.builder().text(environmentLabel()).build();
        texts.add(service);
        texts.add(environment);

        SectionBlock section = new SectionBlock();
        section.setFields(texts);

        List<LayoutBlock> blocks = new ArrayList<>();
        blocks.add(section);
        blocks.add(new DividerBlock());
        Payload payload = Payload.builder().blocks(blocks).attachments(attachments).build();

        slackApi.send(payload);
    }
}
