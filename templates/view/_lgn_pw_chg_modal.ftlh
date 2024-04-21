<#--
 * 로그인 화면 > 비밀번호 변경 Modal
 * @included_in : lgn_form.ftlh
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="lgn_pw_chg" size="md">
    <!--begin::Modal Header-->
    <@component.modal_header_dark "비밀번호 변경" />
    <!--begin::Modal Body-->
    <div class="modal-body">
        <form name="lgnPwChgForm" id="lgnPwChgForm" class="form">
            <div id="lgn_pw_chg_div">
                <!-- handlebars template goes in here -->
            </div>
        </form>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <button type="button" class="btn btn-sm btn-primary"
                onclick="commons.util.submit(document.lgnPwChgForm);"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="입력 내용을 저장합니다.">
            <i class="bi bi-pencil-square"></i>수정
        </button>
        <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal" onclick="commons.util.blockUIReload();">
            닫기
        </button>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/_lgn_pw_chg_template.hbs">