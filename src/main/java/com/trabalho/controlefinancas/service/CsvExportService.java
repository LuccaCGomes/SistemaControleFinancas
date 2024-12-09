package com.trabalho.controlefinancas.service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
public class CsvExportService {

    public byte[] generateCsv(List<String[]> data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            for (String[] record : data) {
                csvPrinter.printRecord(record);
            }

            csvPrinter.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }
}
