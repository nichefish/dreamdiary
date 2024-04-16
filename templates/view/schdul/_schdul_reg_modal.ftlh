<#--
  -- 일정 > 전체 일정 > 일정 등록 Modal
  -- @included_in : schdul_cal
  -->

<div class="modal fade" id="schdul_reg_modal" tabindex="-1" aria-hidden="true" data-bs-keyboard="false" data-bs-backdrop="static">
    <!--begin::Modal dialog-->
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <!--begin::Modal content-->
        <div class="modal-content">
            <!--begin::Modal header-->
            <div class="modal-header">
                <!--begin::Modal title-->
                <h2 class="fw-bolder" data-kt-calendar="title"></h2>
                <h2 class="fw-bolder" data-kt-calendar="add">일정 <span class="regOrMdfSpan">등록</span></h2>
                <!--end::Modal title-->
                <!--begin::Close-->
                <div class="btn btn-icon btn-sm btn-active-icon-primary" data-bs-dismiss="modal" aria-label="Close">
                    <!--begin::Svg Icon | path: icons/duotune/arrows/arr061.svg-->
                    <span class="svg-icon svg-icon-1">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <rect opacity="0.5" x="6" y="17.3137" width="16" height="2" rx="1" transform="rotate(-45 6 17.3137)" fill="black" />
                            <rect x="7.41422" y="6" width="16" height="2" rx="1" transform="rotate(45 7.41422 6)" fill="black" />
                        </svg>
                    </span>
                    <!--end::Svg Icon-->
                </div>
                <!--end::Close-->
            </div>
            <!--end::Modal header-->
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body py-10 px-lg-17">
                <!--begin::Form-->
                <form class="form" name="schdulRegForm" id="schdulRegForm">
                    <div id="schdul_reg_div">
                        <!-- handdlebars template goes in here -->
                    </div>
                </form>
            </div>
            <!--end::Modal body-->
            <!--end::Form-->
            <!--begin::Modal footer-->
            <div class="modal-footer flex-end">
                <button type="button" class="btn btn-primary" onclick="commons.util.submit('#schdulRegForm');"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                    <i class="bi bi-pencil-square"></i>
                    <span class="indicator-label">저장</span>
                    <span class="indicator-progress">
                        Please wait...
                        <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                    </span>
                </button>
                <button type="reset" class="btn btn-light me-3" data-bs-dismiss="modal" aria-label="Close">
                    닫기
                </button>
            </div>
            <!--end::Modal footer-->
        </div>
    </div>
</div>

<!-- include:: Handlebars template -->
<#include "/view/schdul/_schdul_reg_template.hbs">
<#include "/view/schdul/_schdul_reg_prtcpnt_template.hbs">