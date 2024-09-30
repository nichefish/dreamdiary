<#--
 * 저널 결산(jrnl_sumry) > 태그 Modal
 * @included_in : jrnl_sumry_dtl.ftlh
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="jrnl_sumry_tag" size="xl">
    <#--begin::Modal Header-->
    <@component.modal_header "저널 결산 태그 저장" />
    <#--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <#--begin::Form-->
        <form name="jrnlSumryTagForm" id="jrnlSumryTagForm" class="form" enctype="multipart/form-data">
            <div id="jrnl_sumry_tag_div">
                <#--Handlebars template goes in here-->
            </div>
        </form>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--모달:: 버튼: 저장하기-->
            <@component.modal_btn_save "JrnlSumry.tagAjax();" />
            <#--모달:: 버튼: 모달 닫기-->
            <@component.modal_btn_close_safe />
        </div>
    </div>
</@modal.layout>

<#--include:: Handlebars template-->
<#include "/view/jrnl/sumry/_jrnl_sumry_tag_template.hbs">