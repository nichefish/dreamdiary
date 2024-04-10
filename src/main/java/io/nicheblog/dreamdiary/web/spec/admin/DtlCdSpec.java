package io.nicheblog.dreamdiary.web.spec.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseCrudSpec;
import io.nicheblog.dreamdiary.global.intrfc.spec.embed.BaseStateSpec;
import org.springframework.stereotype.Component;

/**
 * DtlCdSpec
 * <pre>
 *  상세코드 검색인자 세팅 Specification
 *  ※상세코드(dtl_cd) = 분류코드 하위의 상세코드. 분류코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
public class DtlCdSpec
        implements BaseStateSpec<DtlCdEntity> {

    //
}
