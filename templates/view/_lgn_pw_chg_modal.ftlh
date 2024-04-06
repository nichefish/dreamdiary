<#--
  -- 로그인 화면 > 비밀번호 변경 Modal
  -- @included_in : lgn_form
  -->
<div class="modal fade" tabindex="-1" id="lgn_pw_chg_modal" data-bs-keyboard="false" data-bs-backdrop="static">
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
                <form name="lgnPwChgForm" id="lgnPwChgForm" class="form">
                    <div id="lgn_pw_chg_div">
                        <!-- handlebars template goes in here -->
                    </div>
                </form>
            </div>

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
        </div>
    </div>
</div>