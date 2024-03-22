<#--
  -- 로그인 화면 > 비밀번호 변경 Modal
  -- @included in : lgn_form
  -->
<script id="lgn_pw_chg_template" type="text/x-handlebars-template">
    <input type="hidden" name="userId" id="lgnUserId" value="{{userId}}">
    <div class="row">      <!--begin::Row-->
        <div class="col-xl-12 text-red">
            {{errorMsg}}
        </div>
    </div>
    <div class="row mb-5">      <!--begin::Row-->
        <div class="col-xl-3">
            <div class="col-form-label text-center fs-6 fw-bold"><label for="currPw">현재 비밀번호</label></div>
        </div>
        <div class="col-xl-9 text-start">
            <input type="password" name="currPw" id="currPw" class="form-control required" maxlength="20">
            <div id="currPwSpan"></div>
        </div>
    </div>
    <div class="row">           <!--begin::Row-->
        <div class="col-xl-3">
            <div class="col-form-label text-center fs-6 fw-bold"><label for="newPw">새 비밀번호</label></div>
        </div>
        <div class="col-xl-9 text-start">
            <input type="password" name="newPw" id="newPw" class="form-control required" maxlength="20">
            <div class="fs-8 form-text">(※특수문자, 숫자, 영문 포함 9자리 이상)</div>
            <div id="newPwSpan"></div>
        </div>
    </div>
    <div class="row mb-5">      <!--begin::Row-->
        <div class="col-xl-3">
            <div class="col-form-label text-center fs-6 fw-bold"><label for="newPwCf">새 비밀번호 확인</label></div>
        </div>
        <div class="col-xl-9 text-start">
            <input type="password" name="newPwCf" id="newPwCf" class="form-control required" maxlength="20">
            <div id="newPwCfSpan"></div>
        </div>
    </div>
</script>