package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.entity.*;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.*;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.*;
import io.nicheblog.dreamdiary.global.intrfc.model.*;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.*;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

/**
 * MapstructHelper
 * <pre>
 *  Mapstruct에서 쓰는 공통 로직 분리
 * </pre>
 *
 * @author nichefish
 */
public class MapstructHelper {

    /**
     * Map Base-inheritted Fields
     */
    public static <Entity extends BaseCrudEntity, Dto extends BaseCrudDto> void mapBaseFields(final Entity entity, final @MappingTarget Dto dto) throws Exception {

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

    /**
     * Map Clsf Fields
     */
    public static <Entity extends BaseClsfEntity, Dto extends BaseClsfDto> void mapClsfFields(final Entity entity, final @MappingTarget Dto dto) throws Exception {
        // 댓글 :: 공통 필드 매핑 로직
        boolean usesCommentModule = (entity instanceof CommentEmbedModule && dto instanceof CommentCmpstnModule);
        if (usesCommentModule) {
            CommentEmbed embed = ((CommentEmbedModule) entity).getComment();
            CommentCmpstn cmpstn = CommentEmbedMapstruct.INSTANCE.toDto(embed);
            ((CommentCmpstnModule) dto).setComment(cmpstn);
        }

        // 태그 :: 공통 필드 매핑 로직
        boolean usesTagModule = (entity instanceof TagEmbedModule && dto instanceof TagCmpstnModule);
        if (usesTagModule) {
            TagEmbed embed = ((TagEmbedModule) entity).getTag();
            TagCmpstn cmpstn = TagEmbedMapstruct.INSTANCE.toDto(embed);
            ((TagCmpstnModule) dto).setTag(cmpstn);
        }

        // 조치 :: 공통 필드 매핑 로직
        boolean usesManagtModule = (entity instanceof ManagtEmbedModule && dto instanceof ManagtCmpstnModule);
        if (usesManagtModule) {
            ManagtEmbed embed = ((ManagtEmbedModule) entity).getManagt();
            ManagtCmpstn cmpstn = ManagtEmbedMapstruct.INSTANCE.toDto(embed);
            ((ManagtCmpstnModule) dto).setManagt(cmpstn);
        }

        // 열람 :: 공통 필드 매핑 로직
        boolean usesViewerModule = (entity instanceof ViewerEmbedModule && dto instanceof ViewerCmpstnModule);
        if (usesViewerModule) {
            ViewerEmbed embed = ((ViewerEmbedModule) entity).getViewer();
            ViewerCmpstn cmpstn = ViewerEmbedMapstruct.INSTANCE.toDto(embed);
            ((ViewerCmpstnModule) dto).setViewer(cmpstn);
        }

        // 새 글 여부 표시
        if (usesManagtModule && usesViewerModule) {
            ((ViewerCmpstnModule) dto).setIsNew(determineIfNew(entity));
        }
    }

    /**
     * Map State Fields
     */
    public static <Entity, Dto> void mapStateFields(final Dto dto, final @MappingTarget Entity entity) throws Exception {
        // 댓글 :: 공통 필드 매핑 로직
        boolean usesStateModule = (entity instanceof StateEmbedModule && dto instanceof StateCmpstnModule);
        if (usesStateModule) {
            StateEmbed embed = ((StateEmbedModule) entity).getState();
            if (embed == null) embed = new StateEmbed();
            StateCmpstn cmpstn = ((StateCmpstnModule) dto).getState();
            if (cmpstn == null) cmpstn = new StateCmpstn();
            StateEmbedMapstruct.INSTANCE.updateFromDto(cmpstn, embed);
            ((StateEmbedModule) entity).setState(embed);
        }
    }

    /** 
     * 새 글 여부 처리 로직:: 메소드 분리
     */
    public static <Entity extends BaseClsfEntity, Dto extends BaseClsfDto> Boolean determineIfNew(Entity entity) throws Exception {

        if (((ManagtEmbedModule) entity).getManagt() == null || ((ManagtEmbedModule) entity).getManagt().getManagtDt() == null) return false;
        // 최종수정 이후 7일 지난 글은 새 글이 아님
        if (!((ManagtEmbedModule) entity).getManagt().getManagtDt().after(DateUtils.getCurrDateAddDay(-7))) return false;
        // 내가 최종수정자면 false
        if (AuthUtils.isRegstr(((ManagtEmbedModule) entity).getManagt().getManagtrId())) return false;
        // 열람자에 내가 없으면 true
        if (((ViewerEmbedModule) entity).getViewer() == null || CollectionUtils.isEmpty(((ViewerEmbedModule) entity).getViewer().getList())) return true;
        return ((ViewerEmbedModule) entity).getViewer().getList().stream()
                .anyMatch(e -> !AuthUtils.getLgnUserId().equals(e.getRegstrId()));
    }

}