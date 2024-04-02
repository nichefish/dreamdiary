<#--
  -- 분류 코드(CL_CD) > 상세 Template
  -- @included in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="menu_reg_template">
    <!-- form start -->
    <input type="hidden" name="menuId" id="menuId" value="{{menuId}}" placeholder="menuId">
    <input type="hidden" name="menuTyCd" id="menuTyCd" value="{{menuTyCd}}" placeholder="menuTyCd">
    <input type="hidden" name="upperMenuId" id="upperMenuId" value="{{upperMenuId}}" placeholder="upperMenuId">
    {{#unless (equals menuTyCd "main")}}
        <div class="row mb-3">
            <div class="col-2 text-center">
                <label for="menuNm" class="col-form-label fw-bold required">
                    상위메뉴번호/이름
                </label>
            </div>
            <div class="col-10 col-form-label">
                {{upperMenuNo}} / {{upperMenuNm}}
            </div>
        </div>
    {{/unless}}
    <div class="row mb-3">
        <div class="col-2 text-center">
            <label for="menuNo" class="col-form-label fw-bold required">
                메뉴번호
            </label>
        </div>
        <div class="col-2">
            <input type="text" name="menuNo" id="menuNo" class="form-control d-inline-block number required" maxlength="8" value="{{menuNo}}">
        </div>
        <div class="col-8"></div>
        <div class="col-2"></div>
        <div class="col-2"><span id="menuNoSpan"></span></div>
    </div>
    <div class="row mb-3">
        <div class="col-2 text-center">
            <label for="menuNm" class="col-form-label fw-bold required">
                메뉴 이름
            </label>
        </div>
        <div class="col-6">
            <input type="text" name="menuNm" id="menuNm" class="form-control d-inline-block required" value="{{menuNm}}">
        </div>
        <div class="col-4"></div>
        <div class="col-2"></div>
        <div class="col-6"><span id="menuNmSpan"></span></div>
    </div>
    <div class="row mb-3">
        <div class="col-2 text-center">
            <label for="menuNm" class="col-form-label fw-bold">
                메뉴 아이콘
            </label>
        </div>
        <div class="col-10 d-flex align-items-center text-center">
            <input type="checkbox" name="iconTyCd" id="iconTyCd_Class" class="form-check-input" {{#if false}}checked{{/if}}
                   onclick="">
                <label for="">&lt;i&gt;</label>
            <input type="checkbox" name="iconTyCd" id="iconTyCd_Svg" class="form-check-input" {{#if false}}checked{{/if}}
                   onclick="">
                <label for="">&lt;svg&gt;</label>
            <input type="checkbox" name="iconTyCd" id="iconTyCd_Img" class="form-check-input" {{#if false}}checked{{/if}}
                   onclick="">
                <label for="">&lt;img&gt;</label>
        </div>
        <div class="col-2 text-center">
        </div>
        <div class="col-4">
            <input type="text" name="icon" id="icon" class="form-control d-inline-block" value="{{icon}}">
        </div>
        <div class="col-1 d-flex align-items-center">
            <div class="btn btn-sm btn-icon btn-light-primary" onclick="Menu.refreshIcon();">
                <i class="bi bi-arrow-repeat"></i>
            </div>
        </div>
        <div class="col-4 d-flex align-items-center">
            <div id="menu_icon_div me-3">
                아이콘 미리보기:
            </div>
            <div id="menu_icon_div">
                <!-- icon goes in here -->
            </div>
        </div>
    </div>
    <div class="row mb-3">
        <div class="col-2 text-center">
            <label for="url" class="col-form-label fw-bold">
                URL
            </label>
        </div>
        <div class="col-6">
            <input type="text" name="url" id="url" class="form-control d-inline-block number" value="{{url}}">
        </div>
        <div class="col-4"></div>
        <div class="col-2"></div>
        <div class="col-10"><span id="menuNoSpan"></span></div>
    </div>
    <div class="row mb-2">
        <div class="col-2 text-center">
            <label class="col-form-label font-weight-bold">메뉴 옵션</label>
        </div>
        <div class="col-10">
            <div class="form-check col-form-label">
                <input class="form-check-input" type="checkbox" name="useYn" id="useYn" value="Y">
                <label class="form-check-label" for="useYn">메뉴 숨김</label>
            </div>
        </div>
    </div>
</script>