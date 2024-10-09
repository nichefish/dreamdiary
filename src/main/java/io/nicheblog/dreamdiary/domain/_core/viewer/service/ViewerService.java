package io.nicheblog.dreamdiary.domain._core.viewer.service;

import io.nicheblog.dreamdiary.domain._core.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.domain._core.viewer.entity.ViewerEntity;
import io.nicheblog.dreamdiary.domain._core.viewer.repository.jpa.ViewerRepository;
import io.nicheblog.dreamdiary.domain._core.viewer.spec.ViewerSpec;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ViewerService
 * <pre>
 *  컨텐츠 열람자 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("ViewerService")
@RequiredArgsConstructor
public class ViewerService {

    private final ViewerRepository viewerRepository;
    private final ViewerSpec viewerSpec;

    /**
     * 게시물 열람자 존재 여부(기 방문 여부)를 체크합니다.
     * @param key - 게시물의 고유 키
     * @return 이미 방문한 경우 true, 그렇지 않은 경우 false
     */
    public Boolean hasAlreadyVisited(final BaseClsfKey key) {
        Map<String, Object> searchParamMap = new HashedMap<>() {{
            put("regstrId", AuthUtils.getLgnUserId());
            put("refPostNo", key.getPostNo());
            put("refContentType", key.getContentType());
        }};
        List<ViewerEntity> viewerList = viewerRepository.findAll(viewerSpec.searchWith(searchParamMap));
        return CollectionUtils.isNotEmpty(viewerList);
    }

    /**
     * 게시물 열람자를 등록합니다. (이미 방문한 경우에는 등록하지 않습니다.)
     * @param key - 게시물의 고유 키
     */
    public void addViewer(final BaseClsfKey key) {
        if (this.hasAlreadyVisited(key)) return;
        ViewerEntity viewer = new ViewerEntity(key);
        viewerRepository.save(viewer);
    }
}
