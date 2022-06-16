package com.vmo.core.common.utils.action;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ActionMappingJsonResult<T> {
    T map() throws JsonProcessingException;
}
