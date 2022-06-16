package com.vmo.core.common.config.env;

import lombok.Getter;

public enum Environments {
    LOCAL("Local"),
    UNIT_TEST("Unit test"),
    DEVELOPMENT("Development"),
    STAGING("Staging"),
    UAT("User Acceptance Testing"),
    BETA("Beta"),
    PRODUCTION("Production");

    @Getter
    String value;

    Environments(String value) {
        this.value = value;
    }
}
