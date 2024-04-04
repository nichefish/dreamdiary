<!--
 -- 레이아웃 > 사이드바 메뉴 (사용자)
 -- @included_in : aside/_aside_base
 -->
<#import "/layout/sidebar/_sidebar_menu_template.ftlh" as menu >

<#assign topMenuNo = (siteAcsInfo.topMenuNo)!>
<#assign siteMenuNo = (siteAcsInfo.siteMenuNo)!>

<!--begin::sidebar menu-->
<div class="app-sidebar-menu overflow-hidden flex-column-fluid">
    <!--begin::Menu wrapper-->
    <div id="kt_app_sidebar_menu_wrapper" class="app-sidebar-wrapper">
        <!--begin::Scroll wrapper-->
        <div id="kt_app_sidebar_menu_scroll" class="scroll-y my-5 mx-3"
            data-kt-scroll="true"
            data-kt-scroll-activate="true"
            data-kt-scroll-height="auto"
            data-kt-scroll-dependencies="#kt_app_sidebar_logo, #kt_app_sidebar_footer"
            data-kt-scroll-wrappers="#kt_app_sidebar_menu"
            data-kt-scroll-offset="5px"
            data-kt-scroll-save-state="true">

            <!--begin::Menu-->
            <div id="#kt_app_sidebar_menu" class="menu menu-column menu-rounded menu-sub-indention fw-semibold fs-6"
                    data-kt-menu="true"
                    data-kt-menu-expand="false">

                <!-- NOTICE -->
                <@menu.noSubMenu topMenuNo=topMenuNo siteMenu=SiteMenu.NOTICE />

                <!-- DREAM -->
                <@menu.extendedSubMenu topMenuNo=topMenuNo siteMenu=SiteMenu.DREAM />

                <!-- BOARD -->
                <@menu.withSubMenu topMenuNo=topMenuNo siteMenu=SiteMenu.BOARD />

                <div class="separator mx-5 mt-8 mb-3 opacity-25 blink"></div>

                <!-- SCHDUL -->
                <@menu.withSubMenu topMenuNo=topMenuNo siteMenu=SiteMenu.SCHDUL />

                <!-- EXPTR -->
                <@menu.withSubMenu topMenuNo=topMenuNo siteMenu=SiteMenu.EXPTR />

                <!--begin:Menu item-->
                <div data-kt-menu-trigger="click" class="menu-item here show menu-accordion">
                    <!--begin:Menu link-->
                    <span class="menu-link">
                        <span class="menu-icon">
                            <i class="ki-duotone ki-element-11 fs-2">
                                <span class="path1"></span>
                                <span class="path2"></span>
                                <span class="path3"></span>
                                <span class="path4"></span>
                            </i>
                        </span>
                        <span class="menu-title">Dashboards</span>
                        <span class="menu-arrow"></span>
                    </span><!--end:Menu link-->
                    <!--begin:Menu sub-->
                    <div class="menu-sub menu-sub-accordion">
                        <!--begin:Menu item-->
                        <div class="menu-item">
                            <!--begin:Menu link-->
                            <a class="menu-link active" href="?page=index">
                                <span class="menu-bullet">
                                    <span class="bullet bullet-dot"></span>
                                </span>
                                <span class="menu-title">Default</span>
                            </a>
                            <!--end:Menu link-->
                        </div>
                        <!--end:Menu item-->
                        <!--begin:Menu item-->
                        <div class="menu-item">
                            <!--begin:Menu link-->
                            <a class="menu-link" href="?page=dashboards/ecommerce">
                                <span class="menu-bullet">
                                    <span class="bullet bullet-dot"></span>
                                </span>
                                <span class="menu-title">eCommerce</span>
                            </a>
                            <!--end:Menu link-->
                        </div>
                        <!--end:Menu item-->
                        <!--begin:Menu item-->
                        <div class="menu-item">
                            <!--begin:Menu link-->
                            <a class="menu-link" href="?page=dashboards/projects">
                                <span class="menu-bullet">
                                    <span class="bullet bullet-dot"></span>
                                </span>
                                <span class="menu-title">Projects</span>
                            </a>
                            <!--end:Menu link-->
                        </div>
                        <!--end:Menu item-->
                        <!--begin:Menu item-->
                        <div class="menu-item">
                            <!--begin:Menu link-->
                            <a class="menu-link" href="?page=dashboards/online-courses">'
                                <span
                                        class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                        class="menu-title">Online Courses</span></a><!--end:Menu link--></div>
                        <!--end:Menu item--><!--begin:Menu item-->
                        <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                        href="?page=dashboards/marketing"><span
                                        class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                        class="menu-title">Marketing</span></a><!--end:Menu link--></div>
                        <!--end:Menu item-->
                        <div class="menu-inner flex-column collapse " id="kt_app_sidebar_menu_dashboards_collapse">
                            <!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/bidding"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Bidding</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/pos"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">POS System</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/call-center"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Call Center</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/logistics"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Logistics</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/website-analytics"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Website Analytics</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/finance-performance"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Finance Performance</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/store-analytics"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Store Analytics</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/social"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Social</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/delivery"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Delivery</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/crypto"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Crypto</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/school"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">School</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link"
                                                                            href="?page=dashboards/podcast"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Podcast</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--><!--begin:Menu item-->
                            <div class="menu-item"><!--begin:Menu link--><a class="menu-link" href="?page=landing"><span
                                            class="menu-bullet"><span class="bullet bullet-dot"></span></span><span
                                            class="menu-title">Landing</span></a><!--end:Menu link--></div>
                            <!--end:Menu item--></div>
                        <div class="menu-item">
                            <div class="menu-content">
                                <a class="btn btn-flex btn-color-primary d-flex flex-stack fs-base p-0 ms-2 mb-2 toggle collapsible collapsed"
                                   data-bs-toggle="collapse" href="#kt_app_sidebar_menu_dashboards_collapse"
                                   data-kt-toggle-text="Show Less">
                                    <span data-kt-toggle-text-target="true">Show 12 More</span> <i
                                            class="ki-duotone ki-minus-square toggle-on fs-2 me-0"><span
                                                class="path1"></span><span class="path2"></span></i><i
                                            class="ki-duotone ki-plus-square toggle-off fs-2 me-0"><span
                                                class="path1"></span><span class="path2"></span><span
                                                class="path3"></span></i>
                                </a>
                            </div>
                        </div>
                    </div>
                    <!--end:Menu sub-->
                </div>
                <!--end:Menu item-->



            </div>
            <!--end::Menu-->
        </div>
        <!--end::Scroll wrapper-->

    </div>
    <!--end::Menu wrapper-->

</div>
<!--end::sidebar menu-->

