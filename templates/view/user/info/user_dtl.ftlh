<#--
  -- 사용자 관리 > 계정 및 권한 관리 > 상세 화면
  -->
<#import "/layout/layout.ftlh" as layout >
<#import "/spring.ftl" as spring />

<@layout.layout>

<script>
    const Page = function() {
        return {
            /** 수정 화면 이동 */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.USER_MDF_FORM!}");
            },
            /** 승인 처리 (ajax) */
            cfAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.cf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.USER_REQST_CF_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 승인취소 처리 (ajax) */
            uncfAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.uncf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.USER_REQST_UNCF_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.USER_DEL_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) Page.list();
                            });
                    });
                });
            },
            /** 목록 화면 이동 */
            list: function() {
                commons.util.blockUIReplace("${SiteUrl.USER_LIST!}?isBackToList=Y");
            },
        }
    }();
</script>

<form name="procForm" id="procForm">
    <input type="hidden" name="userNo" value="${user.userNo!}">
    <#if user.userInfo??>
        <input type="hidden" name="userProflNo" value="${user.userInfo.userProflNo!}">
    </#if>
</form>

<div class="card post">
    <!--begin::Card body-->
    <div class="card-body p-9">
        <!--begin::Row-->
        <div class="row mb-1">
            <div class="col-xl-2 col-4 d-flex-center">
                <label class="fs-6 fw-bold">승인여부</label>
            </div>
            <div class="col-xl-1 col-2 d-flex align-items-center">
                <#if user.isCf!true>
                    <span class="text-success">
                        사용<i class="bi bi-check text-success"></i>
                    </span>
                <#else>
                    <span class="text-muted">
                        미승인<i class="bi bi-dash fs-9 text-muted blink"></i>
                    </span>
                </#if>
            </div>
            <div class="col-xl-2 col-8 col-form-label">
                <#if user.isCf!true>
                    <div class="btn btn-sm btn-danger" onclick="Page.uncfAjax();">
                        승인 취소
                    </div>
                <#else>
                    <div class="btn btn-sm btn-secondary" onclick="Page.cfAjax();">
                        승인 처리
                    </div>
                </#if>
            </div>
        </div>

        <!--begin::Menu separator-->
        <div class="separator my-8"></div>
        <!--end::Menu separator-->

        <div class="row mb-1">      <!--begin::Row-->
            <div class="col-xl-2 col-4 d-flex-center">
                <label class="fs-6 fw-bold">아이디</label>
            </div>
            <div class="col-xl-2 col-8 col-form-label">
                <div class="btn btn-icon btn-secondary position-relative w-30px h-30px w-md-40px h-md-40px me-3">
                    <#if user.proflImgUrl??>
                        <img src="${user.proflImgUrl!}" class="img-thumbnail p-0 w-100" />
                    <#else>
                        <span class="svg-icon svg-icon-1">
                            <i class="fas fa-user-circle fs-2"></i>
                        </span>
                    </#if>
                </div>
                ${user.userId!}
            </div>
            <div class="col-xl-2 col-4">
                <div class="col-form-label text-center fs-6 fw-bold">등록자</div>
            </div>
            <div class="col-xl-6 col-8 pc-d-flex col-form-label">
                <div class="col-xl-2 col-12">${user.regstrId!}</div>
                <div class="col-xl-6 col-12"> (${user.regDt!})</div>
            </div>
        </div>
        <div class="row mb-4">      <!--begin::Row-->
            <div class="col-xl-2 col-4">
                <div class="col-form-label text-center fs-6 fw-bold"><label for="authNm">권한</label></div>
            </div>
            <div class="col-xl-2 col-8 col-form-label">
                ${user.authNm!}
                <#if (user.authCd! == Constant.AUTH_MNGR)>
                    <i class="bi bi-person-lines-fill text-info ms-1 opacity-75"></i>   <!-- 관리자 -->
                <#elseif (user.authCd! == Constant.AUTH_USER)>
                    <i class="bi bi-people-fill ms-1"></i>        <!-- 사용자 -->
                </#if>
            </div>
        </div>
        <div class="row mb-4">      <!--begin::Row-->
            <div class="col-xl-2 col-4">
                <div class="col-form-label text-center fs-6 fw-bold">
                    <label for="nickNm">이름</label>
                </div>
            </div>
            <div class="col-xl-2 col-8 col-form-label">
                ${user.nickNm!}
            </div>
        </div>
        <div class="row mb-4">      <!--begin::Row-->
            <div class="col-xl-2 col-4">
                <div class="col-form-label text-center fs-6 fw-bold">잠금여부</div>
            </div>
            <div class="col-xl-9 col-8 col-form-label">
                <#if user.isLocked!false>
                    <div class="text-danger">
                        잠김<i class="bi bi-lock fs-9 text-danger"></i>
                    </div>
                <#else>
                    <div class="text-success">
                        사용<i class="bi bi-check text-success"></i>
                    </div>
                </#if>
            </div>
        </div>
        <div class="row mb-4">      <!--begin::Row-->
            <div class="col-xl-2 col-4 col-form-label">
                <div class="text-center fs-6 fw-bold ">
                    <label for="acsIpListStr">접속IP</label>
                </div>
            </div>
            <div class="col-xl-2 col-4 d-flex align-items-center">
                <div class="form-check form-switch form-check-custom form-check-solid">
                    <span class="me-8">
                        <#if user.isAcsIpY!false>사용<#else>미사용</#if>
                    </span>
                </div>
            </div>
            <div class="col-xl-6 col-4 col-form-label">
                <div class="form-check form-switch form-check-custom form-check-solid">
                    <#if user.isAcsIpY && user.acsIpList??>
                        <#list user.acsIpList![] as item>
                            <span class="div-textarea div-height-1 me-4">${item.acsIp}</span>
                        </#list>
                    </#if>
                </div>
            </div>
        </div>
        <div class="row">           <!--begin::Row-->
            <div class="col-xl-2 col-4">
                <div class="col-form-label text-center fs-6 fw-bold">계정 설명</div>
            </div>
            <div class="col-xl-9 col-8 col-form-label">
                <div class="div-textarea h-auto min-h-125px">${user.userDc!}</div>
            </div>
        </div>
    </div>

    <#if user.userInfo??>
        <!--begin::Menu separator-->
        <div class="separator my-2"></div>
        <!--end::Menu separator-->

        <div class="card-body p-9">
            <div class="row mb-4">      <!--begin::Row-->
                <div class="col-xl-2 col-4">
                    <div class="col-form-label text-center fs-6 fw-bold"><label for="cttpc">부서</label></div>
                </div>
                <div class="col-xl-2 col-8 col-form-label">
                    ${user.userInfo.cmpyNm!"-"} / ${user.userInfo.teamNm!"-"} / ${user.userInfo.emplymNm!"-"}
                </div>
            </div>
            <div class="row mb-4">      <!--begin::Row-->
                <div class="col-xl-2 col-4">
                    <div class="col-form-label text-center fs-6 fw-bold">직급</div>
                </div>
                <div class="col-xl-2 col-8 col-form-label">
                    ${(user.userInfo.jobTitleNm)!"-"}
                    <#if ((user.userInfo.jobTitleCd)! == "STAFF") && ((user.userInfo.apntcYn)! == "Y")>
                        <span class="text-muted">(수습)</span>
                    </#if>
                </div>
            </div>
            <div class="row mb-4">      <!--begin::Row-->
                <div class="col-xl-2 col-4">
                    <div class="col-form-label text-center fs-6 fw-bold">입사일</div>
                </div>
                <div class="col-xl-2 col-8 col-form-label">
                    ${(user.userInfo.ecnyDt)!"-"}
                </div>
                <#if (((user.userInfo.retireYn)!"N") == "Y")!false>
                    <div class="col-xl-2 col-4">
                        <div class="col-form-label text-center fs-6 fw-bold">퇴사일</div>
                    </div>
                    <div class="col-xl-2 col-8 col-form-label">
                        ${user.userInfo.retireDt!"-"}
                    </div>
                </#if>
            </div>
            <div class="row mb-4">      <!--begin::Row-->
                <div class="col-xl-2 col-4">
                    <div class="col-form-label text-center fs-6 fw-bold">전화번호</div>
                </div>
                <div class="col-xl-2 col-8 col-form-label">
                    ${user.userInfo.cttpc!"-"}
                </div>
            </div>
            <div class="row mb-4">      <!--begin::Row-->
                <div class="col-xl-2 col-4">
                    <div class="col-form-label text-center fs-6 fw-bold">E-mail</div>
                </div>
                <div class="col-xl-2 col-8 col-form-label">
                    ${user.userInfo.email!"-"}
                </div>
            </div>
            <div class="row mb-4">      <!--begin::Row-->
                <div class="col-xl-2 col-4">
                    <div class="col-form-label text-center fs-6 fw-bold">생년월일</div>
                </div>
                <div class="col-xl-1 col-4 col-form-label">
                    ${user.userInfo.brthdy!"-"}
                </div>
                <div class="col-xl-2 col-4 col-form-label">
                    <#if (user.userInfo.lunarYn!"N") == "Y">(음력)</#if>
                </div>
            </div>
            <div class="row mb-4">      <!--begin::Row-->
                <div class="col-xl-2 col-4">
                    <div class="col-form-label text-center fs-6 fw-bold">계좌번호</div>
                </div>
                <div class="col-8 col-form-label">
                    ${user.userInfo.acntBank!"-"} <span class="px-3">|</span> ${user.userInfo.acntNo!"-"}
                </div>
            </div>
            <#if user.userInfo.itemList?has_content>
                <div class="row mb-4">           <!--begin::Row-->
                    <div class="col-xl-2 col-4">
                        <div class="col-form-label text-center fs-6 fw-bold">추가정보</div>
                    </div>
                    <div class="col-xl-9 col-12 col-form-label" id="userItemListDiv">
                        <#if user.userInfo?? && user.userInfo.itemList?has_content>
                            <#list user.userInfo.itemList![] as item>
                                <div class="row mb-2 ps-2 border-bottom border-1 text-muted">
                                    <div class="col-xl-2 col-3 text-start">${item.itemNm}</div>
                                    <div class="col-xl-4 col-4 text-start">${item.itemCn}</div>
                                    <div class="col-xl-4 col-4 text-start">${item.itemDc}</div>
                                </div>
                            </#list>
                        </#if>
                    </div>
                </div>
            </#if>
            <div class="row">           <!--begin::Row-->
                <div class="col-xl-2 col-4">
                    <div class="col-form-label text-center fs-6 fw-bold">사용자 설명</div>
                </div>
                <div class="col-xl-9 col-8 col-form-label">
                    <div class="div-textarea h-auto min-h-125px">${user.userInfo.userInfoDc!}</div>
                </div>
            </div>
        </div>
    </#if>
    <!--end::Card body-->
    <!--begin::Card footer-->
    <div class="card-footer d-flex justify-content-end py-6 px-9">
        <button type="button" class="btn btn-sm btn-light btn-active-primary me-2" onclick="Page.mdfForm();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
            수정
        </button>
        <#if isMngr>
            <button type="button" class="btn btn-sm btn-light btn-active-danger me-2" onclick="Page.delAjax();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.del"/>">
                삭제
            </button>
        </#if>
        <button type="button" class="btn btn-sm btn-light" onclick="Page.list();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.list"/>">
            목록
        </button>
    </div>
    <!--end::Card footer-->
</div>
<!--end::Card-->

</@layout.layout>