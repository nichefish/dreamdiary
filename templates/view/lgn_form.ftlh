<#--
  -- 로그인 화면
  -- (사이드바 없음)
  -->
<#import "/layout/layout_no_aside.ftlh" as layout >

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                // request에서 불러온 에러 메세지 표시
                const errorMsgSpan = $("#errorMsgSpan");
                errorMsgSpan.html("");
                <#if errorMsg??>
                    $("#userIdSpan").text("");
                    $("#passwordSpan").text("");
                    errorMsgSpan.html("${errorMsg!}".replace(/&lt;br&gt;/g, '<br/>'));   // 줄바꿈 처리
                </#if>

                commons.util.enterKey("#userId", Page.lgn);       // 엔터키 처리
                commons.util.enterKey("#password", Page.lgn);       // 엔터키 처리

                <#if isCredentialExpired!false || needsPwReset!false>
                    let data = { "userId": "${userId!}", "errorMsg": "${errorMsg!}" };
                    commons.util.handlebarsTemplate(data, "lgn_pw_chg", "show");
                    /** jquery :: validation */
                    $(function(){
                        $("#lgnPwChgForm").validate({
                            submitHandler: function() {
                                Page.lgnPwChgAjax();
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
                </#if>

                <#if isDupIdLgn!false>
                    Swal.fire({
                        title: <@spring.message "view.result.dupLgn"/>,
                        text: <@spring.message "view.cnfm.dupLgn"/>,
                        icon: "warning",
                        showCancelButton: true,
                        confirmButtonText: "로그인",
                        cancelButtonText: "취소",
                    }).then(function(result) {
                        if (result.value) {
                            $("#userId").val("${userId!}");
                            $("#isDupIdLgnProc").val("Y");
                            $("#password").attr("disabled", "disabled");
                            Page.lgn();
                        } else {
                            $("#isDupIdLgnProc").val("");
                        }
                    });
                </#if>

                /** jquery :: validation */
                commons.validate.validateForm("#lgnForm");
            },
            /** 로그인 처리 */
            lgn: function() {
                commons.util.blockUISubmit("#lgnForm", "${SiteUrl.AUTH_LGN_PROC!}");
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
            /** 신규계정 신청 페이지로 이동 */
            reqstUser: function() {
                commons.util.blockUIReplace("${SiteUrl.USER_REQST_REG_FORM!}");
            },
        }
    }();

    /** 페이지 초기화시 처리 */
    $(document).ready(function() {
        Page.init();
    });
</script>
<style>
    /** ---- 모바일 버전 ---- */
    @media (max-width: 768px) {
        .logo{ width:200px; margin:0 40px; }
    }
</style>

<!--begin::Root-->
<div class="w-lg-500px bg-body rounded shadow-sm mt-20 p-10 p-lg-15 mx-auto my-auto">
    <form class="form w-100" name="lgnForm" id="lgnForm" novalidate="novalidate" method="post">
        <div class="aside-logo flex-column-auto mb-20" id="kt_aside_logo">
            <div class="d-flex justify-content-center">
                <img alt="Logo" src="/media/img/logo/logo_racoon.jpg" class="logo"/>
            </div>
        </div>
        <div class="mb-7">
            <label class="form-label fs-6 fw-bolder" for="userId">아이디</label>
            <input type="text" name="userId" id="userId" class="form-control form-control-lg form-control-solid enter required" autocomplete="off" maxlength="20" placeholder="아이디를 입력하세요." />
                <#if isDupIdLgn!false>
                    <input type="hidden" name="isDupIdLgnProc" id="isDupIdLgnProc">
                </#if>
            <div class="h-15px">
                <span id="userIdSpan" class="text-danger"></span>
            </div>
        </div>
        <div>
            <div class="d-flex flex-stack mb-2">
                <label class="form-label fw-bolder fs-6 mb-0" for="password">비밀번호</label>
            </div>
            <input type="password" name="password" id="password" class="form-control form-control-lg form-control-solid enter required" autocomplete="off" maxlength="20"  placeholder="비밀번호를 입력하세요."/>
            <div class="mb-3 h-15px">
                <span id="passwordSpan" class="text-danger"></span>
            </div>
        </div>
        <div class="text-left">
            <span id="errorMsgSpan" class="text-danger"></span>
        </div>
        <div class="me-2 float-end">
            <label class="form-check form-check-custom form-check-solid" for="${REMEMBER_ME_PARAM!}"
                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="현재 브라우저에서&#10;로그인 상태를 유지합니다.">
                <span class="form-check-label me-2">
                    로그인 유지
                </span>
                <input type="checkbox" class="form-check-input" id="${REMEMBER_ME_PARAM!}" name="${REMEMBER_ME_PARAM!}">
            </label>
        </div>
        <div class="text-center mb-4 mt-15">
            <button type="button" class="btn btn-lg btn-primary w-100" onclick="Page.lgn();">
                <span class="indicator-label">Sign In</span>
                <span class="indicator-progress">Please wait...
                    <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                </span>
            </button>
        </div>
        <div class="d-flex justify-content-end mb-5">
            <button type="button" class="badge btn btn-sm btn-light-primary badge-outlined btn-outlined fw-light opacity-75 blink" onclick="Page.reqstUser();"
                    data-bs-toggle="tooltip" data-bs-placement="bottom" title="계정 신청 화면으로&#10;이동합니다.">
                <i class="bi bi-person-plus-fill blink"></i>신규 계정 신청
            </button>
        </div>
        <div class="text-end mb-5">
            ©2024. nichefish. All rights reserved.
        </div>
    </form>
    <!--end::Form-->
</div>
<#if isCredentialExpired!false || needsPwReset!false>
    <#include "/view/lgn_pw_chg_modal.ftlh">
    <#include "/view/lgn_pw_chg_template.ftlh">
</#if>

</@layout.layout>