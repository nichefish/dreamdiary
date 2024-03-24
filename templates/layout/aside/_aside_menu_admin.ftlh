<!--
 -- 레이아웃 > 사이드바 메뉴 (관리자)
 -- @included in : aside/_aside_base
 -->
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
        <#assign isNotTopMenu = (topMenuNo != SiteMenu.TOP_MENU_NO_USER!)!>
        <div data-kt-menu-trigger="click" class="menu-item menu-accordion show" id="${SiteMenu.TOP_MENU_NO_USER!}">
            <span class="menu-link <#if isNotTopMenu> text-inverse-light</#if>">
                <span class="menu-icon">
                    <i class="fas fa-user-cog text-gray-600 fs-3"></i>
                </span>
                <span class="menu-title <#if isNotTopMenu> text-inverse-light</#if>">사용자 관리</span>
                <span class="menu-arrow"></span>
            </span>
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <!-- 사용자 관리 > 계정 및 권한 관리 -->
                <div class="menu-item" id="${SiteMenu.MENU_USER_INFO[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_USER_INFO[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_USER_INFO[2]!}</span>
                    </a>
                </div>
                <#if isDev!false>
                <!-- 사용자 관리 > 개발자 이력 관리 -->
                <div class="menu-item" id="${SiteMenu.MENU_USER_PROFL[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_USER_PROFL[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_USER_PROFL[2]!}</span>
                    </a>
                </div>
                </#if>
            </div>
        </div>
        <!-- 사용자관리 메뉴 끝 -->
        <#if isDev!false>
        <!-- PROJECT -->
        <div class="menu-item">
            <div class="menu-content pt-8 pb-2">
                <span class="menu-section text-muted text-uppercase fs-8 ls-1">Project</span>
            </div>
        </div>
        <#assign isNotTopMenu = (topMenuNo != SiteMenu.TOP_MENU_NO_PRJCT!)!>
        <div data-kt-menu-trigger="click" class="menu-item menu-accordion show" id="${SiteMenu.TOP_MENU_NO_PRJCT!}">
        <span class="menu-link <#if isNotTopMenu> text-inverse-light</#if>">
            <span class="menu-icon">
                <i class="fas fa-diagram-project text-gray-600 fs-3"></i>
            </span>
            <span class="menu-title <#if isNotTopMenu> text-inverse-light</#if>">프로젝트</span>
                <span class="menu-arrow"></span>
            </span>
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <!-- 프로젝트 > 프로젝트 관리 -->
                <div class="menu-item" id="${SiteMenu.MENU_PRJCT_INFO[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_PRJCT_INFO[3]!}">
                    <span class="menu-bullet">
                        <span class="bullet bullet-dot"></span>
                    </span>
                    <span class="menu-title">${SiteMenu.MENU_PRJCT_INFO[2]!}</span>
                    </a>
                </div>
            </div>
        </div>
        </#if>
        <!-- VACATION -->
        <div class="menu-item">
            <div class="menu-content pt-8 pb-2">
                <span class="menu-section text-muted text-uppercase fs-8 ls-1">Vacation</span>
            </div>
        </div>
        <!-- 휴가관리 메뉴 시작 -->
        <#assign isNotTopMenu = (topMenuNo != SiteMenu.TOP_MENU_NO_SCHDUL!)!>
        <div data-kt-menu-trigger="click" class="menu-item menu-accordion show" id="${SiteMenu.TOP_MENU_NO_SCHDUL!}">
            <span class="menu-link <#if isNotTopMenu> text-inverse-light</#if>">
                <span class="menu-icon">
                    <i class="fas fa-umbrella-beach text-gray-600 fs-3"></i>
                </span>
                <span class="menu-title <#if isNotTopMenu> text-inverse-light</#if>">휴가 및 일정관리</span>
                <span class="menu-arrow"></span>
            </span>
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <!-- 휴가관리 > 년도별 휴가관리 -->
                <div class="menu-item" id="${SiteMenu.MENU_VCATN_STATS_YY[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_VCATN_STATS_YY[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_VCATN_STATS_YY[2]!}</span>
                    </a>
                </div>
                <!-- 휴가관리 > 휴가사용일자 -->
                <div class="menu-item" id="${SiteMenu.MENU_VCATN_DY[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_VCATN_DY[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_VCATN_DY[2]!}</span>
                    </a>
                </div>
            </div>
        </div>
        <!-- 휴가관리 메뉴 끝 -->
        <!-- EXPENSE -->
        <div class="menu-item">
            <div class="menu-content pt-8 pb-2">
                <span class="menu-section text-muted text-uppercase fs-8 ls-1">EXPENSE</span>
            </div>
        </div>
        <!-- 경비관리 메뉴 시작 -->
        <#assign isNotTopMenu = (topMenuNo != SiteMenu.TOP_MENU_NO_EXPTR!)!>
        <div data-kt-menu-trigger="click" class="menu-item menu-accordion show" id="${SiteMenu.TOP_MENU_NO_EXPTR!}">
            <span class="menu-link <#if isNotTopMenu>text-inverse-light</#if>">
                <span class="menu-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-calculator-fill" viewBox="0 0 16 16">
                        <path d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2zm2 .5v2a.5.5 0 0 0 .5.5h7a.5.5 0 0 0 .5-.5v-2a.5.5 0 0 0-.5-.5h-7a.5.5 0 0 0-.5.5zm0 4v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5zM4.5 9a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1zM4 12.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5zM7.5 6a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1zM7 9.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5zm.5 2.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1zM10 6.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5zm.5 2.5a.5.5 0 0 0-.5.5v4a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-4a.5.5 0 0 0-.5-.5h-1z"/>
                    </svg>
                </span>
                <span class="menu-title <#if isNotTopMenu>text-inverse-light</#if>">경비관리</span>
                <span class="menu-arrow"></span>
            </span>
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <!-- 경비관리 > 경비지출누적집계 -->
                <div class="menu-item" id="${SiteMenu.MENU_EXPTR_INDVD_STATS[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_EXPTR_INDVD_STATS[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_EXPTR_INDVD_STATS[2]!}</span>
                    </a>
                </div>
                <!-- 경비관리 > 월간지출내역 -->
                <div class="menu-item" id="${SiteMenu.MENU_EXPTR_INDVD_RPT[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_EXPTR_INDVD_RPT[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_EXPTR_INDVD_RPT[2]!}</span>
                    </a>
               </div>
            </div>
        </div>
        <!-- 경비관리 메뉴 끝 -->
        <!-- SITE -->
        <div class="menu-item">
            <div class="menu-content pt-8 pb-2">
                <span class="menu-section text-muted text-uppercase fs-8 ls-1">site</span>
            </div>
        </div>
        <!-- 사이트 관리 메뉴 시작 -->
        <#assign isNotTopMenu = (topMenuNo != SiteMenu.TOP_MENU_NO_ADMIN!)!>
        <div data-kt-menu-trigger="click" class="menu-item menu-accordion show" id="${SiteMenu.TOP_MENU_NO_ADMIN!}">
            <span class="menu-link <#if isNotTopMenu>text-inverse-light</#if>">
                <span class="menu-icon">
                    <span class="svg-icon svg-icon-3">
                        <i class="fas fa-globe text-gray-600 fs-3"></i>
                    </span>
                </span>
                <span class="menu-title <#if isNotTopMenu>text-inverse-light</#if>">사이트 관리</span>
                <span class="menu-arrow"></span>
            </span>
            <!-- 사이트 관리 > 사이트 관리 -->
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <div class="menu-item" id="${SiteMenu.MENU_SITE_MNG[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_SITE_MNG[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_SITE_MNG[2]!}</span>
                    </a>
                </div>
            </div>
            <!-- 사이트 관리 > 게시판 관리 -->
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <div class="menu-item" id="${SiteMenu.MENU_BOARD_DEF[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_BOARD_DEF[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_BOARD_DEF[2]!}</span>
                    </a>
                </div>
            </div>
            <!-- 사이트 관리 > 로그인 정책 관리 -->
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <div class="menu-item" id="${SiteMenu.MENU_LGN_POLICY[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_LGN_POLICY[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_LGN_POLICY[2]!}</span>
                    </a>
                </div>
            </div>
            <#if isDev!false>
            <!-- 사이트 관리 > 템플릿 관리 -->
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <div class="menu-item" id="${SiteMenu.MENU_TMPLAT[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_TMPLAT[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_TMPLAT[2]!} (작업중)</span>
                    </a>
                </div>
            </div>
            <!-- 사이트 관리 > 메뉴 관리 -->
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <div class="menu-item" id="${SiteMenu.MENU_MENU[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_MENU[3]!}">
                    <span class="menu-bullet">
                        <span class="bullet bullet-dot"></span>
                    </span>
                        <span class="menu-title">${SiteMenu.MENU_MENU[2]!} (작업중)</span>
                    </a>
                </div>
            </div>
            </#if>
        </div>
        <!-- 사이트 관리 메뉴 끝 -->

        <div class="menu-item">
            <div class="menu-content pt-8 pb-2">
                <span class="menu-section text-muted text-uppercase fs-8 ls-1">log</span>
            </div>
        </div>
        <!-- 로그 관리 메뉴 시작 -->
        <#assign isNotTopMenu = (topMenuNo != SiteMenu.TOP_MENU_NO_ADMIN!)!>
        <div data-kt-menu-trigger="click" class="menu-item menu-accordion show" id="${SiteMenu.TOP_MENU_NO_ADMIN!}">
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
                <div class="menu-item" id="${SiteMenu.MENU_LOG_ACTVTY[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_LOG_ACTVTY[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_LOG_ACTVTY[2]!}</span>
                    </a>
                </div>
            </div>
            <div class="menu-sub menu-sub-accordion menu-active-bg">
                <div class="menu-item" id="${SiteMenu.MENU_LOG_SYS[1]!}">
                    <a class="menu-link" href="${SiteMenu.MENU_LOG_SYS[3]!}">
                        <span class="menu-bullet">
                            <span class="bullet bullet-dot"></span>
                        </span>
                        <span class="menu-title">${SiteMenu.MENU_LOG_SYS[2]!}</span>
                    </a>
                </div>
            </div>
        </div>

    </div>
    <!--end::Menu-->
</div>
<!--end::Aside Menu-->