package io.nicheblog.dreamdiary.web.model.cmm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BaseJstreeStateDto
 * (공통) Jstree 상태(state) 정보 dto
 *
 * @author nichefish
 * @since 2022-05-24
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseJstreeStateDto {

    /** opened */
    @JsonProperty("opened")
    private boolean opened;
    /** disabled */
    @JsonProperty("disabled")
    private boolean disabled;
    /** selected */
    @JsonProperty("selected")
    private boolean selected;
}
