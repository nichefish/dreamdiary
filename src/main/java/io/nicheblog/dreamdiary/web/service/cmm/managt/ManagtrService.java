package io.nicheblog.dreamdiary.web.service.cmm.managt;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.web.entity.cmm.managt.ManagtrEntity;
import io.nicheblog.dreamdiary.web.repository.cmm.managt.ManagtrRepository;
import io.nicheblog.dreamdiary.web.spec.cmm.managt.ManagtrSpec;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * ManagtrService
 * <pre>
 *  조치자 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("managtrService")
@Log4j2
public class ManagtrService {

    @Resource(name = "managtrRepository")
    private ManagtrRepository managtrRepository;
    @Resource(name = "managtrSpec")
    private ManagtrSpec managtrSpec;

    /**
     * 게시물 열람자 존재여부 (기 방문여부) 체크
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
     */
    public void addManagtr(final BaseClsfKey key) {
        if (this.hasAlreadyVisited(key)) return;
        ManagtrEntity managtr = new ManagtrEntity(key);
        managtrRepository.save(managtr);
    }
}
