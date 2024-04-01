<#--
  -- 분류 코드(CL_CD) > 상세 Template
  -- @included in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="menu_main_card_template">
    <div class="col-4 my-4">
        <div class="p-6 bg-white shadow" style="{{borderColor}}">
            <div class="d-flex align-items-center justify-content-between fw-bold"
                 id="{{menuId}}" type="{{menuTyCd}}" data-index="{{sortOrdr}}">
                <div class="d-flex align-items-center">
                    <h2 class="mx-2 mb-0 cursor-pointer" onclick="Menu.mdfForm('{{menuId}}');"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="수정 팝업을 호출합니다.">
                        {{menuNm}}
                        <i class="{{icon}} fs-3 ms-1"></i>
                    </h2>
                </div>
                <div class="d-flex align-items-center justify-content-between">
                    <div class="w-75px text-noti">
                        {{menuNo}}
                    </div>
                    <div class="w-80px">
                        <div class="btn btn-sm btn-icon btn-light-primary btn-outlined cursor-pointer"
                             onclick="Menu.regForm('sub', '{{menuId}}');"
                                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="하위메뉴 추가 팝업을&#10;호출합니다.">
                            <i class="fas fa-plus"></i>
                        </div>
                        <div class="btn btn-sm btn-icon btn-light-danger cursor-pointer" onclick="Menu.delAjax('{{menuId}}');"
                             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="이 메인메뉴를 삭제합니다.">
                            <i class="bi bi-trash d-flex-center opacity-75"></i>
                        </div>
                    </div>
                </div>
            </div>
            <div class="separator separator-dashed border-gray-300 my-8"></div>
            <ul id="menu_sub_{{menuId}}" class="sub-menu-list custom-scroll">
                <!-- sub-menu area -->
            </ul>
        </div>
    </div>
</script>