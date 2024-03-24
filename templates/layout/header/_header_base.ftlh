<!--
 -- 레이아웃 > 헤더 (포탈)
 -- @included in : layout
 -->
<script>
    /** 레이아웃 */
    let Layout = function() {
        return {
            init: function() {
                // 모든 버튼에 딜레이 기능 추가
                $("button, .btn, .badge").on("click", function() {
                    commons.util.delayBtn(this);
                });
                // 개별 input 유효성검사
                commons.validate.onlyNum(".number");

                // 사용자/관리자 전환
                $("#userMode").on("change", function() {
                    let mode = $(this).val();
                    if (mode === "${Constant.AUTH_USER!}") location.replace("${SiteUrl.MAIN!}");
                    if (mode === "${Constant.AUTH_MNGR!}") location.replace("${SiteUrl.ADMIN_MAIN!}");
                });

                /** 현재 접속중인 메뉴 활성화 로직 */
                const siteAcsInfoExists = "${topMenuNo}" !== "" && "${siteMenuNo!}" !== "";
                if (siteAcsInfoExists) {
                    $("#${topMenuNo!}").addClass("hover");
                    $("#${siteMenuNo}").addClass("hover");
                }
            },
            /** 사이드바 설정 쿠키 저장 */
            setAsideCookie: function() {
                <#if Constant??>
                    let wasMinimizeOn = ($("#kt_body").attr("data-kt-aside-minimize") === "on");
                    let toMode = !!wasMinimizeOn ? "${Constant.ASIDE_MENU_FIXED!}" : "${Constant.ASIDE_MENU_MINIMIZED!}";
                    let options = {
                        path: "/",
                        expires: commons.date.getCurrDateAddDay(36135)
                    };
                    $.cookie("${Constant.ASIDE_MENU!}", toMode, options);
                </#if>
            },
            /** 사이트맵 모달 팝업 호출 */
            fnSiteMap: function() {
                $("#sitemap_modal").modal("show");
            },
            /** 내 정보 상세 페이지 이동 */
            fnMyInfoDtl: function () {
                location.replace("${SiteUrl.USER_MY_DTL!}");
            },
            /** 로그아웃 처림 */
            fnLgout: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.lgout"/>,
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    location.replace("${SiteUrl.AUTH_LGOUT!}");
                });
            }
        }
    }();

    $(document).ready(function() {
        Layout.init();
    });
</script>
<!--begin::Header-->

<div id="kt_header" class="header align-items-stretch">
    <!--begin::Container-->
    <div class="container-fluid d-flex align-items-stretch justify-content-between">
        <#include "/layout/header/_header_mobile.ftlh">
        <!--begin::logo-->
        <div class="d-flex align-items-center flex-grow-1 flex-lg-grow-0 mx-2 d-block d-lg-none">
            <a href="${SiteUrl.MAIN!}">
                <img alt="Logo" src="/media/img/logo/logo_racoon.jpg" class="h-40px w-auto logo">
            </a>
        </div>
        <!--end::logo-->
        <!--begin::Title-->
        <p class="d-flex align-items-center text-gray-600 fs-6 py-6 d-none d-sm-block">
            현재 접속 계정 : ${(authInfo.nickNm)!}(${(authInfo.userId)!})
        </p>
        <!--end::Title-->
        <!--begin::Wrapper-->
        <div class="d-flex align-items-stretch justify-content-end flex-lg-grow-1">
            <!--begin::Topbar-->
            <div class="d-flex align-items-stretch flex-shrink-0">
                <#include "/layout/topbar/_topbar_base.ftlh">
            </div>
            <!--end::Topbar-->
        </div>
        <!--end::Wrapper-->
    </div>
    <!--end::Container-->
</div>
<!--end::Header-->