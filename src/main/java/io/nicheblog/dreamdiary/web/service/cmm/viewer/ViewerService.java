package io.nicheblog.dreamdiary.web.service.cmm.viewer;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.web.entity.cmm.viewer.ViewerEntity;
import io.nicheblog.dreamdiary.web.repository.cmm.viewer.jpa.ViewerRepository;
import io.nicheblog.dreamdiary.web.spec.cmm.viewer.ViewerSpec;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * ViewerService
 * <pre>
 *  컨텐츠  게시물 열람자 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("ViewerService")
@Log4j2
public class ViewerService {

    @Resource(name = "viewerRepository")
    private ViewerRepository viewerRepository;
    @Resource(name = "viewerSpec")
    private ViewerSpec viewerSpec;

    /**
     * 게시물 열람자 존재여부 (기 방문여부) 체크
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
     * 게시물 열람자 등록
     */
    public void addViewer(final BaseClsfKey key) {
        if (this.hasAlreadyVisited(key)) return;
        ViewerEntity viewer = new ViewerEntity(key);
        viewerRepository.save(viewer);
    }
}
