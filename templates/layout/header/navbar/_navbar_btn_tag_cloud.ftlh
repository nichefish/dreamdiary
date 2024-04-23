<!--
 * 레이아웃 > 탑바 > 태그클라우드 메뉴 버튼
 * @included_in : layout/topbar
 -->

<div class="app-navbar-item mx-1">
    <div class="d-flex align-items-center">
        <div class="btn btn-icon btn-active-light-primary blink position-relative w-30px h-30px w-md-40px h-md-40px"
             data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click"
             title="태그클라우드&#10;화면으로 이동합니다."
             onclick="location.replace('${Url.TAG_CLOUD_PAGE!}');">
            <#--begin::Svg Icon | path: icons/duotune/general/gen022.svg-->
            <span class="svg-primary svg-icon svg-icon-1">
                <#--begin::Svg Icon | path: /var/www/preview.keenthemes.com/keenthemes/metronic/docs/core/html/src/media/icons/duotune/abstract/abs026.svg-->
                <span class="svg-icon svg-icon-muted svg-icon-1hx">
                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="24px" height="24px" viewBox="0 0 24 24" version="1.1">
                        <title>태그클라우드&#10;화면으로 이동합니다.</title>
                        <defs/>
                        <g stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                            <polygon points="0 0 24 0 24 24 0 24"/>
                            <path opacity="0.8" d="M3.52270623,14.028695 C2.82576459,13.3275941 2.82576459,12.19529 3.52270623,11.4941891 L11.6127629,3.54050571 C11.9489429,3.20999263 12.401513,3.0247814 12.8729533,3.0247814 L19.3274172,3.0247814 C20.3201611,3.0247814 21.124939,3.82955935 21.124939,4.82230326 L21.124939,11.2583059 C21.124939,11.7406659 20.9310733,12.2027862 20.5869271,12.5407722 L12.5103155,20.4728108 C12.1731575,20.8103442 11.7156477,21 11.2385688,21 C10.7614899,21 10.3039801,20.8103442 9.9668221,20.4728108 L3.52270623,14.028695 Z M16.9307214,9.01652093 C17.9234653,9.01652093 18.7282432,8.21174298 18.7282432,7.21899907 C18.7282432,6.22625516 17.9234653,5.42147721 16.9307214,5.42147721 C15.9379775,5.42147721 15.1331995,6.22625516 15.1331995,7.21899907 C15.1331995,8.21174298 15.9379775,9.01652093 16.9307214,9.01652093 Z" fill="#000000" fill-rule="nonzero" />
                        </g>
                    </svg><#--end::Svg Icon-->
                </span>
                <#--end::Svg Icon-->
            </span>
            <#--end::Svg Icon-->
        </div>
    </div>
</div>