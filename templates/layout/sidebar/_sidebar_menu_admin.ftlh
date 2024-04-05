<!--
 -- 레이아웃 > 사이드바 메뉴 (관리자)
 -- @included_in : aside/_aside_base
 -->
<#import "/layout/sidebar/_sidebar_menu_template.ftlh" as menu >

<#assign topMenuNo = (siteAcsInfo.topMenuNo)!>
<#assign siteMenuNo = (siteAcsInfo.siteMenuNo)!>

<!--begin::Aside Menu-->
<div class="hover-scroll-overlay-y my-5 my-lg-5" id="kt_aside_menu_wrapper" data-kt-scroll="true"
     data-kt-scroll-activate="{default: false, lg: true}" data-kt-scroll-height="auto"
     data-kt-scroll-dependencies="#kt_aside_logo, #kt_aside_footer"
     data-kt-scroll-wrappers="#kt_aside_menu" data-kt-scroll-offset="0">
    <!--begin::Menu-->
    <div class="menu menu-column menu-title-gray-600 menu-state-title-primary menu-state-icon-primary menu-state-bullet-primary menu-arrow-gray-500"
         id="#kt_aside_menu" data-kt-menu="true">

        <!-- USER -->
        <@menu.withSubMenu topMenuNo=topMenuNo siteMenu=SiteMenu.USER />

        <!-- ADMIN -->
        <div class="menu-item">
            <div class="menu-content mt-5 pb-2">
                <span class="menu-heading fw-bold text-uppercase fs-8 ls-1">ADMIN</span>
            </div>
        </div>

        <!--begin:Menu item-->
        <div data-kt-menu-trigger="click" class="menu-item here show menu-accordion">
            <!--begin:Menu link-->
            <span class="menu-link mb-1">
                <span class="menu-icon">
                    <i class="ki-duotone ki-element-11 fs-2">
                        <span class="path1"></span>
                        <span class="path2"></span>
                        <span class="path3"></span>
                        <span class="path4"></span>
                    </i>
                </span>
                <span class="menu-title">사이트 관리</span>
                <span class="menu-arrow"></span>
            </span>
            <!--end:Menu link-->
            <!--begin:Menu sub-->
            <div class="menu-sub menu-sub-accordion">
                <@menu.outer_menu siteMenu=SiteMenu.ADMIN_PAGE />
                <@menu.inner_menu siteMenu=SiteMenu.LGN_POLICY />
                <@menu.inner_menu siteMenu=SiteMenu.MENU />
                <@menu.inner_menu siteMenu=SiteMenu.BOARD_DEF />
                <@menu.inner_menu siteMenu=SiteMenu.TMPLAT />
                <@menu.inner_menu siteMenu=SiteMenu.POPUP />
                <@menu.inner_menu siteMenu=SiteMenu.CD />

                <div class="menu-item pt-0">
                    <div class="menu-content">
                        <a class="btn btn-flex btn-color-primary d-flex flex-stack fs-base p-0 ms-2 mb-2 toggle collapsible collapsed"
                           data-bs-toggle="collapse" href="#kt_app_sidebar_menu_dashboards_collapse"
                           data-kt-toggle-text="[ 접기 - ]">
                            <span data-kt-toggle-text-target="true">[ 더보기 + ]</span>
                            <i class="ki-duotone ki-minus-square toggle-on fs-2 me-0">
                                <span class="path1"></span>
                                <span class="path2"></span>
                            </i>
                            <i class="ki-duotone ki-plus-square toggle-off fs-2 me-0">
                                <span class="path1"></span>
                                <span class="path2"></span>
                                <span class="path3"></span>
                            </i>
                        </a>
                    </div>
                </div>
            </div>
            <!--end:Menu sub-->
        </div>
        <!--end:Menu item-->

        <!-- LOG -->
        <@menu.withSubMenu topMenuNo=topMenuNo siteMenu=SiteMenu.LOG />

        <div class="separator mx-5 mt-8 mb-3 opacity-25 blink"></div>

        <!-- VCATN -->
        <@menu.withSubMenu topMenuNo=topMenuNo siteMenu=SiteMenu.VCATN_ADMIN />

        <!-- EXPTR -->
        <@menu.withSubMenu topMenuNo=topMenuNo siteMenu=SiteMenu.EXPTR_ADMIN />


    </div>
    <!--end::Menu-->
</div>
<!--end::Aside Menu-->