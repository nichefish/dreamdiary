package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditRegEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditRegDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

/**
 * BaseListMapstruct
 * <pre>
 *  (공통/상속) MapStruct 기반 Mapper 인터페이스
 *  (entity -> listDto 추가)
 * <pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
public interface BaseListMapstruct<Dto, ListDto, Entity>
        extends BaseMapstruct<Dto, Entity> {

    /**
     * Entity -> ListDto
     */
    ListDto toListDto(final Entity e) throws Exception;

    /**
     * default : BaseEntity 기본 요소들 매핑
     */
    @AfterMapping
    default void mapBaseListFields(final Entity entity, final @MappingTarget ListDto dto) throws Exception {
        // AUDIT_REG :: 공통 필드 매핑 로직
        if (entity instanceof BaseAuditRegEntity && dto instanceof BaseAuditRegDto) {
            BaseAuditRegEntity baseEntity = ((BaseAuditEntity) entity);
            AuditorInfo regstrInfo = baseEntity.getRegstrInfo();
            if (regstrInfo != null) {
                // 작성자 이름
                ((BaseAuditRegDto) dto).setRegstrNm(baseEntity.getRegstrInfo().getNickNm());
                // 작성일사
                ((BaseAuditRegDto) dto).setRegDt(DateUtils.asStr(baseEntity.getRegDt(), DatePtn.DATETIME));
                // 작성자 여부
                ((BaseAuditRegDto) dto).setIsRegstr(AuthUtils.isRegstr(baseEntity.getRegstrId()));
            }
        }
        // AUDIT :: 공통 필드 매핑 로직
        if (entity instanceof BaseAuditEntity && dto instanceof BaseAuditDto) {
            BaseAuditEntity baseEntity = ((BaseAuditEntity) entity);
            AuditorInfo mdfusrInfo = baseEntity.getMdfusrInfo();
            if (mdfusrInfo != null) {
                // 수정자 이름
                ((BaseAuditDto) dto).setMdfusrNm(baseEntity.getMdfusrInfo().getNickNm());
                // 수정일시
                ((BaseAuditDto) dto).setMdfDt(DateUtils.asStr(baseEntity.getMdfDt(), DatePtn.DATETIME));
                // 수정자 여부
                ((BaseAuditDto) dto).setIsMdfusr(AuthUtils.isMdfusr(baseEntity.getMdfusrId()));
            }
        }
        // MANAGE :: ...
        // ATCH :: 공통 필드 매핑 로직
        if (entity instanceof BaseAtchEntity && dto instanceof BaseAtchDto) {
            BaseAtchEntity baseEntity = ((BaseAtchEntity) entity);
            // 첨부파일 존재 여부
            Boolean hasAtchFile = !(baseEntity.getAtchFileNo() == null || baseEntity.getAtchFileInfo() == null || CollectionUtils.isEmpty(baseEntity.getAtchFileInfo().getAtchFileList()));
            ((BaseAtchDto) dto).setHasAtchFile(hasAtchFile);
        }
        // CLSF :: BaseClsfMapstruct쪽에 정의
        // POST :: 공통 필드 매핑 로직
        if (entity instanceof BasePostEntity && dto instanceof BasePostDto) {
            BasePostEntity baseEntity = ((BasePostEntity) entity);
            // 글분류 이름
            if (baseEntity.getCtgrCdInfo() != null) {
                ((BasePostDto) dto).setCtgrNm(baseEntity.getCtgrCdInfo().getDtlCdNm());
                ((BasePostDto) dto).setHasCtgrNm(baseEntity.getCtgrCdInfo() != null);
            }
        }
    }
}
