package io.nicheblog.dreamdiary.global.handler;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditRegEntity;

import javax.persistence.PostLoad;

/**
 * AuditEntityListener
 */
public class AuditEntityListener {

    @PostLoad
    public void onPostLoad(BaseAuditRegEntity entity) {
        if (entity.getRegstrInfo() != null) entity.setRegstrNm(entity.getRegstrInfo().getNickNm());        // 로드 후 처리 로직
    }

    @PostLoad
    public void onPostLoad(BaseAuditEntity entity) {
        if (entity.getMdfusrInfo() != null) entity.setMdfusrNm(entity.getMdfusrInfo().getNickNm());        // 로드 후 처리 로직
    }
}