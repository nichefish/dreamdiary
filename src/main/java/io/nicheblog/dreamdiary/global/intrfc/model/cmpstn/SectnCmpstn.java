package io.nicheblog.dreamdiary.global.intrfc.model.cmpstn;

import io.nicheblog.dreamdiary.web.model.cmm.sectn.SectnDto;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * SectnCmpstn
 * <pre>
 *  단락 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectnCmpstn
        implements Serializable {

    /** 단락 목록 */
    private List<SectnDto> list;
    /** 단락 갯수 */
    @Builder.Default
    private Integer cnt = 0;
    /** 단락 존재 여부 */
    @Builder.Default
    private Boolean hasSectn = false;
}