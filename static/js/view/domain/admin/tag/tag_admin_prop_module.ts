/**
 * tag_admin_prop_module.ts
 *
 * @author nichefish
 */
const TagProp = (function(): Module {
    return {
        /**
         * 태그 속성 등록 모달 호출
         * @param {string|number} tagNo - 조회할 태그 번호.
         */
        regModal: function(tagNo: string|number): void {
            if (isNaN(Number(tagNo))) return;

            const url: string = Url.TAG_DTL_AJAX;
            const ajaxData: Record<string, any> = { "tagNo": tagNo };
            cF.ajax.get(url, ajaxData, function(res: AjaxResponse): void {
                if (!res.rslt) {
                    if (cF.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                    return;
                }
                // 태그 상세 정보
                cF.handlebars.template(res.rsltObj, "tag_admin_dtl", "show");
            });
        }
    }
})();
document.addEventListener("DOMContentLoaded", function(): void {
    Page.init();
});