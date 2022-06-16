package com.vmo.core.models.requests;

import com.vmo.core.models.requests.filter.FilterField;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFilterField {

    FilterField getField();
}
