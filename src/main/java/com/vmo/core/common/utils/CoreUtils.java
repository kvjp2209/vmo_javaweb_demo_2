package com.vmo.core.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmo.core.common.CommonConstants;
import com.vmo.core.common.error.ApiException;
import com.vmo.core.common.error.ErrorCode;
import com.vmo.core.common.error.SystemException;
import com.vmo.core.common.utils.action.ActionMappingJsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CoreUtils {
    private static Boolean isSpringApplication;


    public static boolean isSpringApplication() {
        return isSpringApplication != null ? isSpringApplication : false;
    }

    public static void setSpringApplication(boolean isSpringApplication) {
        if (CoreUtils.isSpringApplication == null) {
            CoreUtils.isSpringApplication = isSpringApplication;
        } else if (!Objects.equals(CoreUtils.isSpringApplication, isSpringApplication)) {
            throw new SystemException(ErrorCode.UNSATISFIED_CONDITION, "Can not change value of isSpringApplication");
        }
    }

    /**
     *
     * @param instance any instance of a class in the main application
     * @return
     */
    public static String getRootApplicationFolder(Object instance) {
        return getRootApplicationFolder(instance.getClass());
    }

    /**
     *
     * @param classz any class of main application
     * @return
     */
    public static String getRootApplicationFolder(Class classz) {
        String rootClassPath = classz.getProtectionDomain().getCodeSource().getLocation().toString();
        String rootAppFolder;

        if (rootClassPath.contains(".jar")) {
            String jarPath = rootClassPath.substring(0, rootClassPath.indexOf(".jar") + 4); //remove folders inside .jar file
            rootAppFolder = jarPath.substring(0, jarPath.lastIndexOf("/") + 1) //remove .jar file
                    .replace("jar:", ""); //remove scheme
        } else {
            rootAppFolder = rootClassPath;
        }

        rootAppFolder = rootAppFolder.replace("file:", "");

        return rootAppFolder;
    }

    public static Class getRealBeanClass(Object bean) {
        Class[] interfaces = bean.getClass().getInterfaces();
//        if (any contains EnhancedConfiguration)
        if (bean.getClass().getSimpleName().contains("EnhancerBySpring") && bean.getClass().getSuperclass() != null) {
            return bean.getClass().getSuperclass();
        }

        return AopUtils.getTargetClass(bean);
//        return bean.getClass();
    }


    public static String getBackendHost(HttpServletRequest request) {
        StringBuilder host = new StringBuilder();
        String headerScheme = request.getHeader("x-forwarded-proto");
        if (StringUtils.isNotBlank(headerScheme)) {
            host.append(headerScheme);
        } else {
            host.append(request.getScheme());
        }
        host.append("://");
        host.append(request.getServerName());
        if (request.getServerPort() != 80) {
            host.append(":");
            host.append(request.getServerPort());
        }
        String apiPath = (String)request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (!request.getRequestURI().equalsIgnoreCase(apiPath)) {
            String servletContextPath = request.getRequestURI().replace(apiPath, "");
            if (StringUtils.isNotBlank(servletContextPath)) {
                host.append(servletContextPath);
            }
        }

        return host.toString();
    }

    public static String getClientHost(HttpServletRequest request) {
        String referer = request.getHeader("referer");
        if (StringUtils.isNotBlank(referer)) return referer;

        String origin = request.getHeader("origin");
        if (StringUtils.isNotBlank(origin)) return origin;

        return request.getRemoteAddr();
    }

    public static String getClientIP(HttpServletRequest request) {
        String clientIP = null;
        String headerXForward = request.getHeader(CommonConstants.HEADER_PROXY_FORWARD_IP);
        if (StringUtils.isNotBlank(headerXForward)) {
            if (headerXForward.contains(",")) {
                clientIP = headerXForward.split(",")[0];
            } else {
                clientIP = headerXForward;
            }
        } else {
            clientIP = request.getRemoteHost();
        }
        return clientIP;
    }

    public static String addQueryParam(String url, String paramName, Object paramValue) {
        return addQueryParam(url, paramName, paramValue, false);
    }

    public static String addQueryParam(String url, String paramName, Object paramValue, boolean encode) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(paramName, paramValue);
        UriComponents uri = builder.build();
        if (encode) {
            uri = uri.encode();
        }

        return uri.toUriString();
    }

    public static String addQueryParams(String url, Map<String, Object> params) {
        return addQueryParams(url, params, false);
    }

    public static String addQueryParams(String url, Map<String, Object> params, boolean encode) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (params != null && !params.isEmpty()) {
            for (String param : params.keySet()) {
                builder.queryParam(param, params.get(param));
            }
        }

        UriComponents uri = builder.build();
        if (encode) {
            uri = uri.encode();
        }

        return uri.toUriString();
    }

    /**
     * Example: addPathVariable("http://localhost/resources/{id}", "id", "hello world")
     * @param url
     * @param pathKey
     * @param pathValue
     * @return
     */
    public static String addPathVariable(String url, String pathKey, Object pathValue) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .build()
                .expand(Collections.singletonMap(pathKey, pathValue))
                .encode()
                .toUriString();
    }

    public static String addPathVariables(String url, Map<String, Object> uriParams) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .build()
                .expand(uriParams)
                .encode()
                .toUriString();
    }

    public static boolean needUpdate(Object param, Object current) {
        return param != null && !Objects.equals(param, current);
    }

    public static <T> List<T> filterParamValue(List<T> params) {
        return params.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public static <T> Set<T> filterParamValue(Set<T> params) {
        return params.stream()
                .filter(Objects::nonNull)
//                .distinct() //Collectors.toSet use HashSet so no need distinct()
                .collect(Collectors.toSet());
    }

    public static <T> T warpJsonException(ActionMappingJsonResult<T> action) {
        try {
            return action.map();
        } catch (JsonProcessingException e) {
            throw new ApiException(e, ErrorCode.INVALID_FORMAT_TYPE, e.getMessage());
        }
    }
}
