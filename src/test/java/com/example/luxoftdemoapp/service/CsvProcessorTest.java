package com.example.luxoftdemoapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import com.example.luxoftdemoapp.dto.RecordDataDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CsvProcessorTest {

    static CsvProcessor csvProcessor;

    @BeforeAll
    static void setUp() {
        csvProcessor = new CsvProcessorImpl();
    }

    @Test
    void happyPath() throws IOException {
        CsvProcessorImpl.Result result = process("happyPath.csv");
        List<RecordDataDto> recordDataDtos = result.getRecordDataDtos();
        List<CsvProcessor.ErrorForLine> csvExceptions = result.getCsvExceptions();

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, recordDataDtos.size()),
                () -> Assertions.assertEquals(0, csvExceptions.size()),
                () -> Assertions.assertEquals("123", recordDataDtos.get(0).getPrimaryKey()),
                () -> Assertions.assertEquals("nameValue1", recordDataDtos.get(0).getName()),
                () -> Assertions.assertEquals("descValue1", recordDataDtos.get(0).getDescription()),
                () -> Assertions.assertEquals(LocalDateTime.of(2019,9,7,15,50,0), recordDataDtos.get(0).getUpdatedTimestamp())
        );
    }


    @Test
    void missingPrimaryKey() throws IOException {
        CsvProcessor.Result result = process("missingPrimaryKey.csv");
        List<RecordDataDto> recordDataDtos = result.getRecordDataDtos();
        List<CsvProcessor.ErrorForLine> csvExceptions = result.getCsvExceptions();

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, recordDataDtos.size()),
                () -> Assertions.assertEquals(1, csvExceptions.size()),
                () -> Assertions.assertEquals(2, csvExceptions.get(0).getLineNumber()),
                () -> Assertions.assertEquals("Field 'primaryKey' is mandatory but no value was provided.", csvExceptions.get(0).getMessage())
        );
    }

    @Test
    void invalidDateFormat() throws IOException {
        CsvProcessor.Result result = process("invalidDateFormat.csv");
        List<RecordDataDto> recordDataDtos = result.getRecordDataDtos();
        List<CsvProcessor.ErrorForLine> csvExceptions = result.getCsvExceptions();

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, recordDataDtos.size()),
                () -> Assertions.assertEquals(1, csvExceptions.size()),
                () -> Assertions.assertEquals(2, csvExceptions.get(0).getLineNumber()),
                () -> Assertions.assertEquals("Parsing failed due to mismatch of datatype", csvExceptions.get(0).getMessage())
        );
    }

    @Test
    void duplicatedPrimaryKey() throws IOException {
        CsvProcessor.Result result = process("duplicatedPrimaryKey.csv");
        List<RecordDataDto> recordDataDtos = result.getRecordDataDtos();
        List<CsvProcessor.ErrorForLine> csvExceptions = result.getCsvExceptions();

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, recordDataDtos.size()),
                () -> Assertions.assertEquals(1, csvExceptions.size()),
                () -> Assertions.assertEquals(-1, csvExceptions.get(0).getLineNumber()),
                () -> Assertions.assertEquals("Duplicated value for PRIMARY_KEY = 123. Invalid records were not processed", csvExceptions.get(0).getMessage())
        );
    }

    @Test
    void processEvenWithErrors() throws IOException {
        CsvProcessor.Result result = process("withErrors.csv");
        List<RecordDataDto> recordDataDtos = result.getRecordDataDtos();
        List<CsvProcessor.ErrorForLine> csvExceptions = result.getCsvExceptions();

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, recordDataDtos.size()),
                () -> Assertions.assertEquals(2, csvExceptions.size()),
                () -> Assertions.assertEquals(4, csvExceptions.get(0).getLineNumber()),
                () -> Assertions.assertEquals(5, csvExceptions.get(1).getLineNumber()),
                () -> Assertions.assertEquals("Parsing failed due to mismatch of datatype", csvExceptions.get(0).getMessage()),
                () -> Assertions.assertEquals("Field 'primaryKey' is mandatory but no value was provided.", csvExceptions.get(1).getMessage())
        );
    }

    private CsvProcessor.Result process(String fileName) throws IOException {
        InputStream inputStream = loadFile(fileName);
        return csvProcessor.process(inputStream);
    }


    private InputStream loadFile(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }
}