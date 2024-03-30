package io.nicheblog.dreamdiary.global.handler;

import io.nicheblog.dreamdiary.global.auth.Auth;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditRegEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;

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

    @PostLoad
    public void onPostLoad(BaseAuditRegEntity entity) {
        entity.setIsRegstr(AuthUtils.isRegstr(entity.getRegstrId()));
        if (entity.getRegstrInfo() != null) entity.setRegstrNm(entity.getRegstrInfo().getNickNm());
    }

    @PostLoad
    public void onPostLoad(BaseAuditEntity entity) {
        entity.setIsMdfusr(AuthUtils.isMdfusr(entity.getMdfusrId()));
        if (entity.getMdfusrInfo() != null) entity.setMdfusrNm(entity.getMdfusrInfo().getNickNm());
    }

    @PostLoad
    private void onPostLoad(BasePostEntity entity) {
        entity.setHasCtgrNm(entity.getCtgrCdInfo() != null);
        if (entity.getHasCtgrNm()) entity.setCtgrNm(entity.getCtgrCdInfo().getDtlCdNm());
    }
}