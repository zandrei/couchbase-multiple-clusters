package com.example.couchbase.multiple.clusters.framework;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.example.couchbase.multiple.clusters.config.DataMigrationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.repository.query.CouchbaseEntityInformation;
import org.springframework.data.couchbase.repository.support.SimpleReactiveCouchbaseRepository;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class SampleReactiveRepository<T extends WithDocumentId<T>, ID extends String>
        extends SimpleReactiveCouchbaseRepository<T, ID> {
    private final SampleReactiveRepositoryOperationsMapping.ReactiveCouchbaseOperationsWithFallback
            operations;

    private final DataMigrationProperties dataMigrationProperties;

    /**
     * Create a new Repository.
     *
     * @param entityInformation the Metadata for the entity.
     * @param operations the reference to the reactive template used.
     * @param repositoryInterface
     * @param dataMigrationProperties
     */
    public SampleReactiveRepository(
            CouchbaseEntityInformation<T, String> entityInformation,
            SampleReactiveRepositoryOperationsMapping.ReactiveCouchbaseOperationsWithFallback
                    operations,
            Class<?> repositoryInterface,
            DataMigrationProperties dataMigrationProperties) {
        super(entityInformation, operations.defaultOp(), repositoryInterface);
        this.operations = operations;
        this.dataMigrationProperties = dataMigrationProperties;
    }

    // consider the document id with the following structure: "<tenant>:<rest_of_id>"
    @Override
    public Mono<T> findById(ID id) {
        final var tenantAndTheRestOfTheIdElements = id.split(":", 2);
        String targetedTenant = tenantAndTheRestOfTheIdElements[0];
        final var sourceTenant =
                dataMigrationProperties.getMappings().getOrDefault(targetedTenant, targetedTenant);
        final var sourceId = "%s:%s".formatted(sourceTenant, tenantAndTheRestOfTheIdElements[1]);

        return operations
                .defaultOp()
                .findById(getJavaType())
                .inScope(getScope())
                .inCollection(getCollection())
                .one(id)
                .switchIfEmpty(
                        operations
                                .fallbackOp()
                                .findById(getJavaType())
                                .inScope(getScope())
                                .inCollection(getCollection())
                                .one(sourceId)
                                .doOnSubscribe(
                                        subscription ->
                                                log.info(
                                                        "Could not find document with id {} in default cluster, will try to retrieve it from fallback cluster with id {}",
                                                        id,
                                                        sourceId))
                                .flatMap(
                                        entity ->
                                                operations
                                                        .defaultOp()
                                                        .save(
                                                                entity.withId(id),
                                                                getScope(),
                                                                getCollection())));
    }

    @Override
    public Flux<T> findAll() {
        return this.findAll(new Query());
    }

    /*
    Overwriting this is needed because of the getQueryScanConsistency() method inside the base repository that
    uses the crudMethodMetadata property in the CouchbaseRepositoryBase class which is package private (so we cannot overwrite it)
     */
    private Flux<T> findAll(Query query) {
        return operations
                .defaultOp()
                .findByQuery(getJavaType())
                .withConsistency(QueryScanConsistency.NOT_BOUNDED)
                .inScope(getScope())
                .inCollection(getCollection())
                .matching(query)
                .all();
    }

    @Override
    public Flux<T> findAll(Sort sort) {
        return super.findAll(sort);
    }

    Class<T> getJavaType() {
        return getEntityInformation().getJavaType();
    }
}
