<#--
 * 공지사항(notice) > 상세 Modal
 * @included_in : notice_list.ftlh
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="notice_dtl" size="xl">
    <#--begin::Modal Header-->
    <@component.modal_header "공지사항 조회" />
    <#--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div id="notice_dtl_div">
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
<#include "/view/notice/_notice_dtl_template.hbs">