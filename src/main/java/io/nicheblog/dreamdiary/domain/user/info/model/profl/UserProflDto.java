package io.nicheblog.dreamdiary.domain.user.info.model.profl;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * UserProflDto
 * <pre>
 *  사용자 프로필 Dto.
 * </pre>
 *
 * @author nichefish
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

    /** 음력 여부 (Y/N) */
    @Builder.Default
    @Size(min = 1, max = 1)
    @Pattern(regexp = "^[YN]$")
    private String lunarYn = "N";

    /** 프로필 설명 */
    private String proflCn;
}
