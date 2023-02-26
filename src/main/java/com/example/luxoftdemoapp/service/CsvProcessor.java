package com.example.luxoftdemoapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.example.luxoftdemoapp.dto.RecordDataDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface CsvProcessor {

    Result process(InputStream inputStream) throws IOException;

    @RequiredArgsConstructor
    @Getter
    @ToString
    public static class Result {
        private final List<RecordDataDto> recordDataDtos;
        private final List<CsvProcessor.ErrorForLine> csvExceptions;
    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    public static class ErrorForLine {
        private final Long lineNumber;
        private final String message;
    }
}
