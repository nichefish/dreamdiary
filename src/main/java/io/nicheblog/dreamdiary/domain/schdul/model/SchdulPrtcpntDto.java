package io.nicheblog.dreamdiary.domain.schdul.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SchdulPrtcpntDto
 * <pre>
 *  일정 참여자 Dto
 *  ※ 일정 참여자(schdul_prtcpnt) = 일정(schdul)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"userId"})
public class SchdulPrtcpntDto {

    /** 일정 참여자 번호 (PK) */
    private Integer schdulPrtcpntNo;

    /** 참조 글 번호 */
    private Integer refPostNo;

    /** 참석자 ID */
    private String userId;

    /** 참석자 이름 */
    private String userNm;

    /* ----- */

    /**
     * 생성자.
     *
     * @param userId 사용자 ID
     */
    public SchdulPrtcpntDto(final String userId) {
        this.userId = userId;
    }
}
