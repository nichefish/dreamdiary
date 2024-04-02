<!--
 -- 레이아웃 > 툴바
 -- @included in : layout
 -->
<!--begin::Toolbar-->
<div class="toolbar" id="kt_toolbar m-0 p-0">
    <!--begin::Container-->
    <div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
        <!--begin::Page title-->
        <div data-kt-swapper="true" data-kt-swapper-mode="prepend"
             data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
             class="page-title d-flex align-items-center flex-wrap me-3 mb-5 mb-lg-0">
            <!--begin::Title-->
            <h1 class="d-flex align-items-center text-dark fw-bolder fs-3 my-1">
                <!-- breadcrumb -->
                <ol class="breadcrumb text-muted fs-6 fw-semibold">
                    <li class="breadcrumb-item">
                        <a href="<#if isMngrMode>${SiteUrl.ADMIN_MAIN}<#else>${SiteUrl.MAIN}</#if>" class="">
                            <i class="bi bi-house-<#if isMngrMode>gear-</#if>fill fs-4 me-1 text-primary"></i>Home
                        </a>
                    </li>
                    <#if !(siteAcsInfo.isTopMenu)>
                        <li class="breadcrumb-item text-muted">${(siteAcsInfo.topMenuNm)!}</li>
                    </#if>
                    <li class="breadcrumb-item text-muted">${(siteAcsInfo.menuNm)!}</li>
                </ol>
                <!--begin::Separator-->
                <span class="h-20px border-gray-200 border-start ms-3 mx-2"></span>
                <!--end::Separator-->
                <!--begin::Description-->
                <small class="text-muted fs-7 fw-bold my-1 ms-1">${(siteAcsInfo.acsPageNm)!}</small>
                <!--end::Description-->
            </h1>
            <!--end::Title-->
        </div>
        <!--end::Page title-->
    </div>
    <!--end::Container-->
</div>
<!--end::Toolbar-->