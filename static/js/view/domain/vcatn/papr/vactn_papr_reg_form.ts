/**
 * vactn_papr_reg_form.ts
 * 휴가신청서 등록/수정 페이지 스크립트
 *
 * @author nichefish
 */
// @ts-ignore
const Page: Page = (function(): Page {
    return {
        isReg: $("#noticeRegForm").data("mode") === "regist",
        idx: 0,

        /**
         * Page 객체 초기화
         */
        init: function(): void {
            /* initialize modules. */
            dF.VcatnPapr.init();

            // 추가추가에 사용할 인덱스 설정
            Page.idx = $("vcatnPaprRegForm").data("init-length") || 0;

            /* initialize form. */
            dF.VcatnPapr.initForm();
            // 첨부파일 영역 0개인지 체크
            dF.AtchFile.atchFileListToggle();

            if (Page.isReg) {
                $("#jandiYn").trigger("click");
                // 휴가 추가 폼 기본 뿌리기
                Page.addVcatnSchdul();
            }
        },

        /**
         * 휴가등록 부분 추가
         */
        addVcatnSchdul: function(): void {
            const idx: number = Page.idx++;
            cF.handlebars.append({ "idx": idx }, "vcatn_schdul_reg");
            cF.datepicker.singleDatePicker("#bgnDt" + idx, "yyyy-MM-DD", null, function(): void {
                dF.VcatnSchdul.propEndDt(idx);
                dF.VcatnSchdul.noBefore("#bgnDt"+idx, "#endDt"+idx, idx);
            });
            cF.datepicker.singleDatePicker("#endDt"+idx, "yyyy-MM-DD", null, function(): void {
                if ($("#endDt"+idx).val() !== "") dF.VcatnSchdul.noBefore("#bgnDt"+idx, "#endDt"+idx, idx);
            });
        },
        /** 추가항목 영역 삭제 */
        delSpan: function(idx: number): void {
            $("div#itemContainer" + idx).remove();
        }
    }
})();
document.addEventListener("DOMContentLoaded", function(): void {
    Page.init();
});