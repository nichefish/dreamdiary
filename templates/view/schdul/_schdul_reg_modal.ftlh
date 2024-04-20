<#--
 * 일정 > 전체 일정 > 일정 등록 Modal
 * @included_in : schdul_cal
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="schdul_reg" size="lg">
    <!--begin::Modal header-->
    <div class="modal-header">
        <!--begin::Modal title-->
        <h2 class="fw-bolder" data-kt-calendar="title"></h2>
        <h2 class="fw-bolder" data-kt-calendar="add">일정 저장</h2>
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
    <!--begin::Modal Body-->
    <div class="modal-body py-10 px-lg-17">
        <!--begin::Form-->
        <form class="form" name="schdulRegForm" id="schdulRegForm">
            <div id="schdul_reg_div">
                <!-- handdlebars template goes in here -->
            </div>
        </form>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end gap-2">
            <!-- 버튼:: 저장하기 -->
            <@component.modal_btn_save "Schdul.submit();" />
            <!-- 버튼:: 모달 닫기 -->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/schdul/_schdul_reg_template.hbs">
<#include "/view/schdul/_schdul_reg_prtcpnt_template.hbs">