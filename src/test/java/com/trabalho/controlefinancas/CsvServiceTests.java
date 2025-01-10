package com.trabalho.controlefinancas;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.CategoryService;
import com.trabalho.controlefinancas.service.CsvService;
import com.trabalho.controlefinancas.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvServiceTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private CsvService csvService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
    }

    @Test
    void generateCsv_ValidData_ReturnsByteArray() {
        List<String[]> data = List.of(
                new String[]{"Header1", "Header2"},
                new String[]{"Value1", "Value2"}
        );

        byte[] result = csvService.generateCsv(data);

        assertNotNull(result);
        assertTrue(result.length > 0);
        String csvContent = new String(result, StandardCharsets.UTF_8);
        assertTrue(csvContent.contains("Header1,Header2"));
        assertTrue(csvContent.contains("Value1,Value2"));
    }

    @Test
    void generateCsv_EmptyData_ReturnsEmptyByteArray() {
        List<String[]> emptyData = List.of();

        byte[] result = csvService.generateCsv(emptyData);

        assertNotNull(result);
        assertEquals(0, result.length);
    }

}