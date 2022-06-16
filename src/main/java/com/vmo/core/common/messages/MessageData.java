package com.vmo.core.common.messages;

import lombok.Data;

@Data
public class MessageData {
    private String message;
    private String messageCode;

    public MessageData(String message) {
        this.message = message;
    }

    public MessageData(String message, String messageCode) {
        this.message = message;
        this.messageCode = messageCode;
    }
}
