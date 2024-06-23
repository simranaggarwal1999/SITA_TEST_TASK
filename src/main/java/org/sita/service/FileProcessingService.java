package org.sita.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileProcessingService {
    @Value("${output.directory}")
    private String outputDirectory;

    @Value("${processed.directory}")
    private String processedDirectory;

    @Value("${error.directory}")
    private String errorDirectory;

    public void process(File file) {
        String filenameWithoutExtension = getFilenameWithoutExtension(file.getName());
        String extension = getFileExtension(file.getName());
        try {
            List<Integer> numbers = Files.lines(file.toPath())
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            int sum = numbers.stream().mapToInt(Integer::intValue).sum();
            sendToProcessed(filenameWithoutExtension,extension,sum,file);
        } catch (Exception e) {
            try {
                String newFilename = filenameWithoutExtension + ".ERROR" + extension;
                String errorFilePath = Paths.get(errorDirectory, newFilename).toString();
                Files.move(file.toPath(), Paths.get(errorFilePath));
            } catch (IOException ioException) {
                log.error("File already exists",ioException);
            }
            log.error("Inappropriate data in file",e);
        }
    }

    public void sendToProcessed(String filenameWithoutExtension,String extension,int sum,File file) {
        try {
            String newFilename;
            newFilename = filenameWithoutExtension + ".OUTPUT" + extension;
            String outputFilePath = Paths.get(outputDirectory, newFilename).toString();
            Files.write(Paths.get(outputFilePath), String.valueOf(sum).getBytes());

            newFilename = filenameWithoutExtension + ".PROCESSED" + extension;
            String processedFilePath = Paths.get(processedDirectory, newFilename).toString();
            Files.move(file.toPath(), Paths.get(processedFilePath));
        }
        catch (IOException ioException){
            log.error("File already exists",ioException);
        }
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
}
