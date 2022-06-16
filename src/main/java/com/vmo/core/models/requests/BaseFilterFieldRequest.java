package com.vmo.core.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmo.core.models.requests.filter.FilterField;
import com.vmo.core.models.requests.filter.FilterValue;
import com.vmo.core.models.requests.sort.SortDirection;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public abstract class BaseFilterFieldRequest<T> {
    @NotNull
    private T field;
    private FilterValue filter;
    private SortDirection sortDirection;

    @JsonIgnore
    public abstract FilterField getFilterField();
}
