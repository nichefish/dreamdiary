<#--
  -- 일정 > 전체 일정 > 상세 조회 Modal
  -- @included_in : schdul_cal
  -->

<div class="modal fade" id="schdul_dtl_modal" tabindex="-1" aria-hidden="true" data-bs-keyboard="false" data-bs-backdrop="static">
    <!--begin::Modal dialog-->
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <!--begin::Modal content-->
        <div class="modal-content">
            <!--begin::Modal header-->
            <div class="modal-header border-0">
                <h2 class="d-flex-center fw-bolder">
                    <i class="ki-duotone ki-calendar-8 fs-1 text-muted me-5">
                        <span class="path1"></span>
                        <span class="path2"></span>
                        <span class="path3"></span>
                        <span class="path4"></span>
                        <span class="path5"></span>
                        <span class="path6"></span>
                    </i>
                    일정 정보
                </h2>
                <!--begin::Edit-->
                <div class="float-end">
                    <!--begin::Close-->
                    <div class="btn btn-icon btn-sm btn-color-gray-500 btn-active-icon-primary"
                         data-bs-toggle="tooltip" title="Hide Event" data-bs-dismiss="modal">
                        <!--begin::Svg Icon | path: icons/duotune/arrows/arr061.svg-->
                        <span class="svg-icon svg-icon-1">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                                <rect opacity="0.5" x="6" y="17.3137" width="16" height="2" rx="1" transform="rotate(-45 6 17.3137)" fill="black"/>
                                <rect x="7.41422" y="6" width="16" height="2" rx="1" transform="rotate(45 7.41422 6)" fill="black"/>
                            </svg>
                        </span>
                        <!--end::Svg Icon-->
                    </div>
                    <!--end::Close-->
                </div>
            </div>
            <!--end::Modal header-->

            <!--begin::Modal body-->
            <div id="schdul_dtl_div" class="modal-body pt-0 pb-20 px-lg-17">
                <!-- handlebars template goes in here -->
            </div>
            <!--end::Modal body-->

            <div class="modal-footer">
                <!-- 버튼 영역 -->
                <div class="d-flex justify-content-end">
                    <button type="button" class="btn btn-sm btn-light btn-active-primary me-2 btn-sm"
                            onclick="Schdul.mdfForm();"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                        <i class="bi bi-pencil-square"></i>수정
                    </button>
                    <button type="button" class="btn btn-sm btn-light btn-active-danger me-2 btn-sm"
                            onclick="Schdul.delAjax();"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.del"/>">
                        <i class="bi bi-trash"></i>삭제
                    </button>
                    <!-- 버튼:: 모달 닫기 -->
                    <@component.btn_modal_close />
                </div>
            </div>
        </div>
    </div>
</div>

<!-- include:: Handlebars template -->
<#include "/view/schdul/_schdul_dtl_template.hbs">