package com.vmo.core.common.messages;

public interface DefaultMessageCodes {
    //TODO refactor use message code
    String UNKOWN_SERVER_ERROR = "error.server.unknown";

    //core error codes
    String ERROR_CODE_DUPLICATED = "error.code.duplicated";
    String ERROR_CODE_REQUIRED = "error.code.required";

    //message locale
    String ERROR_MESSAGE_CODE_NOT_EMPTY = "error.message.code.not-empty";

    //pagination
    String PAGE_NUMBER_NOT_POSITIVE = "error.api.paging.page_index_not_positive";
    String PAGE_SIZE_NOT_POSITIVE = "error.api.paging.page_size_not_positive";
}
