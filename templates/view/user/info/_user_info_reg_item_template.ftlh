<#--
  -- 사용자 관리 > 계정 및 권한 관리 > 등록 > 사용자 정보 기타항목 추가추가 Template
  -- @included_in : user_reg_form
  -->
<div id="userProflItemTemplate" hidden>
    <span id="userInfoItem__INDEX__"></span>
    <input type="hidden" class="form-control form-control-solid" name="itemList[__INDEX__].userInfoItemNo" id="userInfoItemNo__INDEX__" />
    <div class="col-xl-2 col-6">
        <input type="text" class="form-control form-control-solid" name="userInfo.itemList[__INDEX__].itemNm" id="itemNm__INDEX__" placeholder="항목명" maxlength="30">
        <div id="itemNm__INDEX__Span"></div>
    </div>
    <div class="col-xl-4 col-6">
        <input type="text" class="form-control form-control-solid" name="userInfo.itemList[__INDEX__].itemCn" id="itemCn__INDEX__" placeholder="항목 내용" maxlength="30">
        <div id="itemCn__INDEX__Span"></div>
    </div>
    <div class="col-xl-4 col-10 my-2 my-lg-0">
        <input type="text" class="form-control form-control-solid" name="userInfo.itemList[__INDEX__].itemDc" id="itemDc__INDEX__" placeholder="비고" maxlength="30">
        <div id="itemDc__INDEX__Span"></div>
    </div>
    <div class="col-xl-1 col-1 my-3">
        <input type="hidden" class="form-control form-control-solid" name="userInfo.itemList[__INDEX__].delYn" id="delYn__INDEX__" value="N" />
        <a href="javascript:void(0);" class="btn btn-bg-light btn-active-color-primary p-0"onclick="UserInfo.delUserInfoItemSpan(__INDEX__);">
            <span class="svg-icon svg-icon-muted svg-icon-fluid m-0">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                    <rect opacity="0.5" x="7.05025" y="15.5356" width="12" height="2" rx="1" transform="rotate(-45 7.05025 15.5356)" fill="black"/>
                    <rect x="8.46447" y="7.05029" width="12" height="2" rx="1" transform="rotate(45 8.46447 7.05029)" fill="black"/>
                </svg>
            </span>
        </a>
    </div>
</div>