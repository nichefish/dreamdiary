package io.nicheblog.dreamdiary.web.model.user.emplym;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.web.model.user.UserInfoItemDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * UserInfoDto
 * <pre>
 *  사용자(직원) 정보 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserEmplymDto
        extends BaseCrudDto {

    /** 사용자 정보 고유 ID (PK) */
    private Integer userEmplymNo;
    /** 이름 */
    private String userNm;
    /** 소속(회사)코드 */
    private String cmpyCd;
    /** 소속(회사)이름 */
    private String cmpyNm;
    /** 소속(팀)코드 */
    private String teamCd;
    /** 소속(팀)이름 */
    private String teamNm;
    /** 재직구분코드 */
    private String emplymCd;
    /** 재직구분이름 */
    private String emplymNm;
    /** 직급코드 */
    private String rankCd;
    /** 직급이름 */
    private String rankNm;
    /** 수습여부 */
    @Builder.Default
    private String apntcYn = "N";
    /** 입사일 */
    private String ecnyDt;
    /** 퇴사여부 */
    @Builder.Default
    private String retireYn = "N";
    /** 퇴사일 */
    private String retireDt;
    /** 은행 */
    private String acntBank;
    /** 계좌번호 */
    private String acntNo;
    /** 인사정보 설명 (관리자용) */
    private String emplymCn;
}
