<#--
 * 분류 코드(cl_cd) > 등록 Modal
 * @included_in : cl_cd_list
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="cl_cd_reg" size="lg">
    <!--begin::Modal Header-->
    <@component.modal_header "분류코드 저장" />
    <!--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <form name="clCdRegForm" id="clCdRegForm">
            <div id="cl_cd_reg_div">
                <!-- contents -->
            </div>
        </form>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <!-- 버튼:: 저장하기 -->
            <@component.modal_btn_save "ClCd.submit();" />
            <!-- 버튼:: 모달 닫기 -->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/admin/cd/_cl_cd_reg_template.hbs">