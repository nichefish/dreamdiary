package io.nicheblog.dreamdiary.web.service.schdul;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseClsfService;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulEntity;
import io.nicheblog.dreamdiary.web.mapstruct.schdul.SchdulMapstruct;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulDto;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulPrtcpntDto;
import io.nicheblog.dreamdiary.web.repository.schdul.jpa.SchdulRepository;
import io.nicheblog.dreamdiary.web.spec.schdul.SchdulSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * SchdulService
 * <pre>
 *  일정 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("schdulService")
@RequiredArgsConstructor
@Log4j2
public class SchdulService
        implements BaseClsfService<SchdulDto, SchdulDto, Integer, SchdulEntity, SchdulRepository, SchdulSpec, SchdulMapstruct> {

    private final SchdulRepository schdulRepository;
    private final SchdulSpec schdulSpec;
    private final SchdulMapstruct schdulMapstruct = SchdulMapstruct.INSTANCE;

    @Override
    public SchdulRepository getRepository() {
        return this.schdulRepository;
    }

    @Override
    public SchdulSpec getSpec() {
        return this.schdulSpec;
    }

    @Override
    public SchdulMapstruct getMapstruct() {
        return this.schdulMapstruct;
    }

    /**
     * 일정관리 > 일정 등록 전처리
     * Clears Cache : hldyEntityList, isHldy, isHldyOrWeekend
     */
    @Override
    @CacheEvict(value = {"hldyEntityList", "isHldy", "isHldyOrWeekend"}, allEntries = true)
    public void preRegist(final SchdulDto schdulDto) throws Exception {
        // 종료일자 없을시 자동으로 시작일자와 같게 처리
        if (StringUtils.isEmpty(schdulDto.getEndDt())) schdulDto.setEndDt(schdulDto.getBgnDt());

        // 개인 일정시 = '나' 자동으로 넣어줌 :: 메소드 분리
        if (schdulDto.getIsPrvt()) this.setMeToSchdul(schdulDto);
    }

    /**
     * 일정관리 > 일정 수정 전처리
     */
    @Override
    public void preModify(final SchdulDto schdulDto) throws Exception {
        // 종료일자 없을시 자동으로 시작일자와 같게 처리
        if (StringUtils.isEmpty(schdulDto.getEndDt())) schdulDto.setEndDt(schdulDto.getBgnDt());

        // 개인 일정시 = '나' 자동으로 넣어줌 :: 메소드 분리
        if (schdulDto.getIsPrvt()) this.setMeToSchdul(schdulDto);
    }

    /**
     * 스케줄에 '나' 포함시키기 :: 메소드 분리
     */
    public void setMeToSchdul(final SchdulDto schdulDto) {
        List<SchdulPrtcpntDto> prtcpntList = schdulDto.getPrtcpntList();
        if (CollectionUtils.isEmpty(prtcpntList)) prtcpntList = new ArrayList<>();
        // 내이름 있는지 체크
        SchdulPrtcpntDto isMe = new SchdulPrtcpntDto(AuthUtils.getLgnUserId());
        if (!prtcpntList.contains(isMe)) prtcpntList.add(isMe);
        schdulDto.setPrtcpntList(prtcpntList);
    }

    /**
     * 일정관리 > 일정 수정
     * Clears Cache : hldyEntityList, isHldy, isHldyOrWeekend
     */
    @Override
    @CacheEvict(value = {"hldyEntityList", "isHldy", "isHldyOrWeekend"}, allEntries = true)
    public SchdulDto modify(final SchdulDto schdulDto) throws Exception {
        // 수정 전처리
        this.preModify(schdulDto);

        SchdulEntity schdulEntity = this.getDtlEntity(schdulDto);       // Entity 레벨 조회
        boolean wasSingleDate = DateUtils.isSameDay(schdulEntity.getBgnDt(), schdulEntity.getEndDt());
        boolean isInvalidEndDate = schdulDto.getBgnDt()
                                            .compareTo(schdulDto.getEndDt()) > 0;
        if (wasSingleDate || isInvalidEndDate) schdulDto.setEndDt(schdulDto.getBgnDt());
        schdulMapstruct.updateFromDto(schdulDto, schdulEntity);
        // update
        SchdulEntity rsltEntity = this.updt(schdulEntity);
        SchdulDto rsltDto = schdulMapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess(rsltEntity.getPostNo() != null);
        return rsltDto;
    }

    /**
     * DB에 저장된 공휴일 정보 조회
     */
    @Cacheable(cacheNames = "hldyEntityList")
    public List<SchdulEntity> getHldyEntityList() throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("schdulCd", Constant.SCHDUL_HLDY);
        }};
        return this.getListEntity(searchParamMap);
    }

    /**
     * 공휴일여부 반환
     */
    @Cacheable(cacheNames = "isHldy", key = "#date")
    public Boolean isHldy(final Object date) throws Exception {
        // 공휴일 여부 체크
        Date asDate = DateUtils.asDate(date);
        Date sDate = DateUtils.Parser.sDateParse(asDate);
        Optional<SchdulEntity> schdulDtlWrapper = schdulRepository.findBySchdulCdAndBgnDt(Constant.SCHDUL_HLDY, sDate);
        return schdulDtlWrapper.isPresent();
    }

    /**
     * 공휴일 또는 주말여부 반환
     */
    @Cacheable(cacheNames = "isHldyOrWeekend", key = "#date")
    public Boolean isHldyOrWeekend(final Object date) throws Exception {
        return (this.isHldy(date) || DateUtils.isWeekend(date));
    }

    /**
     * 이번달의 첫 번째 평일 반환
     */
    public Date getFirstBsnsDayInCurrMnth() throws Exception {
        Date keyDt = DateUtils.asDate(DateUtils.getCurrYyMnthStr() + "01");
        while (true) {
            if (!this.isHldyOrWeekend(keyDt)) return keyDt;
            keyDt = DateUtils.addDays(keyDt, 1);
        }
    }

    /**
     * 이번달의 첫 번째 평일 여부 반환
     */
    public boolean isFirstBsnsDayInCurrMnth() throws Exception {
        Date firstBsnsDayInCurrMnth = getFirstBsnsDayInCurrMnth();
        Date today = DateUtils.getCurrDate();
        return DateUtils.isSameDay(firstBsnsDayInCurrMnth, today);
    }
}