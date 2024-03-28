package io.nicheblog.dreamdiary.web.model.log;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

/**
 * LogStatsUserDto
 * <pre>
 *  (사용자별) 로그 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class LogStatsUserDto
        implements Comparable<LogStatsUserDto> {

    /**
     * 목록 순번
     */
    private Long rnum;

    /**
     * 아이디
     */
    private String userId;
    /**
     * 이름
     */
    private String userNm;
    /**
     * 권한코드
     */
    private String authCd;
    /**
     * 권한이름
     */
    private String authNm;

    /**
     * 로그 목록 건수
     */
    private Long actvtyCnt;

    /**
     * 사용자 정보
     */
    private String userInfoNo;
    private String retireYn;

    /* ----- */

    /**
     * 로그목록 선수 기준 정렬 (내림차순)
     */
    @SneakyThrows
    @Override
    public int compareTo(final @NotNull LogStatsUserDto compare) {
        Long compareCnt = compare.getActvtyCnt();
        if (compareCnt == null) return -1;
        return compareCnt.compareTo(this.actvtyCnt);
    }
}