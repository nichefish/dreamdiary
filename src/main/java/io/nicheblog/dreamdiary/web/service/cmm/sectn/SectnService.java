package io.nicheblog.dreamdiary.web.service.cmm.sectn;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.sectn.SectnEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.sectn.SectnMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.sectn.SectnDto;
import io.nicheblog.dreamdiary.web.repository.cmm.sectn.jpa.SectnRepository;
import io.nicheblog.dreamdiary.web.spec.cmm.sectn.SectnSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * SectnService
 * <pre>
 *  단락 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("sectnService")
@Log4j2
public class SectnService
        implements BaseMultiCrudService<SectnDto, SectnDto, Integer, SectnEntity, SectnRepository, SectnSpec, SectnMapstruct> {

    private final SectnMapstruct sectnMapstruct = SectnMapstruct.INSTANCE;

    @Resource(name = "sectnRepository")
    private SectnRepository sectnRepository;
    @Resource(name = "sectnSpec")
    private SectnSpec sectnSpec;

    @Override
    public SectnRepository getRepository() {
        return this.sectnRepository;
    }
    @Override
    public SectnMapstruct getMapstruct() {
        return this.sectnMapstruct;
    }
    @Override
    public SectnSpec getSpec() {
        return this.sectnSpec;
    }

    /**
     * 등록 후처리 :: override
     */
    @Override
    public void postRegist(final SectnEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictClsfCache(rslt);
    }

    /**
     * 수정 후처리 :: override
     */
    @Override
    public void postModify(final SectnEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictClsfCache(rslt);
    }

    /**
     * 삭제 후처리 :: override
     */
    @Override
    public void postDelete(final SectnEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictClsfCache(rslt);

        // TODO: 관련 엔티티 삭제?
    }

    /**
     * 관련 캐시 처리
     */
    public void evictClsfCache(final SectnEntity rslt) {
        String refContentType = rslt.getRefContentType();
        if (ContentType.JRNL_DIARY.key.equals(refContentType)) {
            // jrnl_day
            EhCacheUtils.evictCacheAll("jrnlDayList");
            // jrnl_diary
            EhCacheUtils.evictCache("jrnlDiaryDtlDto", rslt.getRefPostNo());
            EhCacheUtils.evictCacheAll("imprtcDiaryList");
        }
        if (ContentType.JRNL_DREAM.key.equals(refContentType)) {
            // jrnl_day
            EhCacheUtils.evictCacheAll("jrnlDayList");
            // jrnl_dream
            EhCacheUtils.evictCache("jrnlDreamDtlDto", rslt.getRefPostNo());
            EhCacheUtils.evictCacheAll("imprtcDreamList");
        }

        EhCacheUtils.clearL2Cache(SectnEntity.class);
    }
}