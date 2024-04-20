<#--
 * 분류 코드(cl_cd) > 상세 Modal
 * @included_in : cl_cd_list.ftlh
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="cl_cd_dtl" size="xl">
    <!--begin::Modal Header-->
    <@component.modal_header "분류코드 조회" />
    <!--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div id="cl_cd_dtl_div">
            <!-- contents -->
        </div>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <!-- 버튼:: 수정 모달 호출 -->
            <@component.modal_btn_modify "ClCd.mdfModal(ClCd.key);" />
            <!-- 버튼:: 모달 닫기 -->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/admin/cd/_cl_cd_dtl_template.hbs">