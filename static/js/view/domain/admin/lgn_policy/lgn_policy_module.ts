/**
 * lgn_policy_module.ts
 * 로그인 정책 스크립트 모듈
 *
 * @author nichefish
 */
if (typeof dF === 'undefined') { var dF = {} as any; }
dF.LgnPolicy = (function(): dfModule {
    return {
        initialized: false,

        /**
         * initializes module.
         */
        init: function(): void {
            if (dF.LgnPolicy.initialized) return;

            dF.LgnPolicy.initialized = true;
            console.log("'dF.LgnPolicy' module initialized.");
        },

        /**
         * form init
         */
        initForm: function(): void {
            /* jquery validation */
            cF.validate.validateForm("#lgnPolicyForm", dF.LgnPolicy.regAjax, {
                rules: {
                    lgnLockDy: { maxlength: 3 },
                    lgnTryLmt: { maxlength: 3 },
                    pwChgDy: { maxlength: 3 },
                    pwForReset: { minlength: 8, maxlength: 20, regex: cF.regex.pw },
                },
                messages: {
                    pwForReset: { regex: "비밀번호가 형식에 맞지 않습니다." },
                },
            });
        },

        /**
         * form submit
         */
        submit: function(): void {
            $("#lgnPolicyForm").submit();
        },

        /**
         * 등록/수정 (Ajax)
         */
        regAjax: function(): void {
            Swal.fire({
                text: Message.get("view.cnfm.mdf"),
                showCancelButton: true,
            }).then(function(result: SwalResult): void {
                if (!result.value) return;

                const url: string = Url.LGN_POLICY_REG_AJAX;
                const ajaxData: Record<string, any> = cF.util.getJsonFormData("#lgnPolicyForm");
                cF.$ajax.post(url, ajaxData, function(res: AjaxResponse): void {
                    Swal.fire({ text: res.message })
                        .then(function(): void {
                            if (res.rslt) cF.ui.blockUIReplace(Url.LGN_POLICY_FORM);
                        });
                }, "block");
            });
        }
    }
})();