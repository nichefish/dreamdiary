package io.nicheblog.dreamdiary.global.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

/**
 * SchemaHandler
 *
 * @author nichefish
 */
@Component("schemaHandler")
@Log4j2
public class SchemaHandler {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private DataSource datasource;

    @Resource
    private BuildProperties buildProperties;

    @Resource
    @Qualifier("vendorProperties")
    private Properties vendorProperties;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 관리자administrator 계정 존재여부 체크 및 생성
     */
    public boolean execute() throws Exception {

        log.info("Product Name: " + buildProperties.getName());
        log.info("Product Version: " + buildProperties.getVersion());
        log.info("Build Date: " + buildProperties.getTime());

        // sql script 실행:: 메소드 분리
        this.execSqlScript();

        // 구동이 필요한 로직이 있으면 수행
        return true;
        // try {
        //     // UserModel model = userService.loadUserByUsername("administrator");
        //     // boolean alreadyHasAdmin = (model != null);
        //     // if (alreadyHasAdmin) return true;
        // } catch (UsernameNotFoundException e) {
        //     // administrator 새로 생성 :: 메소드 분리
        //     return this.createAdminUser();
        // } catch (Exception e) {
        //     return false;
        // }
        // return false;
    }

    /**
     * databaseProductName 얻기:: 메소드 분리
     */
    public String getDatabaseProductName() throws SQLException {
        Connection connection = datasource.getConnection();
        DatabaseMetaData databaseMetaData = connection.getMetaData();

        return databaseMetaData.getDatabaseProductName();
    }

    /**
     * sql script 실행:: 메소드 분리
     */
    public boolean execSqlScript() throws Exception {

        // database vendor 판별
        String databaseProductName = this.getDatabaseProductName();
        String vendor = (String) vendorProperties.getOrDefault(databaseProductName, "");
        log.info(vendor);

        // database vendor에 따라 .sql 파일 로드
        // TODO: 상황에 맞게 로직 보완하기 (releaseDate 등)
        String location = String.format("classpath:schema/231106/data-%s.sql", vendor);
        org.springframework.core.io.Resource schemaSql = applicationContext.getResource(location);
        if (!schemaSql.exists()) return true;

        EncodedResource encodedResource = new EncodedResource(schemaSql, "UTF-8");
        try {
            ScriptUtils.executeSqlScript(
                    datasource.getConnection(),
                    encodedResource,
                    false,
                    false,
                    "--",
                    ";;",
                    "##/*",
                    "*/##"
            );
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }

        return true;
    }
}
