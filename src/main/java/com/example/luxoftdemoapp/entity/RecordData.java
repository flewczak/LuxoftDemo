package com.example.luxoftdemoapp.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "record_data")
@Getter
@Setter
@EqualsAndHashCode
public class RecordData {
    @Id
    private String primaryKey;

    private String name;
    private String description;
    private LocalDateTime updatedTimestamp;

}
