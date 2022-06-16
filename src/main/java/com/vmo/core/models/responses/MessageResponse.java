package com.vmo.core.models.responses;

import lombok.Getter;

public class MessageResponse {
    @Getter
    private String message;

    public MessageResponse() {}

    public MessageResponse(String message) {
        this.message = message;
    }
}
