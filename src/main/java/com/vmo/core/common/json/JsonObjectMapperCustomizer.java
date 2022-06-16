package com.vmo.core.common.json;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@FunctionalInterface
public interface JsonObjectMapperCustomizer {
    void customize(ObjectMapper objectMapper, List<Module> registeringModules);
}
