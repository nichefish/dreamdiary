package io.nicheblog.dreamdiary.web.model.cmm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * BaseJstreeDto
 * <pre>
 *  Jstree Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Log4j2
public class BaseJstreeDto {

    /** 노드 고유 ID */
    @JsonProperty("id")
    protected String id;

    /** 상위 노드 ID */
    @JsonProperty("parent")
    protected String parent;

    /** 표시 */
    @JsonProperty("text")
    protected String test;

    /** 상태 */
    @JsonProperty("state")
    private BaseJstreeStateDto state;

    /** 하위 노드 */
    @JsonProperty("children")
    protected List<BaseJstreeDto> children;

    /* ----- */

    public void setOpenedState() {
        if (this.state == null) this.state = new BaseJstreeStateDto();
        this.state.setOpened(true);
    }
    public void setSelectedState() {
        if (this.state == null) this.state = new BaseJstreeStateDto();
        this.state.setSelected(true);
    }
    public void setOpenedAndSelectedState() {
        if (this.state == null) this.state = new BaseJstreeStateDto();
        this.state.setOpened(true);
        this.state.setSelected(true);
    }
}
