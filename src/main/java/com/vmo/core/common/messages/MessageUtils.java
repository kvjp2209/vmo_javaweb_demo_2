package com.vmo.core.common.messages;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Lazy(value = false)
public class MessageUtils {
    private static MessageSource messageSource = null;

    @Autowired
    public MessageUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public static String getMessage(String messageCode, Object... messageArguments) {
        return getMessage(Locale.getDefault(), messageCode, messageArguments);
    }

    public static String getMessage(Locale locale, String messageCode, Object... messageArguments) {
        return messageSource == null
                ? messageCode
                : messageSource.getMessage(messageCode, messageArguments, locale);
    }

    public static MessageData parse(String content, Object... messageArguments) {
        return parse(Locale.getDefault(), content, messageArguments);
    }

    public static MessageData parse(Locale locale, String content, Object... messageArguments) {
        String message = MessageUtils.getMessage(locale, content, messageArguments);
        if (StringUtils.isNotBlank(message) && !message.equals(content)) {
            return new MessageData(message, content);
        }
        return new MessageData(content);
    }
}
