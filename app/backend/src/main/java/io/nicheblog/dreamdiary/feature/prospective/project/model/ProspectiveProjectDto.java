package io.nicheblog.dreamdiary.feature.prospective.project.model;

import io.nicheblog.dreamdiary.auth.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.model.cmpstn.LifecycleCmpstn;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.model.cmpstn.LifecycleCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * ProspectiveProjectDto
 * <pre>
 *  Prospective Project 읽기·쓰기 DTO.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProspectiveProjectDto extends BaseAuditDto
        implements Identifiable<Integer>, LifecycleCmpstnModule {

    /** Project ID */
    private Integer id;

    /** 사용자에게 표시할 Project 이름 */
    @NotBlank
    @Size(max = 200)
    private String title;

    /** Project가 묶는 장기 맥락에 대한 선택적 설명 */
    private String description;

    /** 사용자 Project 목록의 전역 순번 */
    private Integer sortOrder;

    /** Project의 현재 진행 단계. 행이 없으면 OPEN이다. */
    private LifecycleCmpstn lifecycle;

    @Override
    public Integer getKey() {
        return this.id;
    }
}
