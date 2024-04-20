<!--
 * 레이아웃 > 사이드바 base
 * @included_in : layout
 -->

<!--begin::Sidebar-->
<div id="kt_app_sidebar" class="app-sidebar flex-column" style="margin-top:5.7em !important;"
     data-kt-drawer="true"
     data-kt-drawer-name="app-sidebar"
     data-kt-drawer-activate="{default: true, lg: false}"
     data-kt-drawer-overlay="true"
     data-kt-drawer-width="225px"
     data-kt-drawer-direction="start"
     data-kt-drawer-toggle="#kt_app_sidebar_mobile_toggle">

    <#--include::Logo image and toggle-->
    <#include "/layout/sidebar/_sidebar_logo.ftlh">

    <#--include::Menu-->
    <!--사용자/관리자에 따라 사이드메뉴 다르게 첨부-->
    <#if isMngrMode>
        <#include "/layout/sidebar/_sidebar_menu_admin.ftlh">
    <#else>
        <#include "/layout/sidebar/_sidebar_menu_portal.ftlh">
    </#if>

    <#--include::Footer-->
    <#include "/layout/sidebar/_sidebar_footer.ftlh">
</div>
<!--end::Sidebar-->
