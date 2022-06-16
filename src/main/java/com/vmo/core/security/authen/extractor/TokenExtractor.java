package com.vmo.core.security.authen.extractor;

import javax.servlet.http.HttpServletRequest;

public interface TokenExtractor {
    String extract(HttpServletRequest request);
}
