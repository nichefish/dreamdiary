<!--
 * 레이아웃 > 푸터 > 관련 사이트
 * @included_in : layout/footer/_footer_base.ftlh
 -->

<!--begin::Footer-Related-->
<li class="menu-item" data-kt-menu-trigger="hover" data-kt-menu-placement="top-start" data-kt-menu-flip="top-start">
    <a target="_blank" class="menu-link blink px-2">
        관련 사이트<i class="bi bi-chevron-up ms-1"></i>
    </a>
</li>
<div class="menu-sub menu-sub-lg-down-accordion menu-sub-lg-dropdown menu-rounded-0 py-lg-4 w-lg-225px" data-kt-menu="true">
    <div class="menu-item"
         data-bs-toggle="tooltip" data-bs-trigger="hover" data-bs-placement="left" title="홈페이지입니다.">
        <a class="menu-link py-3" onclick="window.open('${Url.MAIN!}', '_blank');">
            <span class="menu-icon">
                <img src="/media/img/logo/logo_racoon.jpg" class="img-fluid p-0" />
            </span>
            <span class="menu-title">홈페이지</span>
        </a>
    </div>
</div>
<!--end::Footer-Related-->