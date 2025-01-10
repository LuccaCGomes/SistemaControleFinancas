package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.CsvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportControllerTest {

    @Mock
    private CsvService csvService;

    @InjectMocks
    private ImportController importController;

    private User user;
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        redirectAttributes = new RedirectAttributesModelMap();
    }

    @Test
    void importCSV_WithEmptyFile_ReturnsErrorMessage() throws Exception {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                new byte[0]
        );

        // Act
        String result = importController.importCSV(emptyFile, redirectAttributes, user);

        // Assert
        assertEquals("redirect:/categories", result);
        assertEquals("Por favor, envie um arquivo válido.", redirectAttributes.getFlashAttributes().get("error"));
        verify(csvService, never()).processCSV(any(MultipartFile.class), any(User.class));
    }

    @Test
    void importCSV_WithValidFile_ReturnsSuccessMessage() throws Exception {
        // Arrange
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "test content".getBytes()
        );

        doNothing().when(csvService).processCSV(any(MultipartFile.class), any(User.class));

        // Act
        String result = importController.importCSV(validFile, redirectAttributes, user);

        // Assert
        assertEquals("redirect:/transactions", result);
        assertEquals("Arquivo CSV importado com sucesso!", redirectAttributes.getFlashAttributes().get("message"));
        verify(csvService, times(1)).processCSV(validFile, user);
    }

    @Test
    void importCSV_WhenProcessingFails_ReturnsErrorMessage() throws Exception {
        // Arrange
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "test content".getBytes()
        );

        String errorMessage = "Erro de processamento";
        doThrow(new RuntimeException(errorMessage))
                .when(csvService)
                .processCSV(any(MultipartFile.class), any(User.class));

        // Act
        String result = importController.importCSV(validFile, redirectAttributes, user);

        // Assert
        assertEquals("redirect:/transactions", result);
        assertEquals("Erro ao importar o arquivo: " + errorMessage,
                redirectAttributes.getFlashAttributes().get("error"));
        verify(csvService, times(1)).processCSV(validFile, user);
    }

    @Test
    void constructor_WithCsvService_CreatesController() {
        // Arrange & Act
        CsvService newCsvService = mock(CsvService.class);
        ImportController controller = new ImportController(newCsvService);

        // Assert
        assertNotNull(controller);
    }

    @Test
    void importCSV_WithNullFile_ReturnsErrorMessage() throws Exception {
        // Arrange
        MultipartFile nullFile = null;

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> importController.importCSV(nullFile, redirectAttributes, user));
        verify(csvService, never()).processCSV(any(MultipartFile.class), any(User.class));
    }
}