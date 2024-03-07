package com.pass.passbatch.util;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.util.List;

@Slf4j
public class CustomCSVWriter {

    /**
     * CSV 파일을 생성합니다.
     *
     * @param fileName 파일 이름
     * @param data     CSV 파일에 쓸 데이터
     * @return 생성된 행의 수
     */
    public static int write(final String fileName, List<String[]> data) {
        int rows = 0;
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeAll(data);
            rows = data.size();
        } catch (Exception e) {
            log.error("CustomCSVWriter - write: CSV 파일 생성 실패, fileName: {}", fileName);

        }
        return rows;

    }
}
