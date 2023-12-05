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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.config.ParsingUtils;
import org.springframework.data.couchbase.repository.config.ReactiveCouchbaseRepositoryConfigurationExtension;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.w3c.dom.Element;

/**
 * @author Subhashni Balakrishnan
 * @author Mark Paluch
 * @author Alexander Derkach
 * @since 3.0
 */
public class SampleReactiveCouchbaseRepositoryConfigurationExtension
        extends ReactiveCouchbaseRepositoryConfigurationExtension {

    private static final String REACTIVE_COUCHBASE_TEMPLATE_REF = "reactive-couchbase-template-ref";

    /**
     * The reference property to use in xml configuration to specify the index manager bean to use
     * with a repository.
     */
    private static final String COUCHBASE_INDEX_MANAGER_REF = "couchbase-index-manager-ref";

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtension#getRepositoryFactoryBeanClassName()
     */
    @Override
    public String getRepositoryFactoryBeanClassName() {
        return SampleReactiveCouchbaseRepositoryFactoryBean.class.getName();
    }

    @Override
    public void postProcess(
            BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {

        Element element = config.getElement();
        ParsingUtils.setPropertyReference(
                builder, element, REACTIVE_COUCHBASE_TEMPLATE_REF, "reactiveCouchbaseOperations");
        ParsingUtils.setPropertyReference(
                builder, element, COUCHBASE_INDEX_MANAGER_REF, "indexManager");
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#postProcess(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource)
     */
    @Override
    public void postProcess(
            BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {

        builder.addDependsOn("sampleReactiveCouchbaseOperationsMapping");
        builder.addPropertyReference(
                "sampleReactiveCouchbaseOperationsMapping",
                "sampleReactiveCouchbaseOperationsMapping");
    }
}
