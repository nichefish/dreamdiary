    <#--
  -- 내 정보 > 상세 화면
  -->
<#import "/layout/layout.ftlh" as layout >
<#import "/spring.ftl" as spring />

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                /** jquery :: validation */
                $(function(){
                    $("#myPwChgForm").validate({
                        submitHandler: function() {
                            Swal.fire({
                                text: <@spring.message "view.cnfm.chgPw"/>,
                                showCancelButton: true,
                            }).then(function(result) {
                                if (!result.value) return;
                                Page.myPwChgAjax();
                            });
                        },
                        errorPlacement : function(error, element) {
                            commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                        },
                        rules: {
                            newPw: { regex: commons.validate.pwRegex },
                            newPwCf: { equalTo: "#newPw" }
                        },
                        messages: {
                            newPw: { regex:"변경할 비밀번호가 형식에 맞지 않습니다." },
                            newPwCf: { equalTo:"변경할 비밀번호에 입력한 값과 동일하게 입력해야 합니다." }
                        },
                    });

                    $("#pw_chg_modal").on("hidden.bs.modal", function() {
                        $("#myPwChgForm")[0].reset();
                        $("#currPwSpan").text("");
                        $("#newPwSpan").text("");
                        $("#newPwCfSpan").text("");
                    });
                });
                $.validator.addMethod("regex", function(value, element, regex) {
                    let regExp = new RegExp(regex);
                    return (this.optional(element) || regExp.test(value));
                });
            },
            /**
             * 프로필 이미지 첨부
             */
            uploadProflImg: function() {
                let $fileInput = $("#atchFile0");
                $fileInput.click();
                $fileInput.on("change", function() {
                    if (this.value !== "") {
                        if (!commons.validate.fileSizeChck(this)) return false;      // fileSizeChck
                        if (!commons.validate.fileExtnChck(this, "jpg|jpeg|png")) return false;      // fileExtnChck
                        const url = "${SiteUrl.USER_MY_UPLOAD_PROFL_IMG_AJAX!}";
                        let ajaxData = new FormData($("#profllImgForm")[0]);
                        commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                            if (commons.util.isNotEmpty(res.message)) {
                                Swal.fire({ "text": res.message })
                                    .then(function() {
                                        Swal.fire({ "text": "변경된 프로필은 재접속 이후에 적용됩니다." })
                                            .then(function() {
                                                if (res.result) commons.util.blockUIReload();
                                            });
                                    });
                            }
                        });
                    }
                });
            },
            /**
             * 프로필 이미지 삭제
             */
            removeProflImg: function() {

                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.USER_MY_REMOVE_PROFL_IMG_AJAX!}";
                    commons.util.blockUIFileAjax(url, {}, function(res) {
                        if (commons.util.isNotEmpty(res.message)) {
                            Swal.fire({ "text": res.message })
                                .then(function() {
                                    Swal.fire({ "text": "변경된 프로필은 재접속 이후에 적용됩니다." })
                                        .then(function() {
                                            if (res.result) commons.util.blockUIReload();
                                        });
                                });
                        }
                    });
                });
            },
            /**
             * TODO: 원본 프로필 이미지 조회
             */
            viewOrgnlProflImg: function() {
                //
            },
            /** 내 비밀번호 수정 팝업 호출 */
            myPwChgForm: function() {
                $("#pw_chg_modal").modal("show");
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
            },
            /** 내 비밀번호 수정 처리(Ajax) */
            myPwChgAjax: function() {
                const url = "${SiteUrl.USER_MY_PW_CHG_AJAX!}";
                let ajaxData = { "currPw" : $("#currPw").val(), "newPw" : $("#newPw").val() };
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.result) commons.util.blockUIReload();
                        });
                });
            },
            /** 내 휴가 사용현황 조회 */
            myVcatnList: function() {
                $("#user_my_dtl_vcatn_list").modal("show");
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <form name="procForm" id="procForm">
        <input type="hidden" name="userNo" value="${user.userNo!}">
        <#if user.userInfo??>
            <input type="hidden" name="userProflNo" value="${user.userInfo.userProflNo!}">
        </#if>
    </form>
    <!--begin::Form-->
    <form class="form">
        <input type="hidden" name="userNo" value="${user.userNo!}">
        <div class="card-body p-9">
            <div class="row mb-1">      <!--begin::Row-->
                <div class="col-xl-2 col-4 d-flex-center">
                    <label class="fs-6 fw-bold">아이디</label>
                </div>
                <div class="col-xl-2 col-8 col-form-label">
                    <div class="btn btn-icon btn-light-primary position-relative w-30px h-30px w-md-40px h-md-40px me-3 cursor-default"
<#--                         onclick="Page.viewOrgnlProflImg();"
                         data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="원본 이미지를 조회합니다."-->
                    >
                        <label class="position-absolute top-0 start-100 translate-middle badge badge-sm badge-circle badge-light-primary opacity-hover cursor-pointer"
                               onclick="Page.uploadProflImg();"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="프로필 이미지를 변경합니다.">
                            <i class="bi bi-pen icon-xs text-primary"></i>
                        </label>
                        <span class="position-absolute top-100 start-100 translate-middle badge badge-sm badge-circle badge-light-danger opacity-hover cursor-pointer"
                              onclick="Page.removeProflImg();"
                              data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="프로필 이미지를 삭제합니다.">
                            <i class="bi bi-x icon-xs text-danger"></i>
                        </span>
                        <#if user.proflImgUrl??>
                            <img src="${user.proflImgUrl!}" class="img-thumbnail p-0 w-100" />
                        <#else>
                            <span class="svg-icon svg-icon-1">
                                <i class="fas fa-user-circle fs-2 blink"></i>
                            </span>
                        </#if>
                        <!--end::Svg Icon&ndash;&gt;-->
                    </div>
                    ${user.userId!}
                </div>
            </div>
            <div class="row mb-4">      <!--end::Row-->
                <div class="col-xl-2 col-4">
                    <div class="col-form-label text-center fs-6 fw-bold">비밀번호</div>
                </div>
                <div class="col-xl-2 col-5">
                    <div class="fs-6 fw-bold mt-2">
                        <button type="button" class="btn btn-sm btn-secondary" id="pwChgBtn" onclick="Page.myPwChgForm();"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="비밀번호 변경 팝업을&#10;호출합니다.">
                            비밀번호 변경
                        </button>
                    </div>
                </div>
                <div class="col-xl-2 col-3">
                    <div id="pwResetSpan"></div>
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
                    <div class="col-form-label text-center fs-6 fw-bold"><label for="nickNm">표시이름</label></div>
                </div>
                <div class="col-xl-2 col-8 col-form-label">
                    ${user.nickNm!}
                </div>
            </div>
            <div class="row mb-4">      <!--begin::Row-->
                <div class="col-xl-2 col-4">
                    <div class="col-form-label text-center fs-6 fw-bold">
                        <label for="acsIpListStr">접속IP</label>
                    </div>
                </div>
                <div class="col-xl-9 col-8 col-form-label">
                    <div class="form-check form-switch form-check-custom form-check-solid">
                        <span class="me-8">
                            <#if user.isAcsIpY!false>사용<#else>미사용</#if>
                        </span>
                        <#if user.isAcsIpY!false && user.acsIpList??>
                            <#list user.acsIpList![] as item>
                                <span class="div-textarea div-height-1 me-4">${item.acsIp!}</span>
                            </#list>
                        </#if>
                    </div>
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
                        <div class="col-xl-2 col-4 col-form-label">
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
                    <div class="col-xl-2 col-8 col-form-label">
                        ${user.userInfo.brthdy!"-"}
                    </div>
                </div>
                <div class="row mb-4">      <!--begin::Row-->
                    <div class="col-xl-2 col-4">
                        <div class="col-form-label text-center fs-6 fw-bold">계좌번호</div>
                    </div>
                    <div class="col-xl-8 col-8 col-form-label">
                        ${user.userInfo.acntBank!"-"} <span class="px-3">|</span> ${user.userInfo.acntNo!"-"}
                    </div>
                </div>
                <#if user.userInfo.itemList?has_content>
                    <div class="row mb-4">           <!--begin::Row-->
                        <div class="col-xl-2 col-4">
                            <div class="col-form-label text-center fs-6 fw-bold">추가정보</div>
                        </div>
                        <div class="col-xl-9 col-8 col-form-label" id="userItemListDiv">
                            <#if user.userInfo?? && user.userInfo.itemList?has_content>
                                <#list user.userInfo.itemList![] as item>
                                    <div class="row mb-2 ps-2 border-bottom border-1 text-muted">
                                        <div class="col-xl-2 col-3 text-start">${item.itemNm!}</div>
                                        <div class="col-xl-4 col-4 text-start">${item.itemCn!}</div>
                                        <div class="col-xl-4 col-4 text-start">${item.itemDc!}</div>
                                    </div>
                                </#list>
                            </#if>
                        </div>
                    </div>
                </#if>
            </div>
        </#if>

        <div class="separator my-2"></div>

        <!-- 내 휴가 정보 -->
        <#if (authInfo.hasEcnyDt!false)>
            <div class="card-body p-9">
                <div class="row mb-4">      <!--begin::Row-->
                    <div class="col-xl-3 col-12">
                        <div class="fs-6 fw-bold text-center">
                            ${vcatnYy.statsYy!}년 연차 사용 현황<br />
                            (${vcatnYy.bgnDt!} ~ ${vcatnYy.endDt!})
                        </div>
                    </div>
                    <div class="col-xl-1 col-sm-4 col-form-label"
                         data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="기본연차: ${vcatnStats.bsYryc!0}<#if vcatnStats.isNewbie!false>(+${vcatnStats.newbieYryc!0})</#if>일<#if (vcatnStats.cnwkYryc!0) gt 0>, &#10;근속 추가연차: ${vcatnStats.cnwkYryc!0}일</#if><#if (vcatnStats.prjctYryc!0) gt 0>, &#10;프로젝트 추가연차: ${vcatnStats.prjctYryc!0}일</#if>">
                        <span class="cursor-help" >총연차 : ${vcatnStats.totYryc!0}</span>
                    </div>
                    <div class="col-xl-1 col-sm-4 col-form-label">
                        소진연차 : ${vcatnStats.exhsYryc!0}
                    </div>
                    <div class="col-xl-1 col-sm-4 col-form-label">
                        잔여연차 : ${vcatnStats.remndrYryc!0}
                    </div>
                    <div class="col-xl-2 col-sm-12">
                        <button type="button" class="btn btn-sm btn-primary mx-1" onclick="Page.myVcatnList();"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="내 휴가 사용현황 조회&#10;팝업을 호출합니다.">
                            <i class="fas fa-umbrella-beach"></i>휴가 상세 현황 확인
                        </button>
                    </div>
                </div>
            </div>
        </#if>
        <!--end::Card body-->

        <div class="separator my-2"></div>
    </form>
    <!--end:Form-->
</div>
<!--end::Card-->

<div id="tinymceImageTemplate" hidden>
    <form name="profllImgForm" id="profllImgForm" class="form" enctype="multipart/form-data">
        <input type="file" class="file" name="atchFile0" id="atchFile0" onchange="return false;" accept=".png, .jpg, .jpeg"/>
    </form>
</div>

<#include "/view/user/my/user_my_mdf_pw_chg_modal.ftlh">
<#if (authInfo.hasEcnyDt!false)>
    <#include "/view/user/my/user_my_dtl_vcatn_list.ftlh">
</#if>

</@layout.layout>