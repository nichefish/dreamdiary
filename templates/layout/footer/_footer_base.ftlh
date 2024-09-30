<!--
 * 레이아웃 > 푸터 (base)
 * @included_in : layout
 -->

<!--begin::Footer-->
<div id="kt_app_footer" class="app-footer">
    <!--begin::Footer container-->
    <div class="app-container container-fluid d-flex flex-column flex-md-row flex-center flex-md-stack py-3 ">
        <!--begin::Copyright-->
        <div class="order-1 order-md-1 text-gray-900">
            <span class="text-muted fw-semibold me-1"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="프로젝트 기간: &#10;2024.03.20 ~ (진행중)">
                2024&copy;
            </span>
            <a href="${Url.MAIN!}" target="_blank" class="text-gray-600 text-hover-primary fw-bold blink cursor-help"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="작업인원: &#10;nysnyari">
                ${Url.DOMAIN!}
            </a>
        </div>
        <!--begin::Menu-->
        <ul class="order-2 menu menu-gray-600 menu-hover-primary fw-semibold order-1">
            <li class="menu-item">
                <a href="https://keenthemes.com" target="_blank" class="menu-link px-2">About</a>
            </li>
            <li class="menu-item">
                <a href="https://preview.keenthemes.com/html/metronic/docs/index" target="_blank" class="menu-link px-2">Metronic</a>
            </li>
            <#--include::Related Websites-->
            <#include "/layout/footer/_footer_related.ftlh">
        </ul>
        <!--end::Menu-->
    </div>
    <!--end::Footer container-->
</div>
<!--end::Footer-->
