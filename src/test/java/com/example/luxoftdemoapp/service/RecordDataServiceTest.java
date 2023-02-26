package com.example.luxoftdemoapp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.example.luxoftdemoapp.dto.RecordDataDto;
import com.example.luxoftdemoapp.entity.RecordData;
import com.example.luxoftdemoapp.repository.RecordDataRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class RecordDataServiceTest {
    CsvProcessor csvProcessor;
    RecordDataRepository recordDataRepository;
    RecordDataService recordDataService;

    @BeforeEach
    void setUp() {
        this.recordDataRepository = Mockito.mock(RecordDataRepository.class);
        this.csvProcessor = Mockito.mock(CsvProcessor.class);
        this.recordDataService = new RecordDataServiceImpl(recordDataRepository, csvProcessor);
    }

    @Test
    void toInsertIfNotInDb() throws IOException {
        RecordDataDto dto = populateDto();
        when(csvProcessor.process(any())).thenReturn(new CsvProcessor.Result(List.of(dto), List.of(createError())));

        recordDataService.saveAll(null);

        List<RecordData> toSave = captureSavedElements();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, toSave.size()),
                () -> Assertions.assertEquals(dto.getPrimaryKey(), toSave.get(0).getPrimaryKey()),
                () -> Assertions.assertEquals(dto.getName(), toSave.get(0).getName()),
                () -> Assertions.assertEquals(dto.getDescription(), toSave.get(0).getDescription()),
                () -> Assertions.assertEquals(dto.getUpdatedTimestamp(), toSave.get(0).getUpdatedTimestamp())
        );
    }

    @Test
    void toUpdateIfInDb() throws IOException {
        RecordDataDto dto = populateDto();
        when(csvProcessor.process(any())).thenReturn(new CsvProcessor.Result(List.of(dto), List.of(createError())));
        when(recordDataRepository.findAllById(any())).thenReturn(List.of(populateEntity()));

        recordDataService.saveAll(null);

        List<RecordData> toSave = captureSavedElements();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, toSave.size()),
                () -> Assertions.assertEquals(dto.getPrimaryKey(), toSave.get(0).getPrimaryKey()),
                () -> Assertions.assertEquals(dto.getName(), toSave.get(0).getName()),
                () -> Assertions.assertEquals(dto.getDescription(), toSave.get(0).getDescription()),
                () -> Assertions.assertEquals(dto.getUpdatedTimestamp(), toSave.get(0).getUpdatedTimestamp())
        );
    }

    private static CsvProcessor.ErrorForLine createError() {
        return new CsvProcessor.ErrorForLine(3L, "error");
    }

    private List<RecordData> captureSavedElements() {
        ArgumentCaptor<List<RecordData>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(recordDataRepository).saveAll(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    private static RecordDataDto populateDto() {
        RecordDataDto dto = new RecordDataDto();
        dto.setPrimaryKey("primaryKeyValue");
        dto.setName("nameValue");
        dto.setDescription("descriptionValue");
        dto.setUpdatedTimestamp(LocalDateTime.of(2019, 9, 7, 15, 50, 0));
        return dto;
    }

    private static RecordData populateEntity() {
        RecordData entity = new RecordData();
        entity.setPrimaryKey("primaryKeyValue");
        entity.setName("nameValue");
        entity.setDescription("descriptionValue");
        entity.setUpdatedTimestamp(LocalDateTime.of(2019, 9, 7, 15, 50, 0));
        return entity;
    }
}