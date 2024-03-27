package io.nicheblog.dreamdiary.api.jandi.model;

import io.nicheblog.dreamdiary.api.jandi.JandiTopic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * JandiParam
 * <pre>
 *  API:: JANDI:: JANDI API 호출 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@ToString
public class JandiParam {

    /**
     * 대상 토픽 코드
     */
    @Schema(description = "대상 토픽 코드", example = "NOTICE")
    private JandiTopic trgetTopic;

    /**
     * 메세지
     */
    @Schema(description = "대상 토픽 코드", example = "새로운 휴가계획서가 등록되었습니다.")
    private String msg;
}
