package io.nicheblog.dreamdiary.global.handler;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditRegEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import org.springframework.util.CollectionUtils;

import javax.persistence.PostLoad;

/**
 * AuditEntityListener
 * <pre>
 *  엔티티 로딩 후 일괄 처리 로직
 * </pre>
 *
 * @author nichefish
 */
public class BaseEntityListener {

    /** BaseAuditRegEntity */
    @PostLoad
    public void onPostLoad(BaseAuditRegEntity entity) {
        // 작성자 이름
        if (entity.getRegstrInfo() != null) entity.setRegstrNm(entity.getRegstrInfo().getNickNm());
        // 작성자 여부
        entity.setIsRegstr(AuthUtils.isRegstr(entity.getRegstrId()));
    }

    /** BaseAuditEntity */
    @PostLoad
    public void onPostLoad(BaseAuditEntity entity) {
        // 수정자 이름
        if (entity.getMdfusrInfo() != null) entity.setMdfusrNm(entity.getMdfusrInfo().getNickNm());
        // 수정자 여부
        entity.setIsMdfusr(AuthUtils.isMdfusr(entity.getMdfusrId()));
    }

    /** BaseAtchEntity */
    @PostLoad
    public void onPostLoad(BaseAtchEntity entity) {
        // 첨부파일 존재 여부
        entity.setHasAtchFile(!(entity.getAtchFileNo() == null || entity.getAtchFileInfo() == null || CollectionUtils.isEmpty(entity.getAtchFileInfo().getAtchFileList())));
    }

    /** BasePostEntity */
    @PostLoad
    private void onPostLoad(BasePostEntity entity) {
        // 글분류 이름
        entity.setHasCtgrNm(entity.getCtgrCdInfo() != null);
        if (entity.getHasCtgrNm()) entity.setCtgrNm(entity.getCtgrCdInfo().getDtlCdNm());
    }
}