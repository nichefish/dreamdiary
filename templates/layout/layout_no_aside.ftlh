<!--
 -- 레이아웃 > 기본 레이아웃
 -->
<#macro layout>
    <!DOCTYPE html>
    <html lang="ko">
        <!--begin::Head-->
        <#include "/layout/_head.ftlh">
        <!--end::Head-->

        <!--begin::Body-->
        <body id="kt_body">

            <!--layout-partial:layout/_loader.mustache-->
            <#include "/layout/_loader.ftlh">

            <!--begin::Root-->
            <div class="d-flex flex-column flex-root">
                <!--begin::Page-->

                <!--begin::Wrapper-->
                <div class="wrapper d-flex flex-column flex-row-fluid h-auto">
                    <div id="kt_post" class="content d-flex flex-column flex-column-fluid">
                        <!--begin::Container-->
                        <div id="kt_content_container" class="container-xxl mw-75">
                            <!--begin::Content-->
                            <div class="content flex-column-fluid" id="kt_content">

                                <!-- Actual Page Contents Starts Here!!! -->
                                <#nested>
                                <!-- Actual Page Contents Ends Here!!! -->

                            </div>
                            <!--end::Content-->
                        </div>
                        <!--end::Container-->
                    </div>
                    <!--begin::Footer-->
                    <#include "/layout/footer/_footer_base.ftlh">
                    <#--<#include "/layout/_scrolltop.ftlh">-->
                    <!--end::Footer-->
                </div>
                <!--end::Wrapper-->
                <!--end::Page-->
            </div>
            <!--end::Root-->
        </body>
        <!--end::Body-->
        <#if false>
            <#include "/layout/_notice_popup_modal.ftlh">
            <#include "/layout/_notice_popup_template.ftlh">
            <#include "/layout/topbar/_sitemap_modal.ftlh">
        </#if>


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
    </html>
</#macro>