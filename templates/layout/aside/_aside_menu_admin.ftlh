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
        <div class="menu-item">
            <div class="menu-content pt-8 pb-2">
                <span class="menu-section text-muted text-uppercase fs-8 ls-1">user</span>
            </div>
        </div>
        <!-- 사용자 관리 메뉴 시작 -->
        <#assign isNotTopMenu = (topMenuNo != SiteMenu.USER.topMenuNo!)!>
        <div data-kt-menu-trigger="click" class="menu-item menu-accordion show" id="${SiteMenu.USER.topMenuNo!}">
            <span class="menu-link <#if isNotTopMenu> text-inverse-light</#if>">
                <span class="menu-icon">
                    <i class="fas fa-user-cog text-gray-600 fs-3"></i>
                </span>
                <span class="menu-title <#if isNotTopMenu> text-inverse-light</#if>">사용자 관리</span>
                <span class="menu-arrow"></span>
            </span>
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <!-- 사용자 관리 > 계정 및 권한 관리 -->
                <div class="menu-item" id="${SiteMenu.USER.menuNo!}">
                    <a class="menu-link" href="${SiteMenu.USER.url!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.USER.menuNm!}</span>
                    </a>
                </div>
            </div>
        </div>
        <!-- 사용자관리 메뉴 끝 -->
        <!-- SITE -->
        <div class="menu-item">
            <div class="menu-content pt-8 pb-2">
                <span class="menu-section text-muted text-uppercase fs-8 ls-1">site</span>
            </div>
        </div>
        <!-- 사이트 관리 메뉴 시작 -->
        <#assign isNotTopMenu = (topMenuNo != SiteMenu.ADMIN.topMenuNo!)!>
        <div data-kt-menu-trigger="click" class="menu-item menu-accordion show" id="${SiteMenu.ADMIN.topMenuNo!}">
            <span class="menu-link <#if isNotTopMenu>text-inverse-light</#if>">
                <span class="menu-icon">
                    <span class="svg-icon svg-icon-3">
                        <i class="fas fa-globe text-gray-600 fs-3"></i>
                    </span>
                </span>
                <span class="menu-title <#if isNotTopMenu>text-inverse-light</#if>">사이트 관리</span>
                <span class="menu-arrow"></span>
            </span>
            <!-- 사이트 관리 > 게시판 관리 -->
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <div class="menu-item" id="${SiteMenu.BOARD_DEF.menuNo!}">
                    <a class="menu-link" href="${SiteMenu.BOARD_DEF.url!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.BOARD_DEF.menuNm!}</span>
                    </a>
                </div>
            </div>
            <!-- 사이트 관리 > 로그인 정책 관리 -->
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <div class="menu-item" id="${SiteMenu.LGN_POLICY.menuNo!}">
                    <a class="menu-link" href="${SiteMenu.LGN_POLICY.url!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.LGN_POLICY.menuNm!}</span>
                    </a>
                </div>
            </div>
        </div>
        <!-- 사이트 관리 메뉴 끝 -->

        <div class="menu-item">
            <div class="menu-content pt-8 pb-2">
                <span class="menu-section text-muted text-uppercase fs-8 ls-1">log</span>
            </div>
        </div>
        <!-- 로그 관리 메뉴 시작 -->
        <#assign isNotTopMenu = (topMenuNo != SiteMenu.ADMIN.topMenuNo!)!>
        <div data-kt-menu-trigger="click" class="menu-item menu-accordion show" id="${SiteMenu.ADMIN.topMenuNo!}">
            <span class="menu-link <#if isNotTopMenu> text-inverse-light</#if>">
                <span class="menu-icon">
                    <span class="svg-icon svg-icon-3">
                        <i class="fa-solid fa-arrow-down-short-wide text-gray-600 fs-3"></i>
                    </span>
                </span>
                <span class="menu-title <#if isNotTopMenu> text-inverse-light</#if>">로그 관리</span>
                <span class="menu-arrow"></span>
            </span>
            <!-- 사이트 관리 > 활동 로그 조회 -->
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <div class="menu-item" id="${SiteMenu.LOG_ACTVTY.menuNo!}">
                    <a class="menu-link" href="${SiteMenu.LOG_ACTVTY.url!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.LOG_ACTVTY.menuNm!}</span>
                    </a>
                </div>
            </div>
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <div class="menu-item" id="${SiteMenu.LOG_SYS.menuNo!}">
                    <a class="menu-link" href="${SiteMenu.LOG_SYS.url!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.LOG_SYS.menuNm!}</span>
                    </a>
                </div>
            </div>
        </div>

    </div>
    <!--end::Menu-->
</div>
<!--end::Aside Menu-->