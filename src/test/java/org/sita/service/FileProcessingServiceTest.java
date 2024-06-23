package org.sita.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.nio.file.Files;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class FileProcessingServiceTest {

    private String outputDirectory;
    private String processedDirectory;
    private String errorDirectory;

    private FileProcessingService fileProcessingService;

    @BeforeEach
    void setUp() {
        outputDirectory = "src/test/resources/output";
        processedDirectory = "src/test/resources/processed";
        errorDirectory = "src/test/resources/error";

        fileProcessingService = new FileProcessingService();
        ReflectionTestUtils.setField(fileProcessingService, "outputDirectory", outputDirectory);
        ReflectionTestUtils.setField(fileProcessingService, "processedDirectory", processedDirectory);
        ReflectionTestUtils.setField(fileProcessingService, "errorDirectory", errorDirectory);
    }

    @Test
    void testProcess_ValidFile() throws IOException {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.write(tempFile, List.of("1", "2", "3"));
        File file = tempFile.toFile();
        String filenameWithoutExtension = getFilenameWithoutExtension(file.getName());
        String extension = getFileExtension(file.getName());
        String newFilename = filenameWithoutExtension + ".OUTPUT" + extension;
        fileProcessingService.process(file);
        Path outputPath = Path.of(outputDirectory,newFilename);
        Assertions.assertEquals("6",Files.readString(outputPath));
        newFilename = filenameWithoutExtension + ".PROCESSED" + extension;
        Path processedPath = Path.of(processedDirectory,newFilename);
        Assertions.assertEquals(normalizeContent(List.of("1", "2", "3")),normalizeContent(Files.readAllLines(processedPath)));
    }

    @Test
    void testProcess_InvalidFile() throws IOException {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.write(tempFile, List.of("1", "a", "3"));
        File file = tempFile.toFile();
        String filenameWithoutExtension = getFilenameWithoutExtension(file.getName());
        String extension = getFileExtension(file.getName());
        String newFilename = filenameWithoutExtension + ".ERROR" + extension;
        fileProcessingService.process(file);
        Path errorPath = Path.of(errorDirectory, newFilename);
        Assertions.assertEquals(normalizeContent(List.of("1", "a", "3")),normalizeContent(Files.readAllLines(errorPath)));
    }

    private static String getFilenameWithoutExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return filename;
        }
        return filename.substring(0, dotIndex);
    }

    private static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return filename.substring(dotIndex);
    }

    private String normalizeContent(List<String> lines) {
        return lines.stream()
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }
}