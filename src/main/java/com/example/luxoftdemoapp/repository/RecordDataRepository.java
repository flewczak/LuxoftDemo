package com.example.luxoftdemoapp.repository;

import com.example.luxoftdemoapp.entity.RecordData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordDataRepository extends JpaRepository<RecordData,String> {
}
