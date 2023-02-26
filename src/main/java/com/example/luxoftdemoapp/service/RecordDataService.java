package com.example.luxoftdemoapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.example.luxoftdemoapp.dto.RecordDataDto;
import io.vavr.control.Either;

public interface RecordDataService {

    List<CsvProcessor.ErrorForLine> saveAll(InputStream inputStream) throws IOException;

    Optional<RecordDataDto> findByPrimaryKey(String key);

    Either<String,String> deleteByPrimaryKey(String key);
}
