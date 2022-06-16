package com.vmo.core.models.requests;

import com.vmo.core.models.requests.filter.FilterField;
import lombok.Data;

@Data
public class FilterFieldRequest<T extends IFilterField> extends BaseFilterFieldRequest<T> {

    @Override
    public FilterField getFilterField() {
        return getField().getField();
    }
}
