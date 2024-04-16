<#--
  -- 로그인 정책(lgn_policy) : 모듈 스크립트 분리
  -->

<script>
    const LgnPolicy = (function() {
        return {
            /** form validation init */
            initValidate: function() {
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
                        pwForReset: { minlength:8, maxlength:16 },
                    },
                    messages: {
                        lgnLockDy: { maxlength:"장기간 미로그인 잠금일수는 {0}자 이하로 입력해야 합니다." },
                        lgnTryLmt: { maxlength:"로그인 제한 횟수는 {0}자 이하로 입력해야 합니다." },
                        pwChgDy: { maxlength:"비밀번호 변경 일수는 {0}자 이하로 입력해야 합니다." },
                        pwForReset: { minlength:"비밀번호 초기화 설정값은 {0}자 이상으4로 입력해야 합니다.", maxlength:"비밀번호 초기화 설정값은 {0}자 이상으로 입력해야 합니다." }
                    }
                });
            },
            /** 등록 또는 수정 처리(Ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.LGN_POLICY_REG_AJAX!}";
                    let ajaxData = $("#lgnPolicyForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReplace("${Url.LGN_POLICY_FORM!}");
                            });
                    });
                });
            }
        }
    })();
</script>