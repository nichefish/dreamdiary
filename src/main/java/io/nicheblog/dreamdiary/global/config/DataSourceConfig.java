package io.nicheblog.dreamdiary.global.config;

import io.nicheblog.dreamdiary.global.intrfc.repository.impl.BaseRepositoryImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
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
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

/**
 * DataSourceConfig
 * <pre>
 *  멀티 데이터소스 설정시 Primary Config.
 * </pre>
 *
 * @author nichefish
 */
@Configuration
@EnableJpaRepositories(
        basePackages = {"io.nicheblog.dreamdiary.**.repository.jpa", "io.nicheblog.dreamdiary.**.repository.querydsl" },
        repositoryBaseClass = BaseRepositoryImpl.class
)
@MapperScan(
        basePackages = { "io.nicheblog.dreamdiary.**.repository.mybatis" },
        sqlSessionTemplateRef = "sqlSessionTemplate"
)
public class DataSourceConfig
        implements EnvironmentAware {

    @Resource
    private Environment env;

    /**
     * 빈 등록 :: primaryDataSource
     *
     * @return {@link DataSource} -- DataSource 객체
     */
    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary")    // application.properties에서 사용한 이름
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 빈 등록 :: (JPA) EntityManagerFactory
     *
     * @param dataSource 주 데이터 소스 (primaryDataSource)
     * @return {@link EntityManagerFactory} -- EntityManagerFactory 객체
     */
    @Bean
    @Primary
    public EntityManagerFactory entityManagerFactory(final @Qualifier("primaryDataSource") DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("io.nicheblog.dreamdiary.**.entity");
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName()); // 네이밍
        properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName()); // 네이밍
        properties.put("hibernate.enable_lazy_load_no_trans", "true");
        em.setJpaPropertyMap(properties);
        em.afterPropertiesSet();

        return em.getObject();
    }

    /**
     * 빈 등록 :: (JPA) TransactionManager (JPA 트랜잭션 관리)
     *
     * @param entityManagerFactory JPA EntityManagerFactory 객체
     * @return PlatformTransactionManager 객체
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jtm = new JpaTransactionManager();
        jtm.setEntityManagerFactory(entityManagerFactory);
        return jtm;
    }

    /**
     * 빈 등록 :: (MyBatis) sessionFactory
     *
     * @param dataSource 주 데이터 소스 (primaryDataSource)
     * @param applicationContext 애플리케이션 컨텍스트 (ApplicationContext), MyBatis 매퍼 파일의 위치를 가져오기 위해 사용
     * @return {@link SqlSessionFactory} -- SqlSessionFactory 객체
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Bean
    @Primary
    public SqlSessionFactory sessionFactory(
            final @Qualifier("primaryDataSource") DataSource dataSource,
            final ApplicationContext applicationContext
    ) throws Exception {

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setMapperLocations(applicationContext.getResources("classpath*:**/repository/mybatis/*Mapper.xml"));
        bean.setDataSource(dataSource);
        bean.setVfs(SpringBootVFS.class);
        bean.setTypeAliasesPackage("io.nicheblog.dreamdiary.**.model");
        return bean.getObject();
    }

    /**
     * 빈 등록 :: (MyBatis) sqlSessionTemplate
     *
     * @param sqlSessionFactory MyBatis의 SqlSessionFactory 객체
     * @return {@link SqlSessionTemplate} -- SqlSessionTemplate 객체
     */
    @Bean
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(final SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 환경 설정을 주입합니다. (환경 변수에 직접 접근하느 클래스)
     *
     * @param environment Spring의 Environment 객체, 애플리케이션의 환경 설정 정보를 포함
     */
    @Override
    public void setEnvironment(final @NotNull Environment environment) {
        env = environment;
    }
}