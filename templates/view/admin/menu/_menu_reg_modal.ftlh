<#--
 * 메뉴(menu) > 상세 Modal
 * @included_in : menu_page.ftlh
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="menu_reg" size="lg">
    <#--begin::Modal Header-->
    <@component.modal_header "메뉴 저장" />
    <#--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <#--begin::Form-->
        <form name="menuRegForm" id="menuRegForm">
            <div id="menu_reg_div">
                <#--Handlebars template goes in here-->
            </div>
        </form>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer flex-end">
        <#--모달:: 버튼: 저장하기-->
        <@component.modal_btn_save "Menu.submit();" />
        <#--모달:: 버튼: 모달 닫기-->
        <@component.modal_btn_close />
    </div>
</@modal.layout>

<#--include:: Handlebars template-->
<#include "/view/admin/menu/_menu_reg_template.hbs">