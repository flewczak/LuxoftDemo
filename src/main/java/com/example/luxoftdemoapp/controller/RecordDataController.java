package com.example.luxoftdemoapp.controller;

import com.example.luxoftdemoapp.service.CsvProcessor;
import com.example.luxoftdemoapp.service.RecordDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/recordData")
@RequiredArgsConstructor
public class RecordDataController {

    private final RecordDataService recordDataService;

    @PostMapping("/importFromFile")
    public ResponseEntity<? extends Response> uploadFile(@RequestParam("file") MultipartFile file) {
        List<CsvProcessor.ErrorForLine> errorForLines;
        try {
            errorForLines = recordDataService.saveAll(file.getInputStream());
            if (errorForLines.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response(200));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(400, "Failed to parse file"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ErrorListResponse(200, errorForLines));
    }

    @GetMapping("/{key}")
    public ResponseEntity<? extends Response> findByKey(@PathVariable String key) {
        return recordDataService.findByPrimaryKey(key)
                .<ResponseEntity<? extends Response>>map(dto -> ResponseEntity.status(HttpStatus.OK).body(new FindByKeyResponse(200, dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(404, "not found")));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<? extends Response> delete(@PathVariable String key) {
        return recordDataService.deleteByPrimaryKey(key)
                .<ResponseEntity<? extends Response>>map(o -> ResponseEntity.status(HttpStatus.OK).body(new DeleteSuccessResponse(200)))
                .getOrElseGet(o -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(404, "not found")));
    }


}
