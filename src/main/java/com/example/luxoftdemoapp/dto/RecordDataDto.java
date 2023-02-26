package com.example.luxoftdemoapp.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RecordDataDto {
    @CsvBindByName(column = "PRIMARY_KEY", required = true)
    private String primaryKey;
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String description;
    @CsvBindByName(column = "UPDATED_TIMESTAMP")
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTimestamp;


}
