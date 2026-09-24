package io.nicheblog.dreamdiary.feature.prospective.keep.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Keep의 단일 Project 소속만 변경하는 요청. */
@Getter
@Setter
@NoArgsConstructor
public class ProspectiveKeepProjectRequest {

    /** 이동할 Project ID. null이면 Project 미지정으로 이동한다. */
    private Integer projectId;
}
