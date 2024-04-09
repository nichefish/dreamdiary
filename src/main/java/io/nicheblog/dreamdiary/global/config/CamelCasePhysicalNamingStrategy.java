package io.nicheblog.dreamdiary.global.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * CamelCasePhysicalNamingStrategy
 * <pre>
 *  JPA에서 컬럼명 규칙 전략 수동 주입
 * </pre>
 *
 * @author nichefish
 */
public class CamelCasePhysicalNamingStrategy
        extends PhysicalNamingStrategyStandardImpl {

    @Override
    public Identifier toPhysicalTableName(
            Identifier name,
            JdbcEnvironment context
    ) {
        // 테이블 이름 변환 로직을 여기에 구현
        return new Identifier(name.getText(), name.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(
            Identifier name,
            JdbcEnvironment context
    ) {
        // 컬럼 이름 변환 로직을 여기에 구현
        return new Identifier(name.getText(), name.isQuoted());
    }
}
