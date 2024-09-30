<#--
 * 로그인 화면
 * (사이드바 없음)
 -->

<#import "/layout/layout_without_sidebar.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = (function() {
        return {
            init: function () {
                // request에서 불러온 에러 메세지 표시
                const errorMsgSpan = $("#errorMsgSpan");
                errorMsgSpan.html("");
                <#if errorMsg??>
                    $("#userId_valid_span").text("");
                    $("#password_valid_span").text("");
                    errorMsgSpan.html("${errorMsg!}".replace(/&lt;br&gt;/g, '<br/>'));   // 줄바꿈 처리
                </#if>
                // 중복 로그인시 기존 로그인 끊기 팝업 호출
                <#if isDupIdLgn??>
                    Page.dupLgnAlert();
                </#if>
                // 조건 일치시 비밀번호 변경 팝업 호출
                <#if isCredentialExpired!false || needsPwReset!false>
                    LgnPwChg.pwChgModal();
                </#if>

                /* jquery :: validation */
                commons.validate.validateForm("#lgnForm");
                // 엔터키 처리
                commons.util.enterKey(".enter", Page.lgn);
            },
            /** 기존 로그인 끊기 팝업 호출 */
            dupLgnAlert: function() {
                Swal.fire({
                    title: <@spring.message "view.rslt.dupLgn"/>,
                    text: <@spring.message "view.cnfm.dupLgn"/>,
                    icon: "warning",
                    showCancelButton: true,
                    confirmButtonText: "로그인",
                    cancelButtonText: "취소",
                }).then(function(result) {
                    if (result.value) {
                        // 중복ID 로그인
                        $("#userId").val("${userId!}");
                        $("#password").attr("disabled", "disabled");
                        Page.lgn();
                    } else {
                        // 중복ID 세션 attribute 만료시킴
                        const url = "${Url.AUTH_EXPIRE_SESSION_AJAX!}";
                        commons.util.blockUIAjax(url, 'post', null, function() {
                            commons.util.blockUIReplace("${Url.AUTH_LGN_FORM!}");
                        });
                    }
                });
            },
            /** 로그인 처리 */
            lgn: function() {
                commons.util.blockUISubmit("#lgnForm", "${Url.AUTH_LGN_PROC!}");
            },
            /** 신규계정 신청 페이지로 이동 */
            reqstUser: function() {
                commons.util.blockUIReplace("${Url.USER_REQST_REG_FORM!}");
            },
        }
    })();
    /** 페이지 초기화시 처리 */
    $(document).ready(function() {
        Page.init();
    });
</script>
<style>
    .app-page {
        background-image: url("/media/img/dreamdiary.jpg");
        height: 100%;
        background-position: center;
        background-repeat: no-repeat;
        background-size: cover;
        position: relative;
    }
</style>

<!--begin::LgnPage-->
<div class="w-lg-500px rounded mt-20 p-10 p-lg-15 mx-auto my-auto">
    <div class="aside-logo flex-column-auto pb-5 mb-20">
        <!-- -->
    </div>
    <div class="d-flex justify-content-center fs-4 mb-15 text-secondary fw-bold ls-1">
        <i class="bi bi-cloud-moon fs-2"></i>
        <span class="px-2">${Url.DOMAIN!}</span>
        <i class="bi bi-stars fs-2"></i>
    </div>
    <#--begin::Form-->
    <form name="lgnForm" id="lgnForm" class="w-100" method="post">
        <div class="opacity-75 mb-5">
            <label class="form-label fs-6 text-secondary fw-bolder" for="userId">아이디</label>
            <input type="text" name="userId" id="userId" class="form-control form-control-lg enter required"
                   value="" placeholder="아이디를 입력하세요." autocomplete="off" maxlength="20"
                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="아이디를 입력하세요." />
            <div class="h-15px">
                <span id="userId_valid_span" class="text-danger"></span>
            </div>
        </div>
        <div class="opacity-75">
            <label class="form-label text-secondary fw-bolder fs-6 mb-1" for="password">비밀번호</label>
            <input type="password" name="password" id="password" class="form-control form-control-lg enter required"
                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="비밀번호를 입력하세요."
                   placeholder="비밀번호를 입력하세요." maxlength="20" autocomplete="off" />
            <div class="mb-3 h-15px">
                <span id="password_valid_span" class="text-danger"></span>
            </div>
        </div>
        <div class="text-left">
            <span id="errorMsgSpan" class="text-danger"></span>
        </div>
        <div class="me-2 float-end">
            <label class="form-check form-check-custom form-check-solid" for="${REMEMBER_ME_PARAM!}"
                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="현재 브라우저에서&#10;로그인 상태를 유지합니다.">
                <span class="form-check-label text-secondary me-2">
                    로그인 상태 유지
                </span>
                <input type="checkbox" class="form-check-input" id="${REMEMBER_ME_PARAM!}" name="${REMEMBER_ME_PARAM!}">
            </label>
        </div>
        <div class="text-center mb-4 mt-12">
            <button type="button" class="btn btn-lg btn-light-primary opacity-75 w-100"
                    onclick="Page.lgn();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="로그인합니다." >
                <span class="indicator-label">Sign In</span>
                <span class="indicator-progress">Please wait...
                    <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                </span>
            </button>
        </div>
        <div class="d-flex justify-content-end mb-5">
            <button type="button" class="badge btn btn-sm btn-light-primary badge-outlined btn-outlined fw-light opacity-75 blink"
                    onclick="Page.reqstUser();"
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
    <#include "/view/_lgn_pw_chg_modal.ftlh">
    <#include "/view/_lgn_pw_chg_scripts.ftlh">
</#if>

</@layout.layout>