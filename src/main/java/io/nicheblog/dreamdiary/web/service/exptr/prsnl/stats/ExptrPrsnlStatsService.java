package io.nicheblog.dreamdiary.web.service.exptr.prsnl.stats;

import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.stats.ExptrPrsnlStatsDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.repository.exptr.prsnl.ExptrPrsnlPaprRepository;
import io.nicheblog.dreamdiary.web.service.exptr.prsnl.papr.ExptrPrsnlPaprService;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExptrPrsnlStatsService
 * <pre>
 *  경비 관리 > 경비지출서 통계 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("exptrPrsnlStatsService")
@Log4j2
public class ExptrPrsnlStatsService {

    @Resource(name = "exptrPrsnlPaprService")
    private ExptrPrsnlPaprService exptrPrsnlPaprService;

    @Resource(name = "exptrPrsnlPaprRepository")
    private ExptrPrsnlPaprRepository exptrPrsnlPaprRepository;

    @Resource(name = "userService")
    private UserService userService;

    /**
     * 경비 관리 > 경비지출서 > 올해년도에 근무이력이 있는(중도퇴사 포함) 모든 직원(재직+프리랜서) 전원에 대하여 통계 산정
     */
    public List<ExptrPrsnlStatsDto> getExptrPrsnlStatsList(final String yyParam) throws Exception {
        String yyStr = !StringUtils.isEmpty(yyParam) ? yyParam : DateUtils.getCurrYearStr();
        List<ExptrPrsnlStatsDto> list = new ArrayList<>();

        List<UserDto.LIST> userList = userService.getCrdtUserList(yyStr)
                                                .getContent();

        Map<String, Object> searchParamMap = new HashMap<>();
        searchParamMap.put("yy", yyStr);
        for (UserDto.LIST user : userList) {
            List<ExptrPrsnlPaprDto.LIST> exptrPrsnlList = new ArrayList<>();
            searchParamMap.put("regstrId", user.getUserId());
            List<ExptrPrsnlPaprDto.LIST> exptrList = exptrPrsnlPaprService.getListDto(searchParamMap);
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
            list.add(exptrPrsnlStats);
        }
        return list;
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 취합완료 처리
     */
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