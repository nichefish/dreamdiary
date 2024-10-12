<!--
 * 레이아웃 > 사이드바 로고
 * @included_in : layout
 -->

<!--begin::Logo-->
<div class="app-sidebar-logo px-6" id="kt_app_sidebar_logo">

    <!--begin::Logo image-->
    <a href="${Url.MAIN!}" class="d-flex align-items-center"
       data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click" title="${Url.DOMAIN!}">
        <!-- default -->
        <div class="app-sidebar-logo-default w-25px me-5">
            <img alt="Logo" src="/media/img/logo/logo_racoon.jpg" class="img-thumbnail w-auto logo"/>
        </div>
        <div class="app-sidebar-logo-default">${Url.DOMAIN!}</div>
        <!-- minimized -->
        <div class="app-sidebar-logo-minimize w-25px me-5">
            <img alt="Logo" src="/media/img/logo/logo_racoon.jpg" class="img-thumbnail w-auto logo"/>
        </div>
    </a>
    <!--end::Logo image-->

    <!--begin::Sidebar toggle-->
    <!--begin::Minimized sidebar setup:
        if (isset($_COOKIE["sidebar_minimize_state"]) && $_COOKIE["sidebar_minimize_state"] === "on") {
            1. "src/js/layout/sidebar.js" adds "sidebar_minimize_state" cookie value to save the sidebar minimize state.
            2. Set data-kt-app-sidebar-minimize="on" attribute for body tag.
            3. Set data-kt-toggle-state="active" attribute to the toggle element with "kt_app_sidebar_toggle" id.
            4. Add "active" class to to sidebar toggle element with "kt_app_sidebar_toggle" id.
        }
    -->
    <div id="kt_app_sidebar_toggle" class="app-sidebar-toggle btn btn-icon btn-shadow btn-sm btn-color-muted btn-active-color-primary h-30px w-30px position-absolute top-50 start-100 translate-middle rotate "
         data-kt-toggle="true"
         <#if isSidebarMinimized!false>data-kt-toggle-state="active"</#if>
         data-kt-toggle-target="body"
         data-kt-toggle-name="app-sidebar-minimize"
         data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="사이드바를 고정/해제합니다.&#10;해당 설정은 브라우저 캐시에&#10;저장됩니다.">

        <i class="ki-duotone ki-black-left-line fs-3 rotate-180">
            <span class="path1"></span>
            <span class="path2"></span>
        </i>

    </div>
    <!--end::Sidebar toggle-->
</div>
<!--end::Logo-->