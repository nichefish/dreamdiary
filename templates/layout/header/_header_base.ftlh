<!--
 -- 레이아웃 > 헤더
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
            /** 사이트맵 모달 팝업 호출 */
            siteMapModal: function() {
                $("#sitemap_modal").modal("show");
            },
            /** 내 정보 상세 페이지 이동 */
            myInfoDtl: function () {
                location.replace("${SiteUrl.USER_MY_DTL!}");
            },
            /** 로그아웃 처림 */
            lgout: function() {
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
        <p class="d-flex align-items-center text-gray-600 fs-6 py-6 d-none d-sm-block" style="width:235px;">
            현재 접속 계정 : ${(authInfo.nickNm)!}(${(authInfo.userId)!})
        </p>
        <!--end::Title-->
        <span class="my-5 border-gray-300 border-start"></span>
        <!--begin::Wrapper-->
        <div class="d-flex align-items-stretch justify-content-between flex-lg-grow-1">
            <!--begin::Topbar-->
            <#include "/layout/topbar/_topbar_base.ftlh">
            <!--end::Topbar-->
        </div>
        <!--end::Wrapper-->
    </div>
    <!--end::Container-->
</div>
<!--end::Header-->