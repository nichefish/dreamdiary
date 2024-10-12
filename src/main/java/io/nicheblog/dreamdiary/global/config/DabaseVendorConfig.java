package io.nicheblog.dreamdiary.global.config;

import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * DabaseVendorConfig
 * <pre>
 *  Database별로 VendorID 정의
 * </pre>
 *
 * @author nichefish
 */
@Configuration
public class DabaseVendorConfig {

    /**
     * VendorDatabaseIdProvider
     */
    @Bean
    public VendorDatabaseIdProvider vendorDatabaseIdProvider() {
        VendorDatabaseIdProvider vendorDatabaseIdProvider = new VendorDatabaseIdProvider();
        vendorDatabaseIdProvider.setProperties(vendorProperties());
        return vendorDatabaseIdProvider;
    }

    /**
     * VendorProperties 정의
     */
    @Bean
    public Properties vendorProperties() {
        Properties properties = new Properties();
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("MariaDB", "mysql");
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("Tibero", "oracle");
        properties.setProperty("Microsoft SQL Server", "sqlserver");
        properties.setProperty("PostgreSQL", "postgresql");

        return properties;
    }
}
