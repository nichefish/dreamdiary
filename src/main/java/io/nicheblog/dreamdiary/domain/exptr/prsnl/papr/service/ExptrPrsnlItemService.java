package io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.service;

import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlItemEntity;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.repository.jpa.ExptrPrsnlPaprRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * ExptrPrsnlItemService
 * <pre>
 *  경비 관리 > 경비지출항목 관리 서비스 모듈
 *  ※경비지출항목(exptr_prsnl_item) = 경비지출서(exptr_prsnl_papr)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 */
@Service("exptrPrsnlItemService")
@RequiredArgsConstructor
@Log4j2
public class ExptrPrsnlItemService {

    private final ExptrPrsnlPaprService exptrPrsnlPaprService;
    private final ExptrPrsnlPaprRepository exptrPrsnlPaprRepository;

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 첨부파일 업데이트
     *
     * @param exptrPrsnlItemNo 경비지출항목 번호 (Integer)
     * @param atchFileDtlNo 첨부파일 상세 번호 (Integer)
     * @return {@link Boolean} -- 업데이트 성공 여부
     */
    @Transactional
    public Boolean updateRciptFile(final Integer exptrPrsnlItemNo, final Integer atchFileDtlNo) {
        exptrPrsnlPaprRepository.updateRciptFile(exptrPrsnlItemNo, atchFileDtlNo);
        return true;
    }

    /**
     * 경비 관리 > 경비지출서 > 해당 지출내역에 대하여 영수증 원본 제출여부 업데이트
     *
     * @param key 경비지출서 번호
     * @param exptrPrsnlItemNo 경비지출항목 번호
     * @param orgnlRciptYn 영수증 원본 제출 여부 (Y/N)
     * @return {@link Boolean} -- 업데이트 성공 여부
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    public Boolean updtOrgnlRciptYn(final Integer key, final Integer exptrPrsnlItemNo, final String orgnlRciptYn) throws Exception {
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
     *
     * @param key 경비지출서 번호
     * @param exptrPrsnlItemNo 경비지출항목 번호
     * @param rjectYn 반려 여부 (Y/N)
     * @param rjectResn 반려 사유
     * @return {@link Boolean} -- 처리 성공 여부
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    public Boolean exptrPrsnlItemRject(final Integer key, final Integer exptrPrsnlItemNo, final String rjectYn, final String rjectResn) throws Exception {
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
