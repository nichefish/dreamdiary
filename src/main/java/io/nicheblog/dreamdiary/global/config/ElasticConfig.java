package io.nicheblog.dreamdiary.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import javax.annotation.Resource;

/**
 * ElasticConfig
 * <pre>
 *  ElasticSearch 관련 설정 커스터마이즈
 * </pre>
 *
 * @author nichefish
 */
@Configuration
public class ElasticConfig extends ElasticsearchConfiguration {

    @Resource(name = "elasticProperty")
    private ElasticProperty elasticProperty;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticProperty.getUris())
                .withBasicAuth(elasticProperty.getUsername(), elasticProperty.getPassword())
                .build();
    }
}