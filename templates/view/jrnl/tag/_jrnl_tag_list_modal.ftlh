<#--
 * 저널 태그(jrnl_tag) > 목록 Modal
 * @included_in : jrnl_day_page.ftlh
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="jrnl_tag_list" size="xl">
    <#--begin::Modal Header-->
    <@component.modal_header "태그 전체 목록" />
    <#--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div id="jrnl_tag_list_div">
            <#--Handlebars template goes in here-->
        </div>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--모달:: 버튼: 모달 닫기-->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<#--include:: Handlebars template-->
<#include "/view/jrnl/tag/_jrnl_tag_list_template.hbs">
