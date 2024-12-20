package com.trabalho.controlefinancas.service;
import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.TransactionRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.io.OutputStreamWriter;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;

import static java.lang.Long.parseLong;

@Service
public class CsvService {
    @Autowired
    CategoryService categoryService = new CategoryService();

    @Autowired
    TransactionService transactionService = new TransactionService();

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

    public void processCSV(MultipartFile file, User user) throws Exception {


        List<Transaction> transactions = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("ID", "Tipo", "Descrição", "Valor", "Data", "Categoria", "É Recorrente?")
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : records) {
                String id = record.get("ID");
                String type = record.get("Tipo");
                String description = record.get("Descrição");
                String amount = record.get("Valor");
                String date = record.get("Data");
                String categoryName = record.get("Categoria");
                String isRecurring = record.get("É Recorrente?");

                LocalDate localDate = null;
                try {
                    if (date != null && !date.isEmpty()) {
                        localDate = LocalDate.parse(date); // Formato esperado: YYYY-MM-DD
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Formato de data inválido na linha: " + record.getRecordNumber());
                }
                // Construa a entidade
                Transaction transaction = new Transaction();

                Category category = categoryService.findByNameAndUser(categoryName, user);

                transaction.setType(type.equals("RECEITA") ? TransactionType.RECEITA: TransactionType.DESPESA);
                transaction.setAmount(amount.isEmpty() ? null : new BigDecimal(amount));
                transaction.setDescription(description);
                transaction.setDate(localDate);
                transaction.setCategory(category);
                transaction.setRecurring(isRecurring.equals("Sim"));
                transaction.setUser(user);

                transactions.add(transaction);
            }
        }

        for (Transaction transaction:transactions) {
            transactionService.addTransaction(transaction);

        }
    }

}
