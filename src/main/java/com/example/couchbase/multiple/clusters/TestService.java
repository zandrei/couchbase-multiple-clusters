package com.example.couchbase.multiple.clusters;

import com.example.couchbase.multiple.clusters.repository.SampleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TestService {

    private final SampleRepository repository;

    public TestService(SampleRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    void afterInit() {
        final var sampleDocument = this.repository
                .findById("destination:37e0da90-89a6-4313-ad58-71ecb0fcf822")
                .doOnNext(
                        document ->
                                System.out.printf("found document with id: %s", document.getDocumentId()))
                .block(Duration.ofSeconds(60));

        if (sampleDocument != null) {
            // cleanup for next run
            this.repository.deleteById("destination:37e0da90-89a6-4313-ad58-71ecb0fcf822").block(Duration.ofSeconds(60));
        }
    }
}
