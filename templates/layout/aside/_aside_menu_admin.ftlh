<!--
 -- 레이아웃 > 사이드바 메뉴 (관리자)
 -- @included in : aside/_aside_base
 -->
<#import "/layout/aside/_aside_menu_template.ftlh" as menu >

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

        <!-- SITE -->
        <@menu.withSubMenu topMenuNo=topMenuNo siteMenu=SiteMenu.ADMIN />

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