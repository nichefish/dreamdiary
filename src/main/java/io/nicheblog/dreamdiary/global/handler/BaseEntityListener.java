package io.nicheblog.dreamdiary.global.handler;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditRegEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import org.springframework.util.CollectionUtils;

import javax.persistence.PostLoad;

/**
 * BaseEntityListener
 * <pre>
 *  엔티티 로딩 후 일괄 처리 로직
 *  :: 클래스당 한 개의 이벤트 리스너밖에 적용되지 않기 때문에 적용 보류
 * </pre>
 *
 * @author nichefish
 */
public class BaseEntityListener {
    //
}