package com.vmo.core.common.utils;

import com.vmo.core.security.context.IUserContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CoreSecurityUtils {
    public static boolean isLogined() {
        return SecurityContextHolder.getContext() != null &&
                SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof IUserContext;
    }

    public static String getUserLogin() {
        if (isLogined()) {
            return ((IUserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        }
        return null;
    }

    public static List<String> getUserRoles () {
        if (isLogined()) {
            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            List<String> roles = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(authorities)) {
                for (GrantedAuthority authority : authorities) {
                    roles.add(authority.getAuthority());
//                    CommonConstants.ROLE_PREFIX
                }
            }
            return roles;
        }
        return null;
    }
}
