package com.vmo.core.common;

import java.util.Arrays;
import java.util.List;

public interface CommonConstants {
    //configuration
    String CONFIG_PREFIX = "core";
    String CONFIG_COMMON = CONFIG_PREFIX + ".common";
    String CONFIG_ASYNC = CONFIG_PREFIX + ".async";
    String CONFIG_WEB = CONFIG_PREFIX + ".web";
    String CONFIG_API_DOC = CONFIG_PREFIX + ".api-doc";
    String CONFIG_SCHEDULER = CONFIG_PREFIX + ".scheduler";


    String HEADER_BASIC_AUTH_NAME = "Authorization";
    String HEADER_BASIC_AUTH_PREFIX = "Basic ";
    String HEADER_AUTH_PREFIX = "Bearer ";
    String ROLE_PREFIX = "ROLE_";
    String HEADER_PROXY_FORWARD_IP = "x-forwarded-for";


    List<String> cacheableBodyContentTypes = Arrays.asList(
            "json",
            "xml",
            "text",
            "xhtml"
    );


    //time in miliseconds
    long SECOND = 1000L;
    long MINUTE = SECOND * 60;
    long HOUR = MINUTE * 60;
    long DAY = HOUR * 24;
    long WEEK = DAY * 7;


    String FORMAT_DATE_TIME_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

}
