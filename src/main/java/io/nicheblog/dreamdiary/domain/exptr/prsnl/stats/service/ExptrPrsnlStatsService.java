package io.nicheblog.dreamdiary.domain.exptr.prsnl.stats.service;

import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.model.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.repository.jpa.ExptrPrsnlPaprRepository;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.service.ExptrPrsnlPaprService;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.stats.model.ExptrPrsnlStatsDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.stats.model.ExptrPrsnlStatsSearchParam;
import io.nicheblog.dreamdiary.domain.user.info.model.UserDto;
import io.nicheblog.dreamdiary.domain.user.info.service.UserService;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ExptrPrsnlStatsService
 * <pre>
 *  경비 관리 > 경비지출서 통계 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("exptrPrsnlStatsService")
@RequiredArgsConstructor
@Log4j2
public class ExptrPrsnlStatsService {

    private final ExptrPrsnlPaprService exptrPrsnlPaprService;
    private final ExptrPrsnlPaprRepository exptrPrsnlPaprRepository;
    private final UserService userService;

    /**
     * 경비 관리 > 경비지출서 > 올해년도에 근무이력이 있는(중도퇴사 포함) 모든 직원(재직+프리랜서) 전원에 대하여 통계 산정
     *
     * @param yyParam 조회할 연도 (String), 연도가 없으면 현재 연도를 사용
     * @return {@link List} -- 모든 직원의 경비지출서 통계 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    public List<ExptrPrsnlStatsDto> getExptrPrsnlStatsList(final String yyParam) throws Exception {
        String yyStr = !StringUtils.isEmpty(yyParam) ? yyParam : DateUtils.getCurrYyStr();

        List<UserDto.LIST> userList = userService.getCrdtUserList(yyStr);
        if (CollectionUtils.isEmpty(userList)) return new ArrayList<>();

        List<ExptrPrsnlStatsDto> statsList = new ArrayList<>();
        ExptrPrsnlStatsSearchParam searchParam = ExptrPrsnlStatsSearchParam.builder().yy(yyStr).build();
        for (UserDto.LIST user : userList) {
            List<ExptrPrsnlPaprDto.LIST> exptrPrsnlList = new ArrayList<>();
            searchParam.setRegstrId(user.getUserId());
            List<ExptrPrsnlPaprDto.LIST> exptrList = exptrPrsnlPaprService.getListDto(searchParam);
            for (int i = 1; i <= 12; i++) {
                if (CollectionUtils.isEmpty(exptrList)) {
                    exptrPrsnlList.add(null);
                    continue;
                }
                for (ExptrPrsnlPaprDto.LIST exptr : exptrList) {
                    if (Integer.parseInt(exptr.getMnth()) == i) {
                        exptrPrsnlList.add(exptr);
                        break;
                    }
                }
                if (exptrPrsnlList.size() < i) exptrPrsnlList.add(null);
            }
            ExptrPrsnlStatsDto exptrPrsnlStats = new ExptrPrsnlStatsDto(user, exptrPrsnlList);
            statsList.add(exptrPrsnlStats);
        }
        return statsList;
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 취합완료 처리
     *
     * @param key 경비지출서 번호
     * @return {@link Boolean} -- 처리 성공 여부
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    public Boolean exptrPrsnlStatsCompt(final Integer key) throws Exception {
        // Entity 레벨 조회
        ExptrPrsnlPaprEntity exptrEntity = exptrPrsnlPaprService.getDtlEntity(key);
        exptrEntity.setCfYn("Y");
        // update
        ExptrPrsnlPaprEntity rsltEntity = exptrPrsnlPaprRepository.save(exptrEntity);
        return (rsltEntity.getPostNo() != null);
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 통계 정보 엑셀 다운로드
     *
     * @param yyStr 조회할 연도
     * @return {@link List} -- 엑셀 다운로드용 경비지출서 통계 정보 목록
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public List<Object> getExptrPrsnlStatsListXlsx(final String yyStr) throws Exception {
        List<ExptrPrsnlStatsDto> statsList = this.getExptrPrsnlStatsList(yyStr);

        // List<Dto> -> List<ListXlsxDto>
        List<Object> statsObjList = new ArrayList<>();
        for (ExptrPrsnlStatsDto stats : statsList) {
            // statsObjList.add(exptrPrsnlMapstruct.toListXlsxDto(stats));
        }
        return statsObjList;
    }
}