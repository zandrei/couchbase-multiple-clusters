package com.example.couchbase.multiple.clusters;

import com.example.couchbase.multiple.clusters.entity.SampleEntityDestination;
import com.example.couchbase.multiple.clusters.repository.SampleDestinationRepository;
import com.example.couchbase.multiple.clusters.repository.SampleSourceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TestService {

    private final SampleSourceRepository sourceRepository;
    private final SampleDestinationRepository destinationRepository;

    public TestService(
            SampleSourceRepository sourceRepository,
            SampleDestinationRepository destinationRepository) {
        this.sourceRepository = sourceRepository;
        this.destinationRepository = destinationRepository;
    }

    @PostConstruct
    void afterInit() {
        final var sampleDocument =
                this.destinationRepository
                        .findById("destination:37e0da90-89a6-4313-ad58-71ecb0fcf822")
                        .doOnNext(
                                document ->
                                        System.out.printf(
                                                "found document with id: %s",
                                                document.getDocumentId()))
                        .switchIfEmpty(
                                this.sourceRepository
                                        .findById("source:37e0da90-89a6-4313-ad58-71ecb0fcf822")
                                        .doOnSubscribe(
                                                (subscription) ->
                                                        System.out.println(
                                                                "will search for document in source cluster"))
                                        .flatMap(
                                                entity ->
                                                        this.destinationRepository
                                                                .save(
                                                                        new SampleEntityDestination(
                                                                                entity.withId(
                                                                                        "destination:37e0da90-89a6-4313-ad58-71ecb0fcf822")))
                                                                .doOnSubscribe(
                                                                        (subscription) ->
                                                                                System.out.println(
                                                                                        "will save the document in destination cluster"))
                                                                .doOnNext(
                                                                        x ->
                                                                                System.out.println(
                                                                                        "saved document in destination cluster"))))
                        .block(Duration.ofSeconds(60));

        if (sampleDocument != null) {
            // cleanup for next run
            this.destinationRepository
                    .deleteById("destination:37e0da90-89a6-4313-ad58-71ecb0fcf822")
                    .block(Duration.ofSeconds(60));
        }
    }
}
