package io.nicheblog.dreamdiary.web.model.user.profl;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
public class UserProflDto
        extends BaseCrudDto {

    /** 사용자 정보 고유 ID (PK) */
    private Integer userProflNo;

    /** 생년월일 */
    private String brthdy;
    /** 음력여부 */
    @Builder.Default
    private String lunarYn = "N";
    /** 프로필 설명 */
    private String proflCn;
}
