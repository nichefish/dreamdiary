<#--
  -- 분류 코드(CL_CD) > 상세 Template
  -- @included_in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="menu_sub_template">
    <div class="list-unstyled" style="{{borderColor}}"
        id="{{menuNo}}" type="{{menuTyCd}}" data-index="{{sortOrdr}}">
        <div class="d-flex align-items-center justify-content-between rounded">
            <div class="d-flex justify-content-between"
                 onclick="Menu.mdfForm('{{menuNo}}');"
                 data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="수정 팝업을 호출합니다.">
                <span class="{{#if isMain}}text-primary fw-bolder fs-6{{else}}menu-title-gray-600{{/if}} cursor-pointer">
                    {{#if (exists icon)}}
                        <i class="{{icon}} fs-3"></i>
                    {{else}}
                        <span class="menu-bullet">
                        <span class="bullet bullet-dot vertical-middle"></span>
                    </span>
                    {{/if}}
                    <span class="ms-1">
                        {{menuNm}}
                    </span>
                </span>
            </div>
            <div class="d-flex align-items-center justify-content-end">
                <div class="w-80px">
                    <div class="btn btn-sm btn-icon btn-light-primary btn-outlined cursor-pointer"
                         onclick="Menu.regModal('${Constant.MENU_TY_SUB!}', '{{menuNo}}', '{{menuNm}}');"
                         data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="하위메뉴 추가 팝업을&#10;호출합니다.">
                        <i class="fas fa-plus"></i>
                    </div>
                    <div class="btn btn-sm btn-icon btn-light-danger cursor-pointer"
                         onclick="Menu.delAjax('{{menuNo}}');"
                         data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="이 메뉴를 삭제합니다.">
                        <i class="bi bi-trash d-flex-center opacity-75"></i>
                    </div>
                </div>
            </div>
        </div>
        <ul id="menu_sub_{{menuNo}}" class="sub-menu-list custom-scroll">
            <!-- sub-menu area -->
        </ul>
    </div>
</script>