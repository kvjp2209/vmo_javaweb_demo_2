package com.vmo.core.models.requests.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FilterComparisonOperator {
    EQUAL(FilterFieldFormat.values()),
    NOT_EQUAL(FilterFieldFormat.values()),
    CONTAINS(new FilterFieldFormat[] { FilterFieldFormat.STRING }),
    NOT_CONTAINS(new FilterFieldFormat[] { FilterFieldFormat.STRING }),
    START_WITH(new FilterFieldFormat[] { FilterFieldFormat.STRING }),
    END_WITH(new FilterFieldFormat[] { FilterFieldFormat.STRING }),
    GREATER(new FilterFieldFormat[] { FilterFieldFormat.NUMBER, FilterFieldFormat.TIME }),
    GREATER_OR_EQUAL(new FilterFieldFormat[] { FilterFieldFormat.NUMBER, FilterFieldFormat.TIME }),
    LESS(new FilterFieldFormat[] { FilterFieldFormat.NUMBER, FilterFieldFormat.TIME }),
    LESS_OR_EQUAL(new FilterFieldFormat[] { FilterFieldFormat.NUMBER, FilterFieldFormat.TIME }),
    IN_LIST(new FilterFieldFormat[] { FilterFieldFormat.STRING, FilterFieldFormat.NUMBER, FilterFieldFormat.BOOLEAN});

    @Getter
    private FilterFieldFormat[] applicableFormats;
}
