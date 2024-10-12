<!--
 * 레이아웃 > 탑바 > 노션 메뉴 버튼
 * @included_in : layout/topbar
 -->

<div class="app-navbar-item align-items-stretch mx-1">
    <div class="d-flex align-items-center">
        <div class="btn btn-icon btn-active-light-primary blink position-relative w-30px h-30px w-md-40px h-md-40px"
             data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click"
             title="노션 연동&#10;화면으로 이동합니다.&#10;(Alpha version)"
             onclick="location.replace('${Url.NOTION_HOME!}?notionPageId=');">
            <#--begin::Svg Icon | path: icons/duotune/general/gen022.svg-->
            <span class="svg-primary svg-icon svg-icon-1">
                <#--begin::Svg Icon | path: /var/www/preview.keenthemes.com/keenthemes/metronic/docs/core/html/src/media/icons/duotune/general/gen017.svg-->
                <span class="svg-icon svg-icon-muted svg-icon-1hx">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <title>노션 연동&#10;화면으로 이동합니다.&#10;(Alpha version)</title>
                        <path opacity="0.3" d="M5 8.04999L11.8 11.95V19.85L5 15.85V8.04999Z" fill="currentColor"/>
                        <path d="M20.1 6.65L12.3 2.15C12 1.95 11.6 1.95 11.3 2.15L3.5 6.65C3.2 6.85 3 7.15 3 7.45V16.45C3 16.75 3.2 17.15 3.5 17.25L11.3 21.75C11.5 21.85 11.6 21.85 11.8 21.85C12 21.85 12.1 21.85 12.3 21.75L20.1 17.25C20.4 17.05 20.6 16.75 20.6 16.45V7.45C20.6 7.15 20.4 6.75 20.1 6.65ZM5 15.85V7.95L11.8 4.05L18.6 7.95L11.8 11.95V19.85L5 15.85Z" fill="currentColor"/>
                    </svg>
                </span>
                <#--end::Svg Icon-->
            </span>
            <#--end::Svg Icon-->
        </div>
    </div>
</div>