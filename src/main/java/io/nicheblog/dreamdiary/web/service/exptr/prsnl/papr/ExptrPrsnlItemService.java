package io.nicheblog.dreamdiary.web.service.exptr.prsnl.papr;

import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlItemEntity;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.web.repository.exptr.prsnl.ExptrPrsnlPaprRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * ExptrPrsnlItemService
 * <pre>
 *  경비 관리 > 경비지출항목 관리 서비스 모듈
 *  ※경비지출항목(exptr_prsnl_item) = 경비지출서(exptr_prsnl_papr)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @implements BasePostService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("exptrPrsnlItemService")
@Log4j2
public class ExptrPrsnlItemService {

    @Resource(name = "exptrPrsnlService")
    private ExptrPrsnlPaprService exptrPrsnlPaprService;

    @Resource(name = "exptrPrsnlPaprRepository")
    private ExptrPrsnlPaprRepository exptrPrsnlPaprRepository;

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 첨부파일 업데이트
     */
    public Boolean updateExptrPrsnlItemRcipt(
            final Integer exptrPrsnlItemNo,
            final Integer atchFileDtlNo
    ) {
        exptrPrsnlPaprRepository.updateExptrPrsnlItemRcipt(exptrPrsnlItemNo, atchFileDtlNo);
        return true;
    }

    /**
     * 경비 관리 > 경비지출서 > 해당 지출내역에 대하여 영수증 원본 제출여부 업데이트
     */
    public Boolean exptrPrsnlStatsOrgnlRcipt(
            final Integer key,
            final Integer exptrPrsnlItemNo,
            String orgnlRciptYn
    ) throws Exception {
        // Entity 레벨 조회
        ExptrPrsnlPaprEntity exptrEntity = exptrPrsnlPaprService.getDtlEntity(key);
        List<ExptrPrsnlItemEntity> itemList = exptrEntity.getItemList();

        if (CollectionUtils.isEmpty(itemList)) return false;

        for (ExptrPrsnlItemEntity item : itemList) {
            if (item.getExptrPrsnlItemNo()
                    .equals(exptrPrsnlItemNo)) {
                item.setOrgnlRciptYn(orgnlRciptYn);
                break;
            }
        }
        // update
        ExptrPrsnlPaprEntity rsltEntity = exptrPrsnlPaprRepository.save(exptrEntity);
        return (rsltEntity.getPostNo() != null);
    }


    /**
     * 경비 관리 > 경비지출서 > 경비지출서 해당 지출내역 반려 처리 (관리자)
     */
    public Boolean exptrPrsnlItemRject(
            final Integer key,
            final Integer exptrPrsnlItemNo,
            final String rjectYn,
            final String rjectResn
    ) throws Exception {
        // Entity 레벨 조회
        ExptrPrsnlPaprEntity exptrEntity = exptrPrsnlPaprService.getDtlEntity(key);
        List<ExptrPrsnlItemEntity> itemList = exptrEntity.getItemList();

        if (CollectionUtils.isEmpty(itemList)) return false;

        for (ExptrPrsnlItemEntity item : itemList) {
            if (item.getExptrPrsnlItemNo()
                    .equals(exptrPrsnlItemNo)) {
                item.setRjectYn(rjectYn);
                item.setRjectResn(rjectResn);
                break;
            }
        }
        // update
        ExptrPrsnlPaprEntity rsltEntity = exptrPrsnlPaprRepository.save(exptrEntity);
        return (rsltEntity.getPostNo() != null);
    }
}
