package com.vmo.core.common.utils.action;

@FunctionalInterface
public interface ActionInput<T> {
    void call(T t);
}
