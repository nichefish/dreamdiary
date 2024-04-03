package io.nicheblog.dreamdiary.global.intrfc.model.cmpstn;

import lombok.*;

import java.io.Serializable;

/**
 * StateCmpstn
 * <pre>
 *  상태 관리(사용여부, 정렬순서) 관련 정보 위임 (dto level)
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateCmpstn
        implements Serializable {

    /** 정렬 순서 */
    @Builder.Default
    private Integer sortOrdr = 0;

    /** 사용 여부 (Y/N) */
    @Builder.Default
    private String useYn = "Y";
}