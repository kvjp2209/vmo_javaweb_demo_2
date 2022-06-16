package com.vmo.core.common.utils.action;

@FunctionalInterface
public interface ActionResult<T> {
    T call();
}
