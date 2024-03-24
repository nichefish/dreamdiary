<!--
 -- 레이아웃 > Footer
 -- @included in : layout
 -->
<!--begin::Footer-->
<#if asideAt!false>
    <div class="footer py-4 d-flex flex-lg-column" id="kt_footer">
        <!--begin::Container-->
        <div class="container-fluid d-flex flex-column flex-md-row align-items-center justify-content-between">
            <!--begin::Copyright-->
            <div class="text-dark order-2 order-md-1">
                <span class="text-muted opacity-hover fw-bold me-1 cursor-help"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="프로젝트 기간: &#10;2024.03.20 ~ (진행중)">
                    ©2022.
                </span>
                <a href="${SiteUrl.DOMAIN!}" target="_blank" class="text-gray-800 text-hover-primary fw-bold blink cursor-help"
                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="작업인원: &#10;nysnyari">
                    dreamdiary.nicheblog.io
                </a>
            </div>
            <!--end::Copyright-->
            <!--begin::Menu-->
            <ul class="menu menu-gray-600 menu-hover-primary fw-bold order-1 me-xl-13"
                data-kt-menu-trigger="hover" data-kt-menu-placement="top" data-kt-menu-flip="top-start">
                <#if isDev!false>
                    <li class="menu-item">
                        <a href="javascript:void(0);" target="_blank" class="menu-link px-2"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="About">
                            About
                        </a>
                    </li>
                    <li class="menu-item">
                        <a href="javascript:void(0);" target="_blank" class="menu-link px-2"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="Support">
                            Support
                        </a>
                    </li>
                </#if>
                <!-- begin::related websites -->
                <#include "/layout/footer/_footer_related.ftlh">
                <!-- end::related websites -->
            </ul>
            <!--end::Menu-->
        </div>
        <!--end::Container-->
    </div>
    <!--end::Footer-->
</#if>
