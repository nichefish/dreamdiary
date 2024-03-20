package io.nicheblog.dreamdiary.cmm.config;

import io.nicheblog.dreamdiary.cmm.intrfc.repository.impl.BaseRepositoryImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

/**
 * DataSourceConfig
 * <pre>
 *  멀티 데이터소스 설정시 Primary Config
 * </pre>
 *
 * @author nichefish
 */
@Configuration
@PropertySource({"classpath:application.yml"})
@EnableJpaRepositories(
        basePackages = { "io.nicheblog.dreamdiary.*.repository" },
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager",
        repositoryBaseClass = BaseRepositoryImpl.class
)
@MapperScan(
        basePackages = { "io.nicheblog.dreamdiary.**.mapper" },
        sqlSessionTemplateRef = "sqlSessionTemplate"
)
public class DataSourceConfig
        implements EnvironmentAware {

    @Resource
    private Environment env;

    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")    // application.properties에서 사용한 이름
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * JPA Entity
     */
    @Primary
    @Bean
    public EntityManagerFactory entityManagerFactory(final @Qualifier("primaryDataSource") DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("io.nicheblog.dreamdiary.*.entity");
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName()); // 네이밍
        properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName()); // 네이밍
        em.setJpaPropertyMap(properties);
        em.afterPropertiesSet();

        return em.getObject();
    }

    /**
     * JPA TransactionManager
     */
    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jtm = new JpaTransactionManager();
        jtm.setEntityManagerFactory(entityManagerFactory);
        return jtm;
    }

    /**
     * MyBatis
     */
    @Primary
    @Bean
    public SqlSessionFactory sessionFactory(
            final @Qualifier("primaryDataSource") DataSource dataSource,
            final ApplicationContext applicationContext
    ) throws Exception {

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setMapperLocations(
                // bix5-library에서 Mapper.xml 경로 = / (root)임.
                // -> mapper/sql/*Mapper.xml 이 형식만 맞으면 다 로딩하도록 설정
                applicationContext.getResources("classpath*:**/mapper/sql/*Mapper.xml"));
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Primary
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(final SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Override
    public void setEnvironment(final Environment environment) {
        env = environment;
    }
}