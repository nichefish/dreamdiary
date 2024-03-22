package io.nicheblog.dreamdiary.web.model.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LgnPolicyDto
 * <pre>
 *  로그인 정책 정보 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class LgnPolicyDto {

    /**
     * 로그인 정책 고유ID (PK)
     */
    private Integer lgnPolicyNo;
    /**
     * 로그인 최대 시도 횟수
     */
    private Integer lgnTryLmt;
    /**
     * 비밀번호 변경 일자
     */
    private Integer pwChgDy;
    /**
     * 미접속시 계정 잠금 일자
     */
    private Integer lgnLockDy;
    /**
     * 비밀번호 초기화 값
     */
    private String pwForReset;
}
