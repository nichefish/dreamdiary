package io.nicheblog.dreamdiary.feature.prospective.project.model;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** Prospective Project 목록 검색 파라미터. */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProspectiveProjectSearchParam extends BaseSearchParam {
    // createdBy는 서비스가 로그인 사용자로 강제한다.
}
