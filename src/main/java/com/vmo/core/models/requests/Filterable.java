package com.vmo.core.models.requests;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface Filterable {
    List<? extends BaseFilterFieldRequest> getFields();

    Pageable getPageable();
}
