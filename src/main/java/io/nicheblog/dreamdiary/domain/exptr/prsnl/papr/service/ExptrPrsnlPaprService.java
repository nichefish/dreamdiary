package io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.service;

import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlItemEntity;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.mapstruct.ExptrPrsnlPaprMapstruct;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.model.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.repository.jpa.ExptrPrsnlPaprRepository;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.spec.ExptrPrsnlPaprSpec;
import io.nicheblog.dreamdiary.global._common.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global._common.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global._common.file.mapstruct.AtchFileDtlMapstruct;
import io.nicheblog.dreamdiary.global._common.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ExptrPrsnlPaprService
 * <pre>
 *  경비 관리 > 경비지출서 관리 서비스 모듈.
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @implements BasePstService
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ExptrPrsnlPaprService
        implements BasePostService<ExptrPrsnlPaprDto.DTL, ExptrPrsnlPaprDto.LIST, Integer, ExptrPrsnlPaprEntity, ExptrPrsnlPaprRepository, ExptrPrsnlPaprSpec, ExptrPrsnlPaprMapstruct> {

    @Getter
    private final ExptrPrsnlPaprRepository repository;
    @Getter
    private final ExptrPrsnlPaprSpec spec;
    @Getter
    private final ExptrPrsnlPaprMapstruct mapstruct = ExptrPrsnlPaprMapstruct.INSTANCE;

    private final AtchFileDtlMapstruct atchFileDtlMapstruct = AtchFileDtlMapstruct.INSTANCE;

    /**
     * 목록 Page<Entity> -> Page<Dto> 변환 (override)
     *
     * @param entityPage 페이징 처리된 Entity 목록
     * @return {@link Page} -- 변환된 페이징 처리된 Dto 목록
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public Page<ExptrPrsnlPaprDto.LIST> pageEntityToDto(final Page<ExptrPrsnlPaprEntity> entityPage) throws Exception {
        List<ExptrPrsnlPaprDto.LIST> dtoList = new ArrayList<>();
        int i = 0;
        for (ExptrPrsnlPaprEntity entity : entityPage.getContent()) {
            ExptrPrsnlPaprDto.LIST listDto = mapstruct.toListDto(entity);
            listDto.setRnum(CmmUtils.getPageRnum(entityPage, i));
            dtoList.add(listDto);
            i++;
        }
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    /**
     * 경비지출서 기존(이전달, 이번달) 정보 존재여부를 맵에 담아 반환
     * 
     * @return {@link Map} -- 결과 맵
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Transactional(readOnly = true)
    public Map<String, Object> exptrPrsnlExistingChck() throws Exception {
        String userId = Objects.requireNonNull(AuthUtils.getAuthenticatedUser())
                               .getUserId();
        Map<String, Object> resultMap = new HashMap<>();
        // 이전달 것 조회
        List<ExptrPrsnlPaprEntity> entityList = repository.findAll(spec.searchWith(userId, DateUtils.getPrevYyMnth()));
        boolean hasPrevMnth = !CollectionUtils.isEmpty(entityList);
        if (hasPrevMnth) resultMap.put("prevMnth", mapstruct.toDto(entityList.get(0)));
        // 이번달 것 조회
        entityList = repository.findAll(spec.searchWith(userId, DateUtils.getCurrYyMnth()));
        boolean hasCurrMnth = !CollectionUtils.isEmpty(entityList);
        if (hasCurrMnth) resultMap.put("currMnth", mapstruct.toDto(entityList.get(0)));
        // 구체적인 로직은 뷰단에서 처리
        return resultMap;
    }

    /**
     * 경비지출서 기존(이전달, 이번달) 정보 존재여부 조회
     *
     * @param yy 조회할 연도 (Integer)
     * @param mnth 조회할 월 (Integer)
     * @return {@link ExptrPrsnlPaprDto} -- 경비지출서 정보 조회
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Transactional(readOnly = true)
    public ExptrPrsnlPaprDto exptrPrsnlYyMnthChck(final Integer yy, final Integer mnth) throws Exception {
        String userId = Objects.requireNonNull(AuthUtils.getAuthenticatedUser())
                               .getUserId();
        // 해당 년, 월 것 조회
        List<ExptrPrsnlPaprEntity> entityList = repository.findAll(spec.searchWith(userId, new Integer[]{yy, mnth}));
        if (CollectionUtils.isEmpty(entityList)) return null;
        ExptrPrsnlPaprEntity entity = entityList.get(0);
        if ("Y".equals(entity.getCfYn())) {
            return new ExptrPrsnlPaprDto("Y");      // cfYn="Y"로 넘겨줘서 아예 없는 상황/취합완료상황 구분
        }
        return ExptrPrsnlPaprMapstruct.INSTANCE.toDto(entityList.get(0));
    }

    /**
     * 등록 전처리. (override)
     *
     * @param dto 등록할 객체
     */
    @Override
    public void preRegist(final ExptrPrsnlPaprDto.DTL dto) {
        // 빈 지출항목 걸러내기
        dto.filterEmptyItems();
    }

    /**
     * 수정 전처리. (override)
     *
     * @param exptrPrsnlPapr 수정할 객체
     */
    @Override
    public void preModify(final ExptrPrsnlPaprDto.DTL exptrPrsnlPapr) {
        // 빈 지출항목 걸러내기
        exptrPrsnlPapr.filterEmptyItems();
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서가 존재하는 년도 조회 (distinct 처리)
     *
     * @return {@link List} -- 경비지출서가 존재하는 연도 목록 (List<String>)
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Transactional(readOnly = true)
    public List<String> getExptrPrsnlYyList() throws Exception {
        String minYyStr = repository.selectMinYy();
        int minYy = (minYyStr != null) ? Integer.parseInt(minYyStr) : DateUtils.getCurrYy();
        List<String> yyList = IntStream.rangeClosed(minYy, DateUtils.getCurrYy())
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        return yyList;
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 영수증 이미지 파일 목록 조회
     *
     * @param key 경비지출서 번호
     * @return {@link List} -- 경비지출서 영수증 이미지 파일 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Transactional(readOnly = true)
    public List<AtchFileDtlDto> getExptrPrsnlRciptList(final Integer key) throws Exception {
        // Entity 레벨 조회
        ExptrPrsnlPaprEntity exptrEntity = this.getDtlEntity(key);
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
