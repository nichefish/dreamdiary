<#--
  -- 로그인 화면 > 비밀번호 변경 Modal
  -- @included_in : lgn_form
  -->

<script>
    const PwChg = function() {
        return {
            init: function () {
                // 조건 일치시 비밀번호 변경 팝업 호출
                <#if isCredentialExpired!false || needsPwReset!false>
                    PwChg.pwChgModal();
                </#if>
            },
            /** 비밀번호 변경 팝업 호출 */
            pwChgModal: function() {
                const data = { "userId": "${userId!}", "errorMsg": "${errorMsg!}" };
                commons.util.handlebarsTemplate(data, "lgn_pw_chg", "show");

                /** jquery :: validation */
                $(function(){
                    $("#lgnPwChgForm").validate({
                        submitHandler: function() {
                            PwChg.lgnPwChgAjax();
                        },
                        errorPlacement : function(error, element) {
                            commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                        },
                        rules: {
                            newPw: { regex: commons.validate.pwRegex },
                            newPwCf: { equalTo: "#newPw" }
                        },
                        ignore: [],         // hidden 필드도 검증하기 위함
                        messages: {
                            newPw: { regex: "변경할 비밀번호가 형식에 맞지 않습니다." },
                            newPwCf: { equalTo: "변경할 비밀번호에 입력한 값과 동일하게 입력해야 합니다." }
                        },
                    });
                });
                $.validator.addMethod("regex", function(value, element, regex) {
                    const regExp = new RegExp(regex);
                    return (this.optional(element) || regExp.test(value));
                });
            },
            /** 비밀번호 수정 처리(Ajax) */
            lgnPwChgAjax: function() {
                const url = '${SiteUrl.AUTH_LGN_PW_CHG_AJAX!}';
                let ajaxData = { "userId" : $("#lgnUserId").val(), "currPw" : $("#currPw").val(), "newPw" : $("#newPw").val() };
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    Swal.fire({
                        text: res.message
                    }).then(function() {
                        $("#lgnPwChgForm")[0].reset();
                        if (res.result) {
                            $("#userId").val(userId);
                            $("#password").val(password);
                            Page.lgn();
                        }
                    });
                });
            },
        }
    }();

    /** 페이지 초기화시 처리 */
    $(document).ready(function() {
        Page.init();
    });
</script>
<div class="modal fade" tabindex="-1" id="lgn_pw_chg_modal" data-bs-keyboard="false" data-bs-backdrop="static">
    <div class="modal-dialog modal-md modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header" style="background-color:#41416e;">
                <h2 class="modal-title text-light">비밀번호 변경</h2>
                <!--begin::Close-->
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <span class="svg-icon svg-icon-2x">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <rect opacity="0.5" x="6" y="17.3137" width="16" height="2" rx="1" transform="rotate(-45 6 17.3137)" fill="black"></rect>
                            <rect x="7.41422" y="6" width="16" height="2" rx="1" transform="rotate(45 7.41422 6)" fill="#ffffff"></rect>
                        </svg>
                    </span>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body">
                <form name="lgnPwChgForm" id="lgnPwChgForm" class="form">
                    <div id="lgn_pw_chg_div">
                        <!-- handlebars template goes in here -->
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary"
                        onclick="commons.util.submit(document.lgnPwChgForm);"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="입력 내용을 저장합니다.">
                    <i class="bi bi-pencil-square"></i>수정
                </button>
                <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal" onclick="commons.util.blockUIReload();">
                    닫기
                </button>
            </div>
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/_lgn_pw_chg_template.hbs">