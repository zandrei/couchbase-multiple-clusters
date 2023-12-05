package com.example.couchbase.multiple.clusters.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/*
{
	"mappings": {
		"<dest_tenant>":"<source_tenant>",
	...
	}
}
 */
@Configuration
@ConfigurationProperties("sample.data-migration")
@Getter
public class DataMigrationProperties {
    @Setter private Map<String, String> mappings;
}
