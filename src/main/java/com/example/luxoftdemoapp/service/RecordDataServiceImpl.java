package com.example.luxoftdemoapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.luxoftdemoapp.entity.RecordData;
import com.example.luxoftdemoapp.dto.RecordDataDto;
import com.example.luxoftdemoapp.dto.RecordDataMapper;
import com.example.luxoftdemoapp.repository.RecordDataRepository;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log
public class RecordDataServiceImpl implements RecordDataService {
    private final RecordDataRepository recordDataRepository;
    private final CsvProcessor csvProcessor;

    @Override
    public List<CsvProcessor.ErrorForLine> saveAll(InputStream inputStream) throws IOException {
        CsvProcessor.Result result = csvProcessor.process(inputStream);

        Map<String, RecordDataDto> lookupMap = populateLookupMap(result);
        Set<String> primaryKeyList = lookupMap.keySet();

        Map<String, RecordData> fromDbMap = findAllById(primaryKeyList);
        List<RecordData> toSave = populateAndUpdateRecordData(lookupMap, fromDbMap);

        recordDataRepository.saveAll(toSave);
        return result.getCsvExceptions();
    }

    private static List<RecordData> populateAndUpdateRecordData(
            Map<String, RecordDataDto> lookupMap,
            Map<String, RecordData> fromDbMap
    ) {
        return lookupMap.entrySet()
                .stream()
                .map(entry -> toRecordData(entry.getKey(), entry.getValue(), fromDbMap))
                .collect(Collectors.toList());
    }

    private static RecordData toRecordData(String key, RecordDataDto recordDataDto, Map<String, RecordData> fromDbMap) {
        if (fromDbMap.containsKey(key)) {
            RecordData recordData = fromDbMap.get(key);
            RecordDataMapper.updateRecordData(recordData, recordDataDto);
            return recordData;
        } else {
            return RecordDataMapper.mapToRecordData(recordDataDto);
        }
    }

    private Map<String, RecordData> findAllById(Set<String> primaryKeyList) {
        List<RecordData> fetchedRecordData = recordDataRepository.findAllById(primaryKeyList);
        return fetchedRecordData.stream().collect(Collectors.toMap(RecordData::getPrimaryKey, Function.identity()));
    }

    private static Map<String, RecordDataDto> populateLookupMap(CsvProcessor.Result result) {
        return result.getRecordDataDtos()
                .stream()
                .collect(Collectors.toMap(RecordDataDto::getPrimaryKey, Function.identity()));
    }

    @Override
    public Optional<RecordDataDto> findByPrimaryKey(String key) {
        return recordDataRepository.findById(key)
                .map(RecordDataMapper::mapToRecordDataDto);
    }

    @Override
    public Either<String, String> deleteByPrimaryKey(String key) {
        try {
            recordDataRepository.deleteById(key);
            return Either.right("success");
        } catch (EmptyResultDataAccessException e) {
            return Either.left("Entry is not present, so it cannot be deleted");
        } catch (RuntimeException e) {
            return Either.left("Unexpected error occurred");
        }
    }


}
