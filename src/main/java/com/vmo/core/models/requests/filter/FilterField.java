package com.vmo.core.models.requests.filter;

import com.vmo.core.common.utils.tuple.Pair;
import lombok.Getter;

@Getter
public class FilterField {
    private FilterFieldFormat format;
    private String column;
    private boolean sortable;

    public FilterField() {}

    public FilterField(FilterFieldFormat format, String column, boolean sortable) {
        this.format = format;
        this.column = column;
        this.sortable = sortable;
    }
}
