package org.sita.config;

import org.sita.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.LoggingHandler;
import java.io.File;


@Configuration
@EnableIntegration
public class FileIntegrationConfig {

    @Value("${polling.interval}")
    private int pollingValue;

    @Value("${input.directory}")
    private String inputDirectory;

    @Bean
    public IntegrationFlow fileReadingFlow(FileProcessingService fileProcessingService) {
        return IntegrationFlow
                .from(Files.inboundAdapter(new File(inputDirectory)), e -> e.poller(Pollers.fixedDelay(pollingValue).maxMessagesPerPoll(1)))
                .filter(onlyTextFiles())
                .handle(fileProcessingService, "process")
                .log(LoggingHandler.Level.INFO, "org.sita.config.FileIntegrationConfig", m -> m.getPayload())
                .get();
    }

    @Bean
    public GenericSelector<File> onlyTextFiles() {
        return new GenericSelector<File>() {
            @Override
            public boolean accept(File source) {
                return source.getName().endsWith(".txt");
            }
        };
    }
}
