package io.nicheblog.dreamdiary.global.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * AuthRoleDto
 * <pre>
 *  (공통) 권한 정보 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseManagtEntity
 */
@Getter
@Setter
@NoArgsConstructor
public class AuthRoleDto {

    /**
     * 권한 코드 (PK)
     */
    private String authCd;

    /**
     * 권한 이름
     */
    private String authNm;

    /**
     * 권한 레벨
     */
    private Integer authLevel;

    /**
     * 상위 권한 코드 (null일시 최상위 권한)
     */
    private String topAuthCd;

    /**
     * 하위 권한 정보
     */
    private List<AuthRoleDto> subAuthList;
}