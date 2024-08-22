package io.nicheblog.dreamdiary.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import javax.annotation.Resource;

/**
 * ElasticSearchConfig
 * <pre>
 *  ElasticSearch 관련 설정 커스터마이즈
 * </pre>
 *
 * @author nichefish
 */
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Resource(name = "elasticsearchProperty")
    private ElasticsearchProperty elasticsearchProperty;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchProperty.getUris())
                .withBasicAuth(elasticsearchProperty.getUsername(), elasticsearchProperty.getPassword())
                .build();
    }
}