<#--
 -- 상세 코드(dtl_cd) 등록/수정 Modal
 -- @included_in : cl_cd_dtl
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="dtl_cd_reg" size="lg">
    <#--begin::Modal Header-->
    <@component.modal_header "상세코드 저장" />
    <#--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <#--begin::Form-->
        <form name="dtlCdRegForm" id="dtlCdRegForm">
            <div id="dtl_cd_reg_div">
                <#--Handlebars template goes in here-->
            </div>
        </form>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--모달:: 버튼: 저장하기-->
            <@component.modal_btn_save "DtlCd.submit();" />
            <#--모달:: 버튼: 모달 닫기-->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<#--include:: Handlebars template-->
<#include "/view/admin/cd/_dtl_cd_reg_template.hbs">