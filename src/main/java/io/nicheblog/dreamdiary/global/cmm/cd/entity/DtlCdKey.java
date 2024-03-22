package io.nicheblog.dreamdiary.global.cmm.cd.entity;

import lombok.*;

import java.io.Serializable;

/**
 * DtlCdKey
 * <pre>
 *  상세코드 복합키 (clCd + dtlCd)
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DtlCdKey
        implements Serializable {

    /**
     * 분류코드
     */
    private String clCd;
    /**
     * 상세코드
     */
    private String dtlCd;
}
