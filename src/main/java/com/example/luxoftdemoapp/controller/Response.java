package com.example.luxoftdemoapp.controller;


import java.util.List;

import com.example.luxoftdemoapp.dto.RecordDataDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Response {
    private final int responseCode;

}

@Getter
class FindByKeyResponse extends Response {
    private final RecordDataDto dto;

    public FindByKeyResponse(int responseCode, RecordDataDto dto) {
        super(responseCode);
        this.dto = dto;
    }
}

@Getter
class DeleteSuccessResponse extends Response {

    public DeleteSuccessResponse(int responseCode) {
        super(responseCode);
    }
}

@Getter
class ErrorResponse extends Response {
    private final String error;

    public ErrorResponse(int responseCode, String error) {
        super(responseCode);
        this.error = error;
    }
}

@Getter
class ErrorListResponse extends Response {
    private final List<?> error;

    public ErrorListResponse(int responseCode, List<?> error) {
        super(responseCode);
        this.error = error;
    }
}

