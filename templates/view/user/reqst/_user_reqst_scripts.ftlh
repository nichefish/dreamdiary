<#--
  -- 사용자 신청(user_reqst) : 스크립트 모듈 분리
  -->

<script>
    const UserReqst = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                $("#userReqstForm").validate({
                    submitHandler: function() {
                        if ($("#useAcsIpYn").is(":checked") && $("#acsIpListStr").val() === "") {
                            Swal.fire("접속 IP는 최소 한 개 이상 입력해야 합니다.");
                            return false;
                        }
                        Swal.fire({
                            text: <#if isMdf!false><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                            showCancelButton: true,
                        }).then(function(result) {
                            if (!result.value) return;
                            UserReqst.regAjax();
                        });
                    },
                    errorPlacement : function(error, element) {
                        commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                    },
                    ignore: [],         // hidden 필드도 검증하기 위함
                    rules: {
                        userId: { minlength:4, maxlength:16 },
                        ipDupChckPassed: { dupChck:true },
                        password: { minlength:9, maxlength:20, regex:commons.validate.pwRegex },
                        passwordCf: { equalTo:"#password", maxlength:20 },
                    },
                    messages: {
                        ipDupChckPassed: { dupChck:"아이디 중복 체크는 필수 항목입니다." },
                        password: { regex:"비밀번호가 형식에 맞지 않습니다." },
                        passwordCf: { equalTo:"비밀번호에 입력한 값과 동일하게 입력해야 합니다." },
                    },
                });
                $.validator.addMethod("dupChck", function(value) {
                    return (value === "Y");
                });
            },
            /** 등록/수정 처리(Ajax) */
            regAjax: function() {
                const url = "${Url.USER_REQST_REG_AJAX!}";
                let ajaxData = new FormData($("#userReqstForm")[0]);
                commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) UserReqst.main();
                        });
                });
            },
            /** 메인 화면으로 돌아가기 */
            return: function() {
                Swal.fire({
                    text: "메인 화면으로 이동합니다.\n(현재까지 입력한 내용은 사라집니다.)\n계속하겠습니까?",
                    showCancelButton: true,
                }).then(function(result) {
                    if (result.value) UserReqst.main();
                });
            },
            /** 메인 화면으로 이동 */
            main: function() {
                commons.util.blockUIReplace("${Url.MAIN!}");
            }
        }
    })();
</script>