package com.vmo.core.services.log;

import com.vmo.core.models.database.entities.log.LogInboundApi;
import com.vmo.core.models.responses.PaginationResponse;
import org.springframework.data.domain.Pageable;

public interface LogInboundApiService {
    PaginationResponse<LogInboundApi> getLogs(Pageable pageable);
}
