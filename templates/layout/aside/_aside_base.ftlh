<!--
 -- 레이아웃 > 사이드바
 -- @included in : layout
 -->
<!--begin::Aside-->
<div id="kt_aside" class="aside aside-hoverable" data-kt-drawer="true" data-kt-drawer-name="aside"
     data-kt-drawer-activate="{default: true, lg: false}" data-kt-drawer-overlay="true"
     data-kt-drawer-width="{default:'200px', '300px': '250px'}" data-kt-drawer-direction="start"
     data-kt-drawer-toggle="#kt_aside_mobile_<#if (isMngr!false && isMngrMode!false)>admin_</#if>toggle">
    <!--begin::Brand-->
    <div class="aside-logo flex-column-auto mt-3 mb-1" id="kt_aside_logo">
        <!--begin::Logo-->
        <a href="${SiteUrl.MAIN!}" class="d-flex align-items-center"
           data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click" title="${SiteUrl.DOMAIN!}">
            <div class="w-25px me-1">
                <img alt="Logo" src="/media/img/logo/logo_racoon.jpg" class="img-thumbnail w-auto logo"/>
            </div>
            <div>${SiteUrl.DOMAIN!}</div>
        </a>
        <!--end::Logo-->
        <!--begin::Aside toggler-->
        <div id="kt_aside_toggle" class="btn btn-icon w-auto px-0 btn-active-color-primary aside-toggle "
             data-kt-toggle="true" data-kt-toggle-state="active" data-kt-toggle-target="body" data-kt-toggle-name="aside-minimize"
             onclick="Layout.setAsideCookie();">
            <!--begin::Svg Icon | path: icons/duotune/arrows/arr079.svg-->
            <span class="svg-icon svg-icon-1 rotate-180"
                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="사이드바를 고정/해제합니다.&#10;해당 설정은 브라우저 캐시에&#10;저장됩니다.">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                    <path opacity="0.5" d="M14.2657 11.4343L18.45 7.25C18.8642 6.83579 18.8642 6.16421 18.45 5.75C18.0358 5.33579 17.3642 5.33579 16.95 5.75L11.4071 11.2929C11.0166 11.6834 11.0166 12.3166 11.4071 12.7071L16.95 18.25C17.3642 18.6642 18.0358 18.6642 18.45 18.25C18.8642 17.8358 18.8642 17.1642 18.45 16.75L14.2657 12.5657C13.9533 12.2533 13.9533 11.7467 14.2657 11.4343Z" fill="black"/>
                    <path d="M8.2657 11.4343L12.45 7.25C12.8642 6.83579 12.8642 6.16421 12.45 5.75C12.0358 5.33579 11.3642 5.33579 10.95 5.75L5.40712 11.2929C5.01659 11.6834 5.01659 12.3166 5.40712 12.7071L10.95 18.25C11.3642 18.6642 12.0358 18.6642 12.45 18.25C12.8642 17.8358 12.8642 17.1642 12.45 16.75L8.2657 12.5657C7.95328 12.2533 7.95328 11.7467 8.2657 11.4343Z" fill="black"/>
                </svg>
            </span>
            <!--end::Svg Icon-->
        </div>
        <!--end::Aside toggler-->
    </div>
    <!--end::Brand-->

    <div class="separator mx-5 opacity-25 blink"></div>

    <!--begin::Aside menu::사용자/관리자에 따라 사이드메뉴 다르게 첨부-->
    <div class="aside-menu flex-column-fluid">
        (isMngr: ${(isMngr!false)?string}, isMngrMode: ${(isMngrMode!false)?string})
<#--        <#if (isMngr!false && isMngrMode!false)>
            <#include "/layout/aside/_aside_menu_admin.ftlh">
        <#else>-->
            <#include "/layout/aside/_aside_menu_portal.ftlh">
<#--        </#if>-->
        <#include "/layout/aside/_aside_footer.ftlh">
    </div>
<!--end::Aside menu-->
</div>
<!--end::Aside-->
