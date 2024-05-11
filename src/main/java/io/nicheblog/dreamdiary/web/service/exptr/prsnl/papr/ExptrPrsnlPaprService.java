package io.nicheblog.dreamdiary.web.service.exptr.prsnl.papr;

import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.cmm.file.mapstruct.AtchFileDtlMapstruct;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlItemEntity;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.web.mapstruct.exptr.prsnl.ExptrPrsnlPaprMapstruct;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.web.repository.exptr.prsnl.ExptrPrsnlPaprRepository;
import io.nicheblog.dreamdiary.web.spec.exptr.prsnl.ExptrPrsnlPaprSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * ExptrPrsnlPaprService
 * <pre>
 *  경비 관리 > 경비지출서 관리 서비스 모듈
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @implements BasePostService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service
@Log4j2
public class ExptrPrsnlPaprService
        implements BasePostService<ExptrPrsnlPaprDto.DTL, ExptrPrsnlPaprDto.LIST, Integer, ExptrPrsnlPaprEntity, ExptrPrsnlPaprRepository, ExptrPrsnlPaprSpec, ExptrPrsnlPaprMapstruct> {

    @Resource(name = "exptrPrsnlPaprRepository")
    private ExptrPrsnlPaprRepository exptrPrsnlPaprRepository;
    @Resource(name = "exptrPrsnlPaprSpec")
    private ExptrPrsnlPaprSpec exptrPrsnlPaprSpec;

    private final ExptrPrsnlPaprMapstruct exptrPrsnlPaprMapstruct = ExptrPrsnlPaprMapstruct.INSTANCE;
    private final AtchFileDtlMapstruct atchFileDtlMapstruct = AtchFileDtlMapstruct.INSTANCE;

    @Override
    public ExptrPrsnlPaprRepository getRepository() {
        return this.exptrPrsnlPaprRepository;
    }

    @Override
    public ExptrPrsnlPaprSpec getSpec() {
        return this.exptrPrsnlPaprSpec;
    }

    @Override
    public ExptrPrsnlPaprMapstruct getMapstruct() {
        return this.exptrPrsnlPaprMapstruct;
    }

    /**
     * 공지사항 > 공지사항 목록 Page<Entity>->Page<Dto> 변환
     */
    @Override
    public Page<ExptrPrsnlPaprDto.LIST> pageEntityToDto(final Page<ExptrPrsnlPaprEntity> entityPage) throws Exception {
        List<ExptrPrsnlPaprDto.LIST> dtoList = new ArrayList<>();
        int i = 0;
        for (ExptrPrsnlPaprEntity entity : entityPage.getContent()) {
            ExptrPrsnlPaprDto.LIST listDto = exptrPrsnlPaprMapstruct.toListDto(entity);
            List<ExptrPrsnlItemEntity> itemList = entity.getItemList();
            listDto.setRnum(CmmUtils.getPageRnum(entityPage, i));
            dtoList.add(listDto);
            i++;
        }
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 기존(이전달, 이번달) 정보 존재여부 조회
     */
    public Map<String, Object> exptrPrsnlExistingChck() throws Exception {
        String userId = Objects.requireNonNull(AuthUtils.getAuthenticatedUser())
                               .getUserId();
        Map<String, Object> resultMap = new HashMap<>();
        // 이전달 것 조회
        List<ExptrPrsnlPaprEntity> entityList = exptrPrsnlPaprRepository.findAll(exptrPrsnlPaprSpec.searchWith(userId, DateUtils.getPrevYyMnth()));
        boolean hasPrevMnth = !CollectionUtils.isEmpty(entityList);
        if (hasPrevMnth) resultMap.put("prevMnth", exptrPrsnlPaprMapstruct.toDto(entityList.get(0)));
        // 이번달 것 조회
        entityList = exptrPrsnlPaprRepository.findAll(exptrPrsnlPaprSpec.searchWith(userId, DateUtils.getCurrYyMnth()));
        boolean hasCurrMnth = !CollectionUtils.isEmpty(entityList);
        if (hasCurrMnth) resultMap.put("currMnth", exptrPrsnlPaprMapstruct.toDto(entityList.get(0)));
        // 구체적인 로직은 뷰단에서 처리
        return resultMap;
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 기존(이전달, 이번달) 정보 존재여부 조회
     */
    public BasePostDto exptrPrsnlYyMnthChck(
            final Integer yy,
            final Integer mnth
    ) throws Exception {
        String userId = Objects.requireNonNull(AuthUtils.getAuthenticatedUser())
                               .getUserId();
        // 해당 년, 월 것 조회
        List<ExptrPrsnlPaprEntity> entityList = exptrPrsnlPaprRepository.findAll(exptrPrsnlPaprSpec.searchWith(userId, new Integer[]{yy, mnth}));
        if (CollectionUtils.isEmpty(entityList)) return null;
        ExptrPrsnlPaprEntity entity = entityList.get(0);
        if ("Y".equals(entity.getCfYn())) {
            return new ExptrPrsnlPaprDto("Y");      // cfYn="Y"로 넘겨줘서 아예 없는 상황/취합완료상황 구분
        }
        return ExptrPrsnlPaprMapstruct.INSTANCE.toDto(entityList.get(0));
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 등록 전처리 :: override
     */
    @Override
    public void preRegist(final ExptrPrsnlPaprDto.DTL exptrPrsnlPaprDto) {
        // 빈 지출항목 걸러내기
        exptrPrsnlPaprDto.filterEmptyItems();
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 수정 전처리
     */
    @Override
    public void preModify(final ExptrPrsnlPaprDto.DTL exptrPrsnlPaprDto) {
        // 빈 지출항목 걸러내기
        exptrPrsnlPaprDto.filterEmptyItems();
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서가 존재하는 년도 조회 (distinct 처리)
     */
    public List<String> getExptrPrsnlYyList() throws Exception {
        String minYyStr = exptrPrsnlPaprRepository.selectMinYy();
        int minYy = (minYyStr != null) ? Integer.parseInt(minYyStr) : DateUtils.getCurrYy();
        List<String> yyList = new ArrayList<>();
        int currYy = DateUtils.getCurrYy();
        for (int yy = minYy - 1; yy <= currYy; yy++) {     // 최저년도~현재년도. 현재년도는 항상 들어가도록
            yyList.add(Integer.toString(yy));
        }
        return yyList;
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 영수증 이미지 파일 목록 조회
     */
    public List<AtchFileDtlDto> getExptrPrsnlRciptList(final Integer postKey) throws Exception {
        // Entity 레벨 조회
        ExptrPrsnlPaprEntity exptrEntity = this.getDtlEntity(postKey);
        List<ExptrPrsnlItemEntity> itemList = exptrEntity.getItemList();
        List<AtchFileDtlDto> atchFileDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(itemList)) {
            for (ExptrPrsnlItemEntity item : itemList) {
                if ("Y".equals(item.getRjectYn()) || item.getAtchFileDtlNo() == null) continue;
                AtchFileDtlEntity fileDtlEntity = item.getAtchFileDtlInfo();
                if (fileDtlEntity != null) {
                    atchFileDtoList.add(atchFileDtlMapstruct.toDto(fileDtlEntity));
                }
            }
        }
        return atchFileDtoList;
    }
}
