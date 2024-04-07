package io.nicheblog.dreamdiary.web.repository.cmm.flsys;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.flsys.FlsysMetaEntity;
import org.springframework.stereotype.Repository;

/**
 * FlsysMetaRepository
 * <pre>
 *  파일시스템 메타정보 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("flsysMetaRepository")
public interface FlsysMetaRepository
        extends BaseStreamRepository<FlsysMetaEntity, BaseClsfKey> {
    //
}