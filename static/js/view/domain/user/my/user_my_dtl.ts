/**
 * user_my_dtl.ts
 *
 * @author nichefish
 */
// @ts-ignore
const Page: Page = (function(): Page {
    return {
        /**
         * Page 객체 초기화
         */
        init: function(): void {
            /* initialize modules. */
            dF.UserMy.init();
            dF.UserMyPwChg.init();
            /* initialize form. */
            dF.UserMyPwChg.initForm();
        },
    }
})();
document.addEventListener("DOMContentLoaded", function(): void {
    Page.init();
});