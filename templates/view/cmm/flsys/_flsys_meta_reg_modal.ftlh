<#--
 * 파일시스템 메타(flsys_meta) > 등록 Modal
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="flsys_meta_reg" size="xl">
    <#--begin::Modal Header-->
    <@component.modal_header "파일시스템 메타 정보 저장" />
    <#--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <form class="form" name="flsysMetaRegForm" id="flsysMetaRegForm">
            <div id="flsys_meta_reg_div">
                <#--Handlebars template goes in here-->
            </div>
        </form>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--모달:: 버튼: 저장하기-->
            <@component.modal_btn_save "FlsysMeta.submit();" />
            <#--모달:: 버튼: 모달 닫기-->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<#--include:: Handlebars template-->
<#include "/view/cmm/flsys/_flsys_meta_reg_template.hbs">