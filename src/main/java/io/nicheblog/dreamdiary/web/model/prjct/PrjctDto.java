package io.nicheblog.dreamdiary.web.model.prjct;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * PrjctDto
 * <pre>
 *  프로젝트 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PrjctDto
        extends BaseAuditDto {

    //
}
