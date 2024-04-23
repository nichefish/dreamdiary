<#--
 * 내 정보(user_my) > 내 비밀번호 변경 Modal
 * @included_in : user_my_dtl
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="user_my_pw_chg" size="md">
    <#--begin::Modal Header-->
    <@component.modal_header_dark "비밀번호 변경" />
    <#--begin::Modal Body-->
    <div class="modal-body">
        <form name="myPwChgForm" id="myPwChgForm" class="form">
            <#--begin::Row-->
            <div class="row mb-4">
                <div class="col-xl-3">
                    <div class="col-form-label text-center fs-6 fw-bold">
                        <label for="currPw" class="required">현재 비밀번호</label>
                    </div>
                </div>
                <div class="col-xl-9 text-start">
                    <input type="password" name="currPw" id="currPw" class="form-control required" maxlength="20">
                    <div id="currPw_valid_span"></div>
                </div>
            </div>
            <#--begin::Row-->
            <div class="row mb-4">
                <div class="col-xl-3">
                    <div class="col-form-label text-center fs-6 fw-bold">
                        <label for="newPw" class="required">새 비밀번호</label>
                    </div>
                </div>
                <div class="col-xl-9 text-start">
                    <input type="password" name="newPw" id="newPw" class="form-control required" maxlength="20">
                    <div class="fs-8 text-noti form-text text-noti">(※특수문자, 숫자, 영문 포함 9자리 이상)</div>
                    <div id="newPw_valid_span"></div>
                </div>
            </div>
            <#--begin::Row-->
            <div class="row mb-4">
                <div class="col-xl-3">
                    <div class="col-form-label text-center fs-6 fw-bold">
                        <label for="newPwCf" class="required">새 비밀번호<br/> 확인</label>
                    </div>
                </div>
                <div class="col-xl-9 text-start">
                    <input type="password" name="newPwCf" id="newPwCf" class="form-control required" maxlength="20">
                    <div id="newPwCf_valid_span"></div>
                </div>
            </div>
        </form>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--버튼:: 저장하기-->
            <@component.modal_btn_save "UserMyPwChg.submit();" />
            <#--버튼:: 모달 닫기-->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<#--include:: Handlebars template-->
<#include "/view/user/my/_user_my_pw_chg_template.hbs">