package io.nicheblog.dreamdiary.web.service.vcatn.papr;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.file.service.FileService;
import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnPaprEntity;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.web.mapstruct.dream.DreamPieceMapstruct;
import io.nicheblog.dreamdiary.web.mapstruct.vcatn.papr.VcatnPaprMapstruct;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprDto;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprListDto;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnSchdulDto;
import io.nicheblog.dreamdiary.web.model.vcatn.stats.VcatnStatsYyDto;
import io.nicheblog.dreamdiary.web.repository.vcatn.VcatnPaprRepository;
import io.nicheblog.dreamdiary.web.service.vcatn.stats.VcatnStatsYyService;
import io.nicheblog.dreamdiary.web.spec.vcatn.VcatnPaprSpec;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * VcatnPaprService
 * <pre>
 *  휴가계획서 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BasePostService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("vcatnPaprService")
@Log4j2
public class VcatnPaprService
        implements BasePostService<VcatnPaprDto, VcatnPaprListDto, Integer, VcatnPaprEntity, VcatnPaprRepository, VcatnPaprSpec, VcatnPaprMapstruct, FileService> {

    @Resource(name = "vcatnPaprRepository")
    private VcatnPaprRepository vcatnPaprRepository;
    @Resource(name = "vcatnPaprSpec")
    private VcatnPaprSpec vcatnPaprSpec;
    @Resource(name = "fileService")
    private FileService fileService;

    private final VcatnPaprMapstruct vcatnPaprMapstruct = VcatnPaprMapstruct.INSTANCE;

    @Resource(name = "vcatnStatsYyService")
    private VcatnStatsYyService vcatnStatsYyService;
    @Resource(name = "cdService")
    public CdService cmmCdService;

    @Override
    public VcatnPaprRepository getRepository() {
        return this.vcatnPaprRepository;
    }

    @Override
    public VcatnPaprSpec getSpec() {
        return this.vcatnPaprSpec;
    }

    @Override
    public VcatnPaprMapstruct getMapstruct() {
        return this.vcatnPaprMapstruct;
    }

    @Override
    public FileService getFileService() {
        return this.fileService;
    }

    /**
     * 일정  > 휴가계획서 > 휴가계획서 등록 전처리
     */
    @Override
    public void preRegist(final VcatnPaprDto vcatnPaprDto) {
        // 제목 자동 처리
        vcatnPaprDto.setTitle(this.generateTitle(vcatnPaprDto));
    }

    /**
     * 일정  > 휴가계획서 > 휴가계획서 수정 전처리
     */
    @Override
    public void preModify(final VcatnPaprDto vcatnPaprDto) {
        // 제목 자동 처리
        vcatnPaprDto.setTitle(this.generateTitle(vcatnPaprDto));
    }

    /**
     * 제목 자동 처리 :: 메소드 분리
     */
    public String generateTitle(final VcatnPaprDto vcatnPaprDto) {
        List<VcatnSchdulDto> schdulList = vcatnPaprDto.getSchdulList();
        if (CollectionUtils.isEmpty(schdulList)) return "휴가계획서 (날짜없음)";
        return schdulList.stream()
                .map(vcatn -> {
                    String vcatnCd = vcatn.getVcatnCd();
                    String vcatnNm = cmmCdService.getDtlCdNm(Constant.VCATN_CD, vcatnCd);
                    String period = vcatn.getBgnDt();
                    String endDt = vcatn.getEndDt();
                    try {
                        boolean isSameDay = DateUtils.isSameDay(period, endDt) || (endDt == null);
                        if (!isSameDay) {
                            period += "~" + endDt;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return "(" + vcatnNm + ")" + period;
                })
                .collect(Collectors.joining(", "));
    }

    /**
     * 일정 > 휴가계획서 > 휴가계획서 상세보기 > 확인 여부 변경(Ajax)
     */
    public VcatnPaprDto vcatnCf(final Integer key) throws Exception {
        VcatnPaprEntity vcatnPaprEntity = this.getDtlEntity(key);
        vcatnPaprEntity.setCfYn("Y");
        // update
        VcatnPaprEntity rsltEntity = this.updt(vcatnPaprEntity);
        VcatnPaprDto rsltDto = vcatnPaprMapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess(rsltEntity.getPostNo() != null);
        return rsltDto;
    }

    /**
     * 일정 > 휴가계획서 > 휴가계획서 삭제 전처리
     */
    @Override
    public void preDelete(final VcatnPaprEntity entity) {
        // 휴가계획서 상세항목 삭제 처리
        List<VcatnSchdulEntity> vcatnSchdulEntity = entity.getSchdulList();
        for (VcatnSchdulEntity vcatnSchdul : vcatnSchdulEntity) {
            vcatnSchdul.setDelYn("Y");
        }
    }

    /**
     * 일정 > 휴가계획서 > 년도 목록 조회
     */
    public List<VcatnStatsYyDto> getVcatnYyList() throws Exception {
        String minYyStr = vcatnPaprRepository.selectMinYy();
        int minYy = (minYyStr != null) ? Integer.parseInt(minYyStr) : DateUtils.getCurrYear();
        List<VcatnStatsYyDto> yyList = new ArrayList<>();
        int currYy = DateUtils.getCurrYear();
        for (int yy = minYy; yy <= currYy; yy++) {     // 최저년도~현재년도. 현재년도는 항상 들어가도록
            yyList.add(vcatnStatsYyService.getVcatnYyDtDto(Integer.toString(yy)));
        }
        return yyList;
    }
}
