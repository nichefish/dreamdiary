<!--
 * 레이아웃 > 헤더 > 네비게이션 바 (base)
 * @included_in : layout/header/_header_base.ftlh
 -->

<#--begin::Navbar-->
<div class="app-navbar flex-shrink-0">
    <#--include::Notion-->
    <#include "/layout/header/navbar/_navbar_btn_notion.ftlh">

    <#--include::Flsys-->
    <#include "/layout/header/navbar/_navbar_btn_flsys.ftlh">

    <span class="my-6 border-gray-300 border-start ms-3 mx-2"></span>

    <#--include::테스트 페이지 -->
    <#include "/layout/header/navbar/_navbar_btn_test_page.ftlh">

    <#--include::태그클라우드 -->
    <#include "/layout/header/navbar/_navbar_btn_tag_cloud.ftlh">

    <#--include::사이트맵 -->
    <#include "/layout/header/navbar/_navbar_btn_sitemap.ftlh">

    <#--include::검색 -->
    <#include "/layout/header/navbar/_navbar_btn_search.ftlh">

    <span class="my-6 border-gray-300 border-start ms-2 me-6"></span>

    <div class="d-flex align-items-center">
        <div class="d-flex align-items-center">
            <#if isMngr>
                <i class="bi bi-person-lines-fill fs-2 text-info ms-1 opacity-75"
                   data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click" title="관리자"></i>   <!-- 관리자 -->
            <#else>
                <i class="bi bi-people-fill fs-2 ms-1"
                   data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click" title="사용자"></i>   <!-- 관리자 -->
            </#if>
        </div>
        <div class="cursor-pointer mt-2" id="kt_drawer_chat_toggle2"
             data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click"
             title="내 정보 팝업을 호출합니다.&#10;&#10;현재 접속 계정:&#10;${(authInfo.nickNm)!}(${(authInfo.userId)!})">
            <div class="w-50px ms-3 text-truncate">${(authInfo.nickNm)!}</div>
            <div class="w-50px text-end ms-3 text-truncate">(${(authInfo.userId)!})</div>
        </div>
    </div>
    <!--begin::Header menu toggle-->
    <div class="app-navbar-item align-items-center mx-2">
        <#if isMngr!>
            <#--begin::사용자/관리자 모드 선택-->
            <div class="position-relative w-md-115px mx-3"
                 data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click" title="사용자/관리자 권한별로&#10;화면을 전환합니다.">
                <select name="userMode" id="userMode" class="form-select form-select-solid fs-small fw-bolder form-select-sm opacity-hover mt-1 ps-3"
                    style="border-color:#86b7fe; outline: 0; box-shadow: 0 0 0 0.25rem rgba(13,110,253,.25);">
                    <option value="${(Constant.AUTH_USER)!}" <#if !isMngrMode>selected</#if>>사용자 모드</option>
                    <option value="${(Constant.AUTH_MNGR)!}" <#if isMngrMode>selected</#if>>관리자 모드</option>
                </select>
            </div>
        </#if>

        <#--begin::Users-->
        <div class="d-flex align-items-center ms-1 mt-1">
            <#--begin::Menu wrapper-->
            <div class="btn btn-icon btn-active-light-primary position-relative w-30px h-30px w-md-40px h-md-40px" id="kt_drawer_chat_toggle"
                 data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click"
                 title="내 정보 팝업을 호출합니다.&#10;&#10;현재 접속 계정:&#10;${(authInfo.nickNm)!}(${(authInfo.userId)!})">
                <#--begin::Svg Icon | path: icons/duotune/general/gen022.svg-->
                <#if (authInfo.proflImgUrl)??>
                    <img src="${authInfo.proflImgUrl!}" class="img-thumbnail p-0 w-100" onerror="this.src='/metronic/assets/media/avatars/avatar_blank.png';" />
                <#else>
                    <span class="svg-icon svg-icon-1">
                        <i class="fas fa-user-circle fs-2"></i>
                    </span>
                </#if>
                <#--end::Svg Icon-->
            </div>
            <#--end::Menu wrapper-->
        </div>
        <#--end::Users-->
    </div>

    <#--end::Header menu toggle-->
</div>
<#--end::Navbar-->