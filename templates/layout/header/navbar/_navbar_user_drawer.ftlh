<!--
 * 레이아웃 > 탑바 > 사용자 정보 Drawer
 * @included_in : layout
 -->

<#--begin::user drawer-->
<div id="kt_drawer_chat" class="bg-body"
     data-kt-drawer="true"
     data-kt-drawer-name="chat"
     data-kt-drawer-activate="true"
     data-kt-drawer-overlay="true"
     data-kt-drawer-width="{default:'250px', 'md': '400px'}"
     data-kt-drawer-direction="end"
     data-kt-drawer-toggle="#kt_drawer_chat_toggle, #kt_drawer_chat_toggle2"
     data-kt-drawer-close="#kt_drawer_chat_close">

    <!--begin::Messenger-->
    <div class="card w-500px rounded-0" id="kt_drawer_chat_messenger">
        <#--begin::Card header-->
        <div class="card-header pe-5 bg-primary" id="kt_drawer_chat_messenger_header">
            <#--begin::title-->
            <div class="card-title">
                <div class="btn btn-icon btn-primary position-relative w-30px h-30px w-md-40px h-md-40px">
                    <#if (authInfo.proflImgUrl)??>
                        <img src="${authInfo.proflImgUrl!}" class="img-thumbnail p-0 w-100" onerror="this.src='/metronic/assets/media/avatars/avatar_blank.png';" />
                    <#else>
                        <span class="svg-icon svg-icon-1">
                            <i class="fas fa-user-circle fs-1 text-white"></i>
                        </span>
                    </#if>
                </div>
                <span class="text-white fw-bold ps-4 fs-4">내 정보</span>
                <#--end::Svg Icon-->
            </div>
            <!--end::Title-->
            <!--begin::Card toolbar-->
            <div class="card-toolbar">
                <#--begin::Close-->
                <div class="btn btn-sm btn-icon btn-active-light-dark btn-color-white" id="kt_drawer_chat_close">
                    <#--begin::Svg Icon | path: icons/duotune/arrows/arr061.svg-->
                    <span class="svg-icon svg-icon-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <rect x="6" y="17.3137" width="16" height="2" rx="1" transform="rotate(-45 6 17.3137)" fill="black" />
                            <rect x="7.41422" y="6" width="16" height="2" rx="1" transform="rotate(45 7.41422 6)" fill="black" />
                        </svg>
                    </span>
                    <#--end::Svg Icon-->
                </div>
                <#--end::Close-->
            </div>
            <!--end::Card toolbar-->
        </div>
        <!--end::Card header-->
        <#--begin::Card body-->
        <div class="card-body" id="kt_drawer_chat_messenger_body">
            <!--begin::내 정보-->
            <div class="d-flex align-items-center">
                <span class="h2 pt-2" style="padding-right: 10px;">${(authInfo.nickNm)!}</span>
                <button type="button" class="badge  badge-outlined badge-light-primary px-3 py-2"
                        onclick="Header.myInfoDtl();"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="내 정보 화면으로 이동합니다.">
                    <i class="fas fa-user-circle fs-8 pe-1 text-white"></i>
                    내 정보
                </button>
            </div>
            <table class="table table-striped my-7 fs-7 fs-6">
                <tr>
                    <td class="text-gray-700 w-110px fw-bolder ps-4">아이디</td>
                    <td class="text-gray-600">${(authInfo.userId)!}</td>
                </tr>
                <tr>
                    <td class="text-gray-700 w-110px fw-bolder ps-4">현재 IP 주소</td>
                    <td class="text-gray-600">${acsIp!}</td>
                </tr>
                <tr>
                    <td class="text-gray-700 w-110px fw-bolder ps-4">계정 권한</td>
                    <td class="text-gray-600">
                        <div class="d-flex align-items-center">
                            <#if isMngr>
                                <i class="bi bi-person-lines-fill fs-3 text-info mx-1 opacity-75"
                                   data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click" title="관리자"></i>   <!-- 관리자 -->
                                관리자
                            <#else>
                                <i class="bi bi-people-fill fs-3 mx-1"
                                   data-bs-toggle="tooltip" data-bs-placement="bottom" data-bs-dismiss="click" title="사용자"></i>   <!-- 사용자 -->
                                사용자
                            </#if>
                            <#if ((authInfo.authCd)! == Constant.AUTH_MNGR)>
                                <i class="bi bi-person-lines-fill text-info opacity-75"></i>   <!-- 관리자 -->
                            <#elseif ((authInfo.authCd)! == Constant.AUTH_USER)>
                                <i class="bi bi-people-fill ms-1"></i>        <!-- 사용자 -->
                            </#if>
                        </div>
                    </td>
                </tr>
            </table>
            <!--end::게시글 정보-->
            <div class="row">
                <a href="javascript:void(0)" class="btn btn-light btn-light-primary fs-7 mt-2 w-100"
                   onclick="Header.lgout();"
                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="로그아웃합니다.">
                    <#--begin::Svg Icon | path: assets/media/icons/duotune/arrows/arr076.svg-->
                    <span class="svg-icon svg-icon-muted svg-icon-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <rect opacity="0.7" width="12" height="2" rx="1" transform="matrix(-1 0 0 1 15.5 11)" fill="black"/>
                            <path d="M13.6313 11.6927L11.8756 10.2297C11.4054 9.83785 11.3732 9.12683 11.806 8.69401C12.1957 8.3043 12.8216 8.28591 13.2336 8.65206L16.1592 11.2526C16.6067 11.6504 16.6067 12.3496 16.1592 12.7474L13.2336 15.3479C12.8216 15.7141 12.1957 15.6957 11.806 15.306C11.3732 14.8732 11.4054 14.1621 11.8756 13.7703L13.6313 12.3073C13.8232 12.1474 13.8232 11.8526 13.6313 11.6927Z" fill="black"/>
                            <path d="M8 5V6C8 6.55228 8.44772 7 9 7C9.55228 7 10 6.55228 10 6C10 5.44772 10.4477 5 11 5H18C18.5523 5 19 5.44772 19 6V18C19 18.5523 18.5523 19 18 19H11C10.4477 19 10 18.5523 10 18C10 17.4477 9.55228 17 9 17C8.44772 17 8 17.4477 8 18V19C8 20.1046 8.89543 21 10 21H19C20.1046 21 21 20.1046 21 19V5C21 3.89543 20.1046 3 19 3H10C8.89543 3 8 3.89543 8 5Z" fill="#C4C4C4"/>
                        </svg>
                    </span>
                    <#--end::Svg Icon-->
                    <span class="fw-bolder" style="letter-spacing :0.6px;">로그아웃</span>
                </a>
            </div>
        </div>
        <!--end::Card body-->
    </div>
    <!--end::Messenger-->
</div>
<#--end::user drawer-->