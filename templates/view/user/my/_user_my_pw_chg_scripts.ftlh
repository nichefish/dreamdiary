<#--
 * 내 정보(user_my) > 내 비밀번호 변경 > 스크립트 모듈 분리
 -->

<script>
    const UserMyPwChg = (function() {
        return {
            /**
             * form validation init
             */
            initValidateForm: function() {
                /* jquery :: validation */
                $(function(){
                    $("#myPwChgForm").validate({
                        submitHandler: function() {
                            Swal.fire({
                                text: <@spring.message "view.cnfm.chgPw"/>,
                                showCancelButton: true,
                            }).then(function(result) {
                                if (!result.value) return;
                                UserMyPwChg.myPwChgAjax();
                            });
                        },
                        errorPlacement : function(error, element) {
                            commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                        },
                        rules: {
                            newPw: { regex: commons.validate.pwRegex },
                            newPwCf: { equalTo: "#newPw" }
                        },
                        messages: {
                            newPw: { regex:"변경할 비밀번호가 형식에 맞지 않습니다." },
                            newPwCf: { equalTo:"변경할 비밀번호에 입력한 값과 동일하게 입력해야 합니다." }
                        },
                    });
                });
            },
            /** 내 비밀번호 수정 팝업 호출 */
            myPwChgModal: function() {
                const data = { "errorMsg": "${errorMsg!}" };
                commons.util.handlebarsTemplate(data, "user_my_pw_chg", "show");
            },
            /**
             * submit
             */
            submit: function() {
                $("#myPwChgForm").submit();
            },
            /** 내 비밀번호 수정 처리(Ajax) */
            myPwChgAjax: function() {
                const url = "${Url.USER_MY_PW_CHG_AJAX!}";
                const ajaxData = { "currPw" : $("#currPw").val(), "newPw" : $("#newPw").val() };
                commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                });
            },
        }
    })();
</script>
