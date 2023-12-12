/*
 * Copyright 2017-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.couchbase.multiple.clusters.framework;

import com.example.couchbase.multiple.clusters.config.DataMigrationProperties;
import org.springframework.data.couchbase.core.ReactiveCouchbaseOperations;
import org.springframework.data.couchbase.repository.config.ReactiveRepositoryOperationsMapping;
import org.springframework.data.couchbase.repository.support.CouchbaseRepositoryFactoryBean;
import org.springframework.data.couchbase.repository.support.ReactiveCouchbaseRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

import java.io.Serializable;

/*
 * We need to overwrite ReactiveCouchbaseRepositoryFactoryBean because we want to make spring boot back off
 * with registering a CouchbaseReactiveRepositoriesRegistrar
 * which will EnableReactiveCouchbaseRepositories instead of EnableSampleReactiveCouchbaseRepositories and re-register every bean
 */
public class SampleReactiveCouchbaseRepositoryFactoryBean<
                T extends Repository<S, ID>, S, ID extends Serializable>
        extends ReactiveCouchbaseRepositoryFactoryBean<T, S, ID> {

    private final DataMigrationProperties dataMigrationProperties;
    /** Contains the reference to the template. */
    private SampleReactiveRepositoryOperationsMapping sampleReactiveCouchbaseOperationsMapping;

    /**
     * Creates a new {@link CouchbaseRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     * @param dataMigrationProperties
     */
    public SampleReactiveCouchbaseRepositoryFactoryBean(
            Class<? extends T> repositoryInterface,
            final ReactiveCouchbaseOperations reactiveCouchbaseOperations,
            DataMigrationProperties dataMigrationProperties) {
        super(repositoryInterface);
        this.dataMigrationProperties = dataMigrationProperties;
    }

    /**
     * Set the template reference.
     *
     * @param reactiveCouchbaseOperations the reference to the operations template.
     */
    public void setReactiveCouchbaseOperations(
            final ReactiveCouchbaseOperations reactiveCouchbaseOperations) {
        setSampleReactiveCouchbaseOperationsMapping(
                new SampleReactiveRepositoryOperationsMapping(reactiveCouchbaseOperations));
    }

    public void setReactiveCouchbaseOperationsMapping(
            final SampleReactiveRepositoryOperationsMapping couchbaseOperationsMapping) {
        super.setReactiveCouchbaseOperationsMapping(couchbaseOperationsMapping);
        this.sampleReactiveCouchbaseOperationsMapping = couchbaseOperationsMapping;
        setMappingContext(couchbaseOperationsMapping.getMappingContext());
    }

    public SampleReactiveRepositoryOperationsMapping getSampleReactiveCouchbaseOperationsMapping() {
        return sampleReactiveCouchbaseOperationsMapping;
    }

    public void setSampleReactiveCouchbaseOperationsMapping(
            SampleReactiveRepositoryOperationsMapping sampleReactiveCouchbaseOperationsMapping) {
        this.sampleReactiveCouchbaseOperationsMapping = sampleReactiveCouchbaseOperationsMapping;
        this.setReactiveCouchbaseOperationsMapping(sampleReactiveCouchbaseOperationsMapping);
        setMappingContext(sampleReactiveCouchbaseOperationsMapping.getMappingContext());
    }

    /**
     * Returns a factory instance.
     *
     * @return the factory instance.
     */
    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return getFactoryInstance(sampleReactiveCouchbaseOperationsMapping);
    }

    /**
     * Get the factory instance for the operations.
     *
     * @param couchbaseOperationsMapping the reference to the template.
     * @return the factory instance.
     */
    protected SampleReactiveCouchbaseRepositoryFactory getFactoryInstance(
            final SampleReactiveRepositoryOperationsMapping couchbaseOperationsMapping) {
        return new SampleReactiveCouchbaseRepositoryFactory(
                couchbaseOperationsMapping, dataMigrationProperties);
    }

    /** Make sure that the dependencies are set and not null. */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(
                sampleReactiveCouchbaseOperationsMapping, "operationsMapping must not be null!");
    }
}
