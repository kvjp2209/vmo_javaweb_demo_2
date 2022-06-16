package com.vmo.core.integration;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpGetRequest extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "GET";

    public HttpGetRequest() {
    }

    public HttpGetRequest(URI uri) {
        this.setURI(uri);
    }

    public HttpGetRequest(String uri) {
        this.setURI(URI.create(uri));
    }

    public String getMethod() {
        return "GET";
    }
}
