<!--
 -- 레이아웃 > 탑바
 -- @included in : header/_header_base
 -->
<!--begin::Toolbar wrapper-->
<style>
    .profl-img {
        object-fit: cover;
    }
</style>

<div class="d-flex align-items-stretch flex-shrink-0">
    <div class="d-flex align-items-center flex-shrink-0 justify-content-start ps-10">
        TODO: put something here
    </div>
</div>
<div class="d-flex align-items-stretch flex-shrink-0 justify-content-between">
    <#include "/layout/topbar/_topbar_notion.ftlh">
    <#include "/layout/topbar/_topbar_flsys.ftlh">

    <!--begin::Separator-->
    <span class="my-3 border-gray-300 border-start ms-3 mx-2"></span>
    <!--end::Separator-->

    <!-- topbar icons -->
    <#include "/layout/topbar/_topbar_test_page.ftlh">
    <#include "/layout/topbar/_topbar_tag_cloud.ftlh">
    <#include "/layout/topbar/_topbar_sitemap.ftlh">
    <#include "/layout/topbar/_topbar_search.ftlh">

    <!--begin::Separator-->
    <span class="my-3 border-gray-300 border-start ms-3 mx-2"></span>
    <!--end::Separator-->

    <#if isMngr>
        <!--begin::사용자/관리자 모드 선택-->
        <div class="position-relative w-md-115px me-md-2 ms-2 my-5"
             data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click" title="사용자/관리자 권한별로&#10;화면을 전환합니다.">
            <select name="userMode" id="userMode" class="form-select form-select-solid fs-small fw-bolder form-select-sm opacity-hover">
                <option value="${(Constant.AUTH_USER)!}" <#if !isMngrMode>selected</#if>>사용자 모드</option>
                <option value="${(Constant.AUTH_MNGR)!}" <#if isMngrMode>selected</#if>>관리자 모드</option>
            </select>
        </div>
        <!--end::관리자 선택-->
    </#if>

    <!--begin::Users-->
    <div class="d-flex align-items-center ms-1 mt-1">
        <!--begin::Menu wrapper&ndash;&gt;-->
        <div class="btn btn-icon btn-active-light-primary position-relative w-30px h-30px w-md-40px h-md-40px" id="kt_drawer_chat_toggle"
             data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click"
             title="내 정보 팝업을 호출합니다.&#10;&#10;현재 접속 계정:&#10;${(authInfo.nickNm)!}(${(authInfo.userId)!})">
            <!--begin::Svg Icon | path: icons/duotune/general/gen022.svg&ndash;&gt;-->
            <#if (authInfo.proflImgUrl)??>
                <img src="${authInfo.proflImgUrl!}" class="img-thumbnail p-0 w-100" />
            <#else>
                <span class="svg-icon svg-icon-1">
                    <i class="fas fa-user-circle fs-2"></i>
                </span>
            </#if>
            <!--end::Svg Icon&ndash;&gt;-->
        </div>
        <!--end::Menu wrapper&ndash;&gt;-->
    </div>
    <!--end::Users-->
</div>
<!--end::Toolbar wrapper-->