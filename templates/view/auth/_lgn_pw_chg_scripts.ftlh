<#--
 * 로그인 화면 > 비밀번호 변경 : 스크립트 모듈 분리
 -->

<script>
    const LgnPwChg = (function() {
        return {
            /**
             * 비밀번호 변경 팝업 호출
             */
            pwChgModal: function() {
                const data = { "userId": "${userId!}", "errorMsg": "${errorMsg!}" };
                /* initialize form. */
                LgnPwChg.initForm(data);
            },

            /**
             * form init
             * @param {Object} obj - 폼에 바인딩할 데이터
             */
            initForm: function(obj = {}) {
                /* show modal */
                commons.util.handlebarsTemplate(obj, "lgn_pw_chg", "show");

                // 엔터키 처리
                commons.util.enterKey("#currPw", LgnPwChg.submit);
                commons.util.enterKey("#newPw", LgnPwChg.submit);
                commons.util.enterKey("#newPwCf", LgnPwChg.submit);

                /* jquery validation */
                commons.validate.validateForm("#lgnPwChgForm", LgnPwChg.lgnPwChgAjax, {
                    rules: {
                        newPw: { regex: commons.validate.pwRegex },
                        newPwCf: { equalTo: "#newPw" }
                    },
                    messages: {
                        newPw: { regex: "변경할 비밀번호가 형식에 맞지 않습니다." },
                        newPwCf: { equalTo: "변경할 비밀번호에 입력한 값과 동일하게 입력해야 합니다." }
                    },
                });
            },

            /**
             * form submit
             */
            submit: function() {
                $("#lgnPwChgForm").submit();
            },

            /**
             * 비밀번호 수정 (Ajax)
             */
            lgnPwChgAjax: function() {
                const url = '${Url.AUTH_LGN_PW_CHG_AJAX!}';
                const userId = $("#lgnUserId").val();
                const ajaxData = { "userId" : userId, "currPw" : $("#currPw").val(), "newPw" : $("#newPw").val() };
                commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                    Swal.fire({
                        text: res.message
                    }).then(function() {
                        $("#lgnPwChgForm")[0].reset();
                        if (res.rslt) commons.util.blockUIReplace("${Url.AUTH_LGN_FORM!}");
                    });
                });
            },
        }
    })();
</script>