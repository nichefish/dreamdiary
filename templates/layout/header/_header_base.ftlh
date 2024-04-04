<!--
 -- 레이아웃 > 헤더
 -- @included_in : layout
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
                const siteAcsInfoExists = "${topMenuNo!}" !== "" && "${siteMenuNo!}" !== "";
                if (siteAcsInfoExists) {
                    $("#${topMenuNo!}").addClass("hover");
                    $("#${siteMenuNo!}").addClass("hover");
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

<div id="kt_app_header"
     class="app-header shadow-sm"
     data-kt-sticky="true"
     data-kt-sticky-activate="{default: true, lg: true}"
     data-kt-sticky-name="app-header-minimize"
     data-kt-sticky-offset="{default: '200px', lg: '0'}"
     data-kt-sticky-animation="false" >

    <!--begin::Header container-->
    <div class="app-container container-fluid d-flex align-items-stretch justify-content-between" id="kt_app_header_container">

        <!--begin::Mobile logo-->
        <#include "/layout/header/_header_logo.ftlh">
        <!--end::Mobile logo-->

        <!--begin::Header wrapper-->
        <div class="d-flex align-items-stretch justify-content-between flex-lg-grow-1" id="kt_app_header_wrapper">

            <#include "/layout/header/_header_menu.ftlh">

            <#include "/layout/header/navbar/_navbar_base.ftlh">

        </div>
        <!--end::Header wrapper-->

    </div>
    <!--end::Header container-->

</div>
<!--end::Header-->
