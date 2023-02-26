package com.example.luxoftdemoapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.luxoftdemoapp.dto.RecordDataDto;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
public class CsvProcessorImpl implements CsvProcessor {

    public Result process(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            CsvToBean<RecordDataDto> csvToBean = buildCsvToBean(reader);

            List<RecordDataDto> recordDataDtoList = csvToBean.parse();
            List<ErrorForLine> capturedExceptions = mapErrors(csvToBean);

            Set<String> duplicatedKeys = findDuplicatedKeys(recordDataDtoList);
            List<RecordDataDto> withOutDuplicates = filterOutDuplicates(recordDataDtoList, duplicatedKeys);

            List<ErrorForLine> duplicatedKeysErrors = createDuplicatedKeysErrors(duplicatedKeys);
            capturedExceptions.addAll(duplicatedKeysErrors);

            return new Result(withOutDuplicates, capturedExceptions);
        }
    }

    private static CsvToBean<RecordDataDto> buildCsvToBean(Reader reader) {
        return new CsvToBeanBuilder<RecordDataDto>(reader)
                .withType(RecordDataDto.class)
                .withIgnoreLeadingWhiteSpace(true)
                .withThrowExceptions(false)
                .build();
    }

    private static List<ErrorForLine> createDuplicatedKeysErrors(Set<String> duplicatedKeys) {
        return duplicatedKeys.stream()
                .map(CsvProcessorImpl::toDuplicatedPrimaryKeyError)
                .collect(Collectors.toList());
    }

    private static ErrorForLine toDuplicatedPrimaryKeyError(String key) {
        return new ErrorForLine(-1L, "Duplicated value for PRIMARY_KEY = " + key + ". Invalid records were not processed");
    }

    private static List<RecordDataDto> filterOutDuplicates(List<RecordDataDto> recordDataDtoList, Set<String> duplicatedKeys) {
        return recordDataDtoList
                .stream()
                .filter(s -> !duplicatedKeys.contains(s.getPrimaryKey()))
                .collect(Collectors.toList());
    }

    public static Set<String> findDuplicatedKeys(List<RecordDataDto> list) {
        Set<String> items = new HashSet<>();
        return list.stream()
                .map(RecordDataDto::getPrimaryKey)
                .filter(key -> !items.add(key))
                .collect(Collectors.toSet());

    }

    private static List<ErrorForLine> mapErrors(CsvToBean<RecordDataDto> csvToBean) {
        return csvToBean.getCapturedExceptions()
                .stream()
                .map(CsvProcessorImpl::mapError)
                .collect(Collectors.toList());
    }

    private static ErrorForLine mapError(CsvException e) {
        if (e instanceof CsvDataTypeMismatchException) {
            log.warning("Parsing for line " + e.getLineNumber() + " failed due to: " + e.getCause().getMessage());
            return new ErrorForLine(e.getLineNumber(), "Parsing failed due to mismatch of datatype");
        } else {
            return new ErrorForLine(e.getLineNumber(), e.getMessage());
        }
    }


}
