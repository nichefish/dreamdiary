<#--
  -- 분류 코드(CL_CD) > 상세 Template
  -- @included_in : cl_cd_list
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
                    상위메뉴 번호/이름
                </label>
            </div>
            <div class="col-10 col-form-label">
                {{upperMenuNo}} / {{upperMenuNm}}
            </div>
        </div>
    {{/unless}}
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
            <label for="menuNm" class="col-form-label fw-bold required">
                메뉴 아이콘
            </label>
        </div>
        <div class="col-6 d-flex align-items-center text-center">
            <textarea name="icon" id="icon"
                      class="form-control form-control-solid" rows="2"  maxlength="200" placeholder="아이콘 html을 입력하세요."></textarea>
        </div>
        <div class="col-1 d-flex align-items-center">
            <div class="btn btn-sm btn-icon btn-light-primary" onclick="Menu.refreshIcon();"
                 data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="아이콘을 미리보기합니다.">
                <i class="bi bi-arrow-repeat"></i>
            </div>
        </div>
        <div class="col-1 d-flex align-items-center" id="menu_icon_div">
            <!-- icon preview goes in here -->
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