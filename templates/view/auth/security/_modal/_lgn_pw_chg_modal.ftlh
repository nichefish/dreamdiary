<#--
 * 로그인 화면 > 비밀번호 변경 Modal
 * @included_in : lgn_form.ftlh
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<#assign modal_title = "비밀번호 변경" />
<@modal.layout id="lgn_pw_chg" size="md">
    <#--begin::Modal Header-->
    <@component.modal_header_dark modal_title />
    <#--begin::Modal Body-->
    <div class="modal-body">
        <#--begin::Form-->
        <form name="lgnPwChgForm" id="lgnPwChgForm" class="form">
            <div id="lgn_pw_chg_div">
                <#--handlebars template goes in here-->
            </div>
        </form>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <#--모달:: 버튼: 저장하기-->
        <@component.modal_btn_save "dF.LgnPwChg.submit();" />
        <#--모달:: 버튼: 모달 닫기-->
        <@component.modal_btn_close />
    </div>
</@modal.layout>

<#--include:: Handlebars template-->
<#include "_lgn_pw_chg_modal_template.hbs">