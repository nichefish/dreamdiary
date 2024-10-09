package io.nicheblog.dreamdiary.domain._core.managt.service;

import io.nicheblog.dreamdiary.domain._core.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.domain._core.managt.entity.ManagtrEntity;
import io.nicheblog.dreamdiary.domain._core.managt.repository.jpa.ManagtrRepository;
import io.nicheblog.dreamdiary.domain._core.managt.spec.ManagtrSpec;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ManagtrService
 * <pre>
 *  조치자 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("managtrService")
@RequiredArgsConstructor
@Log4j2
public class ManagtrService {

    private final ManagtrRepository managtrRepository;
    private final ManagtrSpec managtrSpec;

    /**
     * 게시물 열람자 존재여부 (기 방문여부) 체크
     * @param key - 확인할 게시물의 키 정보
     * @return Boolean - 사용자가 이미 방문했으면 true, 그렇지 않으면 false
     */
    public Boolean hasAlreadyVisited(final BaseClsfKey key) {
        Map<String, Object> searchParamMap = new HashedMap<>() {{
            put("regstrId", AuthUtils.getLgnUserId());
            put("refPostNo", key.getPostNo());
            put("refContentType", key.getContentType());
        }};
        List<ManagtrEntity> managtrList = managtrRepository.findAll(managtrSpec.searchWith(searchParamMap));
        return CollectionUtils.isNotEmpty(managtrList);
    }

    /**
     * 게시물 열람자 등록
     * @param key - 등록할 게시물의 키 정보
     */
    public void addManagtr(final BaseClsfKey key) {
        if (this.hasAlreadyVisited(key)) return;
        ManagtrEntity managtr = new ManagtrEntity(key);
        managtrRepository.save(managtr);
    }
}
