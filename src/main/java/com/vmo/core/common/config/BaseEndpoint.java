package com.vmo.core.common.config;

public class BaseEndpoint {
    public final static String GENERAL_API_PREFIX = "/api";
    public final static String PRIVATE_API_PREFIX = "/private/api";

    public final static String GENERAL_API_PATTERN = GENERAL_API_PREFIX + "/**";
    public final static String PRIVATE_API_PATTERN = PRIVATE_API_PREFIX + "/**";
}
