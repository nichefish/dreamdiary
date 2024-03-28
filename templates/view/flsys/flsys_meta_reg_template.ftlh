<#--
  -- 분류 코드(CL_CD) > 상세 Template
  -- @included in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="flsys_meta_reg_template">
    <input type="hidden" name="filePath" value="{{filePath}}">
    <input type="hidden" name="upperFilePath" value="{{upperFilePath}}">
    <input type="hidden" name="postNo" value="{{postNo}}">
    <input type="hidden" name="boardCd" value="flsysMeta">
    <!--begin::Input group-->
    <div class="row mb-2">
        <div class="col-xl-2 col-2 col-form-label">
            <label class="fs-6 fw-bold mb-2">제목</label>
        </div>
        <div class="col-xl-7 col-6">
            <input type="text" class="form-control form-control-solid required" placeholder="제목" name="postSj" id="postSj" maxlength="120" value="{{postSj}}" />
        </div>
        <div class="col-xl-2 col-2">
        </div>
        <div class="col-xl-10 col-10">
            <span id="postSjSpan"></span>
        </div>
    </div>
    <!--begin::Input group-->
    <div class="row mb-2">
        <div class="col-xl-2 col-2 col-form-label">
            <label class="fs-6 fw-bold mb-2">태그</label>
        </div>
        <div class="col-xl-7 col-6">
            <input type="text" class="form-control form-control-solid" name="tagListStr" id="tagListStr" maxlength="120" />
        </div>
        <div class="col-xl-2 col-2">
        </div>
        <div class="col-xl-10 col-10">
            <span id="tagListStrSpan"></span>
        </div>
    </div>
</script>