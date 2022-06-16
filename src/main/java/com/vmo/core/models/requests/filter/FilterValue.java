package com.vmo.core.models.requests.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.vmo.core.common.error.ApiException;
import com.vmo.core.common.error.ErrorCode;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Data
public class FilterValue {
    private FilterComparisonOperator comparison;
    private Object simpleValue;
    @Setter(onMethod_ = { @JsonIgnore } )
    private FilterValue groupValue;

    @Setter(onMethod_ = { @JsonIgnore } )
    private FilterLogicalOperator nextOperator;
    @Setter(onMethod_ = { @JsonIgnore } )
    private FilterValue nextValue;


    //setter are for json creator only. use constructor for manual creating

    public FilterValue() {}

    public FilterValue(FilterComparisonOperator comparison, Object simpleValue) {
        this.comparison = comparison;
        this.simpleValue = simpleValue;
    }

    public FilterValue(FilterComparisonOperator comparison, FilterValue group, FilterLogicalOperator nextOperator, FilterValue nextValue) {
        this.comparison = comparison;
        this.groupValue = group;
        this.nextOperator = nextOperator;
        this.nextValue = nextValue;
    }

    public void setComparison(FilterComparisonOperator comparison) {
        this.comparison = comparison;

        if (groupValue != null) {
            throw new ApiException(ErrorCode.INVALID_PARAM, "Can only use either simple value or group value");
        }

        if (this.comparison != null && simpleValue != null) {
            groupValue = new FilterValue(comparison, simpleValue);
        }
    }

    public void setSimpleValue(Object simpleValue) {
        this.simpleValue = simpleValue != null && StringUtils.isNotBlank(simpleValue.toString()) ? simpleValue : null;

        if (groupValue != null) {
            throw new ApiException(ErrorCode.INVALID_PARAM, "Can only use either simple value or group value");
        }

        if (this.simpleValue != null && comparison != null) {
            groupValue = new FilterValue(comparison, simpleValue);
        }
    }

    @JsonSetter("group")
    public void group(FilterValueRecursive group) {
        if (this.groupValue != null) {
            throw new ApiException(ErrorCode.INVALID_PARAM, "Can only use either simple value or group value");
        }

        this.groupValue = group;
    }

    @JsonSetter("and")
    public void and(FilterValueRecursive filter) {
        nextOperator = FilterLogicalOperator.AND;

        if (nextValue != null) {
            throw new ApiException(ErrorCode.INVALID_PARAM, "Can only use either group {and} or {or}");
        }
        nextValue = filter;
    }

    @JsonSetter("or")
    public void or(FilterValueRecursive filter) {
        nextOperator = FilterLogicalOperator.OR;

        if (nextValue != null) {
            throw new ApiException(ErrorCode.INVALID_PARAM, "Can only use either group {and} or {or}");
        }
        nextValue = filter;
    }
}
