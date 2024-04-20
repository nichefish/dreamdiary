<#--
 * 물품구매/경조사비 신청(exptr_reqst) > 상세 Modal
 * @included_in : exptr_reqst_list
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="exptr_reqst_dtl" size="xl">
    <!--begin::Modal Header-->
    <@component.modal_header "물품구매/경조사비 신청 조회" />
    <!--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div id="exptr_reqst_dtl_div">
            <!-- contents -->
        </div>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <!-- 버튼:: 모달 닫기 -->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/exptr/reqst/_exptr_reqst_dtl_template.hbs">