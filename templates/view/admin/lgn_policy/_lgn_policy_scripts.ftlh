<#--
 * 로그인 정책(lgn_policy) : 스크립트 모듈 분리
 -->

<script>
    const LgnPolicy = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                $("#lgnPolicyForm").validate({
                    submitHandler: function() {
                        LgnPolicy.regAjax();
                    },
                    errorPlacement : function(error, element) {
                        commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                    },
                    rules: {
                        lgnLockDy: { maxlength:3 },
                        lgnTryLmt: { maxlength:3 },
                        pwChgDy: { maxlength:3 },
                        pwForReset: { minlength:8, maxlength:20, regex:commons.validate.pwRegex },
                    },
                    messages: {
                        pwForReset: { regex:"비밀번호가 형식에 맞지 않습니다." },
                    },
                });
            },
            /** submit */
            submit: function() {
                $("#lgnPolicyForm").submit();
            },
            /** 등록 또는 수정 처리(Ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.LGN_POLICY_REG_AJAX!}";
                    const ajaxData = $("#lgnPolicyForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReplace("${Url.LGN_POLICY_FORM!}");
                            });
                    }, "block");
                });
            }
        }
    })();
</script>