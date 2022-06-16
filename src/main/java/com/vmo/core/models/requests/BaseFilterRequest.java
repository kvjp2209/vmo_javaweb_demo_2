package com.vmo.core.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmo.core.models.requests.filter.FilterField;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.util.List;

@Data
public abstract class BaseFilterRequest<T extends IFilterField> implements Filterable {

    private Boolean allPage;
    @JsonIgnore
    private Pageable pageable;
    @Valid
    private List<FilterFieldRequest<T>> fields;

    @JsonIgnore
    public abstract String getDefaultSort();

    public void setPageable(Pageable pageable) {
        setPageable(pageable, this.allPage);
    }

    public void setPageable(Pageable pageable, Boolean allPage) {
        this.pageable = Boolean.TRUE.equals(allPage) ? new AllDataPageRequest() : pageable;
    }
}
