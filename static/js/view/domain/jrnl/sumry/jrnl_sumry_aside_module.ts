/**
 * jrnl_sumry_aside_module.ts
 *
 * @author nichefish
 */
if (typeof dF === 'undefined') { var dF = {} as any; }
dF.JrnlSumryAside = (function(): Module {
    return {
        /**
         * initializes module.
         */
        init: function(): void {
            console.log("'JrnlSumryAside' module initialized.");
        },

        yyMnth: function(obj: HTMLInputElement): void {
            // 쿠키 설정하기
            const id: string = $(obj).attr("id");
            const cookieOptions = {
                path: "/jrnl/sumry/",
                expires: cF.date.getCurrDateAddDay(36135)
            };
            $.cookie("jrnl_" + id, $(obj).val(), cookieOptions);
            // 목록 조회
            dF.JrnlSumry.listAjax();
        },
    }
})();