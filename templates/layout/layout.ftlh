<!--
 -- 레이아웃 > 기본 레이아웃
 -->
<#macro layout>
    <!DOCTYPE html>
    <html lang="ko">
        <#assign topMenuNo = (siteAcsInfo.topMenuNo)!>
        <#assign siteMenuNo = (siteAcsInfo.siteMenuNo)!>
        <#assign siteMenuNm = (siteAcsInfo.siteMenuNm)!>
        <#assign pageNm = (siteAcsInfo.pageNm)!>
        <#assign asideAt = (siteAcsInfo.asideAt)!false>

        <script>
            $(document).ready(function() {
                /** 타이틀 설정 */
                document.title = "dreamdiary | ${siteMenuNm!}";

                /** 메세지 존재시 출력 */
                <#if message??>
                Swal.fire("${message!}");
                </#if>
            });
        </script>

        <!--begin::Head-->
        <#include "/layout/_head.ftlh">
        <!--end::Head-->

        <!--begin::Body-->
        <body id="kt_body"
              <#if asideAt>class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed aside-enabled aside-fixed footer-fixed"
              <#if aside_minimized!false>data-kt-aside-minimize="on"</#if> style="--kt-toolbar-height:55px;--kt-toolbar-height-tablet-and-mobile:55px"</#if>>

            <!--layout-partial:layout/_loader.mustache-->
            <#include "/layout/_loader.ftlh">

            <!--begin::Root-->
            <div class="d-flex flex-column flex-root">
                <!--begin::Page-->
                <!--layout-partial:sidebar/header/topbar/drawer-->
                <#if asideAt>
                    <#include "/layout/aside/_aside_base.ftlh">
                    <#include "/layout/header/_header_base.ftlh">
                    <#include "/layout/toolbar/_toolbar.ftlh">
                    <#include "/layout/topbar/_user_drawer.ftlh">
                </#if>

                <!--begin::Wrapper-->
                <div class="wrapper d-flex flex-column flex-row-fluid <#if !(isPdf!false)>pt-10 mt-10</#if> h-auto">
                    <div id="kt_post" class="content d-flex flex-column flex-column-fluid">
                        <!--begin::Container-->
                        <div id="kt_content_container" class="container-xxl mw-100">
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
    </html>
</#macro>