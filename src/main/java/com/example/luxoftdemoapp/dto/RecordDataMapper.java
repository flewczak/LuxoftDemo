package com.example.luxoftdemoapp.dto;

import com.example.luxoftdemoapp.entity.RecordData;

public class RecordDataMapper {

    public static RecordDataDto mapToRecordDataDto(RecordData recordData) {
        return RecordDataDto.builder()
                .primaryKey(recordData.getPrimaryKey())
                .description(recordData.getDescription())
                .name(recordData.getName())
                .updatedTimestamp(recordData.getUpdatedTimestamp())
                .build();
    }

    public static RecordData mapToRecordData(RecordDataDto s) {
        RecordData recordData = new RecordData();
        recordData.setPrimaryKey(s.getPrimaryKey());
        recordData.setName(s.getName());
        recordData.setDescription(s.getDescription());
        recordData.setUpdatedTimestamp(s.getUpdatedTimestamp());
        return recordData;
    }


    public static void updateRecordData(RecordData recordData, RecordDataDto dto) {
        recordData.setName(dto.getName());
        recordData.setDescription(dto.getDescription());
        recordData.setUpdatedTimestamp(dto.getUpdatedTimestamp());
    }
}
