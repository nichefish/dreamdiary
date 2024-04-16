package io.nicheblog.dreamdiary.web.entity;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditRegEntity;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.experimental.UtilityClass;
import org.springframework.test.context.ActiveProfiles;

/**
 * BaseEntityTestHelper
 * <pre>
 *  테스트 Entity 기본속성 세팅 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
public class BaseEntityTestFactoryHelper {

    /**
     * 등록자 정보 세팅
     */
    public static void setRegstrInfo(Object obj) throws Exception {
        if (!(obj instanceof BaseAuditRegEntity)) return;

        ((BaseAuditRegEntity) obj).setRegDt(DateUtils.asDate("2000-01-01"));
        ((BaseAuditRegEntity) obj).setRegstrId("test_reg_user");
        ((BaseAuditRegEntity) obj).setRegstrInfo(AuditorInfo.builder().userId("test_reg_user").nickNm("test_reg_nick_nm").build());
    }

    /**
     * 수정자 정보 세팅
     */
    public static void setMdfusrInfo(Object obj) throws Exception {
        if (!(obj instanceof BaseAuditEntity)) return;

        ((BaseAuditEntity) obj).setMdfDt(DateUtils.asDate("2000-01-01"));
        ((BaseAuditEntity) obj).setMdfusrId("test_mdf_user");
        ((BaseAuditEntity) obj).setMdfusrInfo(AuditorInfo.builder().userId("test_mdf_user").nickNm("test_mdf_nick_nm").build());
    }
}
