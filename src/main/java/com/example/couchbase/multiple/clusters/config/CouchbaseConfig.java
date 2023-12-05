package com.example.couchbase.multiple.clusters.config;

import com.couchbase.client.java.env.ClusterEnvironment;
import com.example.couchbase.multiple.clusters.entity.SampleEntity;
import com.example.couchbase.multiple.clusters.framework.EnableSampleReactiveCouchbaseRepositories;
import com.example.couchbase.multiple.clusters.framework.SampleReactiveRepository;
import com.example.couchbase.multiple.clusters.framework.SampleReactiveRepositoryOperationsMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.SimpleCouchbaseClientFactory;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.CouchbaseCustomConversions;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.core.convert.translation.JacksonTranslationService;

import java.util.List;

@Configuration
@EnableSampleReactiveCouchbaseRepositories(
        basePackages = "com.example.couchbase.multiple.clusters.repository",
        repositoryBaseClass = SampleReactiveRepository.class)
@Slf4j
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    private final CouchbaseProperties properties;
    private final CouchbaseAltProperties altProperties;

    @Autowired private ObjectMapper mapper;

    @Autowired private ApplicationContext applicationContext;

    public CouchbaseConfig(CouchbaseProperties properties, CouchbaseAltProperties altProperties) {
        this.properties = properties;
        this.altProperties = altProperties;
    }

    @Override
    public String getConnectionString() {
        return altProperties.getConnectionString();
    }

    @Override
    public String getUserName() {
        return properties.getBucket().username();
    }

    @Override
    public String getPassword() {
        return properties.getBucket().password();
    }

    @Override
    public String getBucketName() {
        return properties.getBucket().name();
    }

    public ReactiveCouchbaseTemplate sampleReactiveCouchbaseTemplate(
            CouchbaseClientFactory couchbaseClientFactory,
            MappingCouchbaseConverter mappingCouchbaseConverter) {
        final var reactiveCouchbaseTemplate =
                new ReactiveCouchbaseTemplate(
                        couchbaseClientFactory,
                        mappingCouchbaseConverter,
                        new JacksonTranslationService(),
                        getDefaultConsistency());
        /*
         * Application context is needed so that implementations of {@link
         * AbstractCouchbaseEventListener} are called
         */
        reactiveCouchbaseTemplate.setApplicationContext(applicationContext);
        return reactiveCouchbaseTemplate;
    }

    public CouchbaseClientFactory sampleCouchbaseClientFactory(
            String bucketName, String connectionString) {
        final var clusterEnvironment =
                (ClusterEnvironment) applicationContext.getBean("couchbaseClusterEnvironment");
        return new SimpleCouchbaseClientFactory(
                connectionString, authenticator(), bucketName, null, clusterEnvironment);
    }

    @Override
    public CouchbaseCustomConversions customConversions() {
        return new CouchbaseCustomConversions(List.of());
    }

    @Bean(name = "sampleReactiveCouchbaseOperationsMapping")
    public SampleReactiveRepositoryOperationsMapping
            sampleReactiveCouchbaseRepositoryOperationsMapping(
                    ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {
        // create a base mapping that associates all repositories to the default template
        SampleReactiveRepositoryOperationsMapping baseMapping =
                new SampleReactiveRepositoryOperationsMapping(reactiveCouchbaseTemplate);
        // let the user tune it
        configureReactiveRepositoryOperationsMapping(baseMapping);
        return baseMapping;
    }

    void configureReactiveRepositoryOperationsMapping(
            SampleReactiveRepositoryOperationsMapping mapping) {
        try {
            var customConversions = customConversions();
            var converter =
                    mappingCouchbaseConverter(
                            couchbaseMappingContext(customConversions), customConversions);
            // this is needed as spring data couchbase ignores custom conversions and also requires
            // an
            // after properties is set to read the new
            // list(https://github.com/spring-projects/spring-data-couchbase/issues/1141)
            converter.afterPropertiesSet();

            final var altBucket =
                    sampleCouchbaseClientFactory("sample", altProperties.getConnectionString());

            final var bucket =
                    sampleCouchbaseClientFactory("sample", properties.getConnectionString());

            mapping.mapEntity(
                    SampleEntity.class,
                    // primary cluster (destination)
                    sampleReactiveCouchbaseTemplate(altBucket, converter),
                    // fallback cluster (source)
                    sampleReactiveCouchbaseTemplate(bucket, converter));

        } catch (Exception e) {
            log.error("Could not configure repository operations mapping: {}", e.getMessage(), e);
        }
    }
}
