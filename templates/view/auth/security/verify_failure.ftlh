<#--
 * 에러 페이지
 * (사이드바 없음)
 -->

<#import "/layout/layout_without_sidebar.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<div class="d-flex-center">
    <span class="ctgr-span ctgr-imprtc">Authentication Failure Page</span>
</div>
<div class="d-flex-center flex-column mt-8">
    <div><@spring.message 'error.verification'/></div>

    <div class="text-gray-500 fw-bold min-h-100px">${errorMsg!}</div>

    <div class="mt-10">
        <button type="button" class="btn btn-sm btn-outlined btn-light-primary btn-active-primary py-2 px-3"
                onclick="Page.main();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message 'bs.tooltip.return-to-main'/>">
            <i class="bi bi-arrow-counterclockwise"></i><@spring.message 'view.return-to-main'/>...
        </button>
    </div>
</div>

<#--include::script modules-->
<script type="text/javascript" src="/js/view/global/_common/error/error_page.js?${releaseDate!}"></script>

</@layout.layout>