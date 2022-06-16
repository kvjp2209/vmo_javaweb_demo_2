package com.vmo.core.models.requests;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class AllDataPageRequest implements Pageable {
    private Sort sort;

    public AllDataPageRequest() {
        this(Sort.unsorted());
    }

    public AllDataPageRequest(Sort sort) {
        this.sort = sort;
    }


    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public int getPageNumber() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPageSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getOffset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return this;
    }

    @Override
    public Pageable previousOrFirst() {
        return this;
    }

    @Override
    public Pageable first() {
        return this;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        if (pageNumber == 0) {
            return this;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
