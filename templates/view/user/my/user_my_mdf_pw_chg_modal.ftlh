<#--
  -- 내 정보 > 내 비밀번호 변경 Modal
  -- @included in : user_my_dtl
  -->
<div class="modal fade" tabindex="-1" id="pw_chg_modal" data-bs-keyboard="false" data-bs-backdrop="static">
    <div class="modal-dialog modal-md modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header" style="background-color:#41416e;">
                <h2 class="modal-title text-light">비밀번호 변경</h2>
                <!--begin::Close-->
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <span class="svg-icon svg-icon-2x">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <rect opacity="0.5" x="6" y="17.3137" width="16" height="2" rx="1" transform="rotate(-45 6 17.3137)" fill="black"></rect>
                            <rect x="7.41422" y="6" width="16" height="2" rx="1" transform="rotate(45 7.41422 6)" fill="#ffffff"></rect>
                        </svg>
                    </span>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body">
                <form name="myPwChgForm" id="myPwChgForm" class="form">
                    <!--begin::Row-->
                    <div class="row mb-5">
                        <div class="col-xl-3">
                            <div class="col-form-label text-center fs-6 fw-bold"><label for="currPw">현재 비밀번호</label></div>
                        </div>
                        <div class="col-xl-9 text-start">
                            <input type="password" name="currPw" id="currPw" class="form-control required" maxlength="20">
                            <div id="currPwSpan"></div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-xl-3">
                            <div class="col-form-label text-center fs-6 fw-bold"><label for="newPw">새 비밀번호</label></div>
                        </div>
                        <div class="col-xl-9 text-start">
                            <input type="password" name="newPw" id="newPw" class="form-control required" maxlength="20">
                            <div class="fs-8 form-text">(※특수문자, 숫자, 영문 포함 9자리 이상)</div>
                            <div id="newPwSpan"></div>
                        </div>
                    </div>
                    <div class="row mb-5">
                        <div class="col-xl-3">
                            <div class="col-form-label text-center fs-6 fw-bold"><label for="newPwCf">새 비밀번호 확인</label></div>
                        </div>
                        <div class="col-xl-9 text-start">
                            <input type="password" name="newPwCf" id="newPwCf" class="form-control required" maxlength="20">
                            <div id="newPwCfSpan"></div>
                        </div>
                    </div>
                    <!--end::Row-->
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary" onclick="submit(document.myPwChgForm);"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                    <i class="bi bi-pencil-square"></i>
                    수정
                </button>
                <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal">닫기</button>
            </div>
        </div>
    </div>
</div>