<!--
 * 레이아웃 > 기본 레이아웃
 * metronic v8.2.5
 -->

<#macro layout toolbar>
<!DOCTYPE html>
<html lang="ko">
    <#assign isMngr = isMngr!false>
    <#assign isMngrMode = isMngrMode!false>

    <#--include::Head-->
    <#include "/layout/_head.ftlh">

    <!--begin::Body-->
    <body id="kt_app_body"
          data-kt-app-page-loading-enabled="true"
          data-kt-app-page-loading="on"
          data-kt-app-layout="dark-sidebar"
          data-kt-app-header-fixed="true"
          data-kt-app-toolbar-enabled="true"
          data-kt-app-sidebar-hoverable="true"
          data-kt-app-sidebar-push-header="true"
          data-kt-app-sidebar-push-toolbar="true"
          data-kt-app-sidebar-push-footer="true"
          data-kt-app-footer-fixed="true"
          class="app-default" >

    <#--include::Head-->
    <#include "/layout/_page_loader.ftlh">

        <!--begin::App-->
        <div class="d-flex flex-column flex-root app-root" id="kt_app_root">

            <!--begin::Page-->
            <div class="app-page  flex-column flex-column-fluid" id="kt_app_page">

                <!--!NO Header!-->

                <!--begin::Wrapper-->
                <div class="app-wrapper  flex-column flex-row-fluid" id="kt_app_wrapper">

                    <!--begin::Main-->
                    <div class="app-main flex-column flex-row-fluid" id="kt_app_main">

                        <!--begin::Content wrapper-->
                        <div class="d-flex flex-column flex-column-fluid">
                            <!--begin::Content-->
                            <div id="kt_app_content" class="app-content flex-column-fluid" >
                                <!--begin::Content container-->
                                <div id="kt_app_content_container" class="app-container container-fluid">

                                    <#-- Actual Page Contents Starts Here!!! -->
                                    <#nested>
                                    <#-- Actual Page Contents Ends Here!!! -->

                                </div>
                                <!--end::Content container-->
                            </div>
                            <!--end::Content-->
                        </div>
                        <!--end::Content wrapper-->

                        <#--include:: Footer-->
                        <#include "/layout/footer/_footer_base.ftlh">
                    </div>
                    <!--end:::Main-->

                </div>
                <!--end::Wrapper-->

            </div>
            <!--end::Page-->

        </div>
        <!--end::App-->

        <#include "/layout/_scrolltop.ftlh">

        <!--begin::Modals (include modals here)-->
        <!--end::modals-->

        <script>
            $(document).ready(function() {
                /** 타이틀 설정 */
                document.title = "dreamdiary | ${(siteAcsInfo.topMenuNm)!}";

                /** 메세지 존재시 출력 */
                <#if message??>
                    Swal.fire("${message!}");
                </#if>
            });
        </script>

    </body>
    <!--end::Body-->
</html>
</#macro>