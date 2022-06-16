package com.vmo.core.models.responses;

import com.vmo.core.common.error.ApiException;
import com.vmo.core.common.error.ErrorCode;
import com.vmo.core.common.messages.DefaultMessageCodes;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class PaginationResponse<T> implements DefaultMessageCodes {
    private List<T> data;
    private long totalItem;
    private int page;
    private int pageSize;
    private int totalPage;

    @Deprecated
    /**
     * Only for json mapper
     * Dont use for creating API response
     */
    public PaginationResponse() {
    }

    public PaginationResponse(Pageable pageable) {
        this(pageable.isPaged() ? pageable.getPageNumber() : 0,
                pageable.isPaged() ? pageable.getPageSize() : -1,
                false, pageable.isPaged());
    }

    public PaginationResponse(Pageable pageable, boolean createDefaultList) {
        this(pageable.isPaged() ? pageable.getPageNumber() : 0,
                pageable.isPaged() ? pageable.getPageSize() : -1,
                createDefaultList, pageable.isPaged());
    }

    public PaginationResponse(int page, int pageSize) {
        this(page, pageSize, false);
    }

    public PaginationResponse(int page, int pageSize, boolean createDefaultList) {
        this(page, pageSize, createDefaultList, true);
    }

    private PaginationResponse(int page, int pageSize, boolean createDefaultList, boolean validatePageSize) {
        page++;

        if (page < 1) {
            throw new ApiException(
                    ErrorCode.INVALID_PARAM,
                    PAGE_NUMBER_NOT_POSITIVE
            );
        }
        if (validatePageSize && pageSize < 1) {
            throw new ApiException(
                    ErrorCode.INVALID_PARAM,
                    PAGE_SIZE_NOT_POSITIVE
            );
        }
        this.page = page;
        this.pageSize = pageSize;
        this.data = createDefaultList ? new ArrayList<>() : this.data;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(long totalItem) {
        this.totalItem = totalItem;
        if (pageSize > 0 && totalItem > 0) {
            totalPage = (int) Math.ceil((float) totalItem / pageSize);
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        if (pageSize > 0 && totalItem > 0) {
            totalPage = (int) Math.ceil((float) totalItem / pageSize);
        }
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
