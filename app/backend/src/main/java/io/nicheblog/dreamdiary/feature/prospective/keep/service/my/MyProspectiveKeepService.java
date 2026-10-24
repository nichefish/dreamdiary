package io.nicheblog.dreamdiary.feature.prospective.keep.service.my;

import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepDto;
import io.nicheblog.dreamdiary.feature.prospective.keep.model.ProspectiveKeepSearchParam;
import io.nicheblog.dreamdiary.feature.prospective.keep.service.ProspectiveKeepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MyProspectiveKeepService
 * <pre>
 *  로그인 사용자 기준 Prospective Keep 서비스
 * </pre>
 *
 * @author nichefish
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class MyProspectiveKeepService {

    private final ProspectiveKeepService prospectiveKeepService;

    /**
     * 활성 Keep 목록 조회 (dto level).
     *
     * @param searchParam 검색조건을 담고 있는 파라미터 객체
     * @return {@link List} -- 활성 Keep 목록
     */
    public List<ProspectiveKeepDto> getMyActiveList(final ProspectiveKeepSearchParam searchParam) throws Exception {
        final String username = AuthUtils.requireLoginUsername();
        return prospectiveKeepService.getActiveListDtoByUser(username, searchParam);
    }

    /**
     * 전 범위 Keep 목록 조회 (dto level).
     * <pre>
     *  keep 전수관리 화면용 -- 활성(OPEN·PENDING)뿐 아니라 RESOLVED 까지 포함해
     *  사용자의 모든 Keep을 발생지 무관하게 반환한다.
     * </pre>
     *
     * @param searchParam 검색조건을 담고 있는 파라미터 객체
     * @return {@link List} -- 사용자 전 범위 Keep 목록
     */
    public List<ProspectiveKeepDto> getMyList(final ProspectiveKeepSearchParam searchParam) throws Exception {
        final String username = AuthUtils.requireLoginUsername();
        return prospectiveKeepService.getListDtoWithLifecycleByUser(username, searchParam);
    }

    /**
     * 상세 조회 (dto level) :: 본문 캐시 + lifecycle 부착
     * <pre>
     *  lifecycle 은 목록과 같은 방식(행이 없으면 OPEN)으로 매 조회 부착한다.
     * </pre>
     *
     * @param key 일련번호
     * @return {@link ProspectiveKeepDto} -- 조회된 객체 (lifecycle 부착)
     */
    public ProspectiveKeepDto getMyDetailDtoWithLifecycle(final Integer key) throws Exception {
        final String username = AuthUtils.requireLoginUsername();
        return prospectiveKeepService.getDetailDtoWithLifecycleByUser(username, key);
    }
}
