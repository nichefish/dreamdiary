<#--
 * 사용자(user) : 스크립트 모듈 분리
 -->

<script>
    const User = (function() {
        return {
            /**
             * form init
             */
            initForm: function(obj = {}) {
                /* jquery validation */
                commons.validate.validateForm("#userRegForm", User.submitHandler, {
                    <#if isReg!false>
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
                    </#if>
                });
                $.validator.addMethod("dupChck", function(value) {
                    return (value === "Y");
                });
                // 자동 대문자->소문자처리
                commons.validate.toLowerCase("#userId");
                // 연락처 포맷
                commons.validate.cttpc("#cttpc");
                // 권한 유효성검사 바꿀때마다 지우기
                $("#authCd").change(function() {
                    $("#authCd").valid(); // 체크박스 상태 변경시 details 필드 재검증
                });
                // 접속IP 사용 여부 클릭시 글씨 변경 + 입력창 토글 :: 메소드 분리
                commons.util.chckboxLabel("useAcsIpYn", "사용//미사용", "blue//gray", function(){
                    $("#acsIpListSpan").show()
                }, function(){
                    $("#acsIpListSpan").hide()
                });
                /* 접속IP tagify */
                commons.tagify.init("#acsIpListStr");
            },

            /**
             * Custom SubmitHandler
             */
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
                    User.regAjax();
                });
            },

            /**
             * 목록 검색
             */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.USER_LIST!}?actionTyCd=SEARCH");
            },

            /**
             * 엑셀 다운로드
             */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${Url.USER_LIST_XLSX_DOWNLOAD!}").submit();
                });
            },

            /**
             * 등록 화면으로 이동
             */
            regForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.USER_REG_FORM!}");
            },

            /**
             * 아이디 중복 체크(Ajax)
             */
            idDupChckAjax: function() {
                const userIdValidSpan = $("#userId_valid_span");
                const userId = $("#userId").val();
                const idRegex = /^(?=.*[a-z])[a-z\d]{4,16}$/g;
                if (!idRegex.test(userId)) {
                    userIdValidSpan.text("아이디가 형식에 맞지 않습니다.").removeClass("text-success").addClass("text-danger");
                    return false;
                }
                const url = "${Url.USER_ID_DUP_CHK_AJAX!}";
                const ajaxData = { "userId": userId };
                commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                    userIdValidSpan.text(res.message);
                    if (res.rslt) {
                        userIdValidSpan.removeClass("text-danger").addClass("text-success");
                        $("#ipDupChckPassed").val("Y");
                        $("#ipDupChckPassed_valid_span").text("");
                        $("#idDupChckBtn").removeClass("blink").addClass("btn-success").removeClass("btn-secondary").attr("disabled", "disabled");
                    } else {
                        userIdValidSpan.removeClass("text-success").addClass("text-danger");
                        $("#ipDupChckPassed").val("N");
                    }
                });
            },

            /**
             * 등록/수정 처리(Ajax)
             */
            regAjax: function() {
                const url = "<#if isReg!false>${Url.USER_REG_AJAX!}</#if><#if isMdf!false>${Url.USER_MDF_AJAX!}</#if>";
                const ajaxData = new FormData(document.getElementById("userRegForm"));
                commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) User.list();
                        });
                }, "block");
            },

            /**
             * 상세 화면으로 이동
             * @param {string|number} userNo - 사용자 번호
             */
            dtl: function(userNo) {
                if (isNaN(postNo)) return;

                $("#procForm #userNo").val(userNo);
                commons.util.blockUISubmit("#procForm", "${Url.USER_DTL!}");
            },

            /**
             * 승인 처리 (ajax)
             */
            cfAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.cf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.USER_REQST_CF_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },

            /**
             * 승인취소 처리 (ajax)
             */
            uncfAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.uncf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.USER_REQST_UNCF_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },

            /**
             * 패스워드 초기화 (ajax)
             */
            pwResetAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.resetPw"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.USER_PW_RESET_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },

            /**
             * 수정 화면으로 이동
             */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.USER_MDF_FORM!}");
            },

            /**
             * 삭제 처리 (ajax)
             */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.USER_DEL_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) User.list();
                            });
                    }, "block");
                });
            },

            /**
             * 목록 화면으로 이동
             */
            list: function() {
                const listUrl = "${Url.USER_LIST!}<#if isMdf!false>?isBackToList=Y</#if>";
                commons.util.blockUIReplace(listUrl);
            }
        }
    })();
</script>