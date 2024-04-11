<#--
  -- 사용자 관리 > 계정 및 권한 관리 > 등록 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<#assign isReg = (isReg!false)>
<#assign isMdf = (isMdf!false)>

<script>
    const Page = function() {
        return {
            init: function () {
                commons.validate.noSpaces(".no-space");         // 공백 자동 제거
                commons.validate.toLowerCase("#userId");        // 자동 대문자->소문자처리
                commons.validate.cttpc("#cttpc");               // 연락처 포맷

                // 권한 유효성검사 바꿀때마다 지우기
                $("#authCd").on("change", function() {
                    if ($(this).val() !== "") $("#authCd_valid_span").text("");
                });

                <#if isReg>
                    // 등록화면:: 사용자 ID 변경입력시 중복체크 통과여부 초기화
                    $("#userId").on("keyup", function() {
                        $("#userId_valid_span").empty();
                        $("#ipDupChckPassed").val("N");
                        $("#idDupChckBtn").addClass("blink").removeClass("btn-success").addClass("btn-secondary").removeAttr("disabled");
                    }).on("keydown", function() {
                        $("#userId_valid_span").text("");
                        $("#ipDupChckPassed").val("N");
                        $("#idDupChckBtn").addClass("blink").removeClass("btn-success").addClass("btn-secondary").removeAttr("disabled");
                    });
                </#if>

                // 접속IP 사용 여부 클릭시 글씨 변경 + 입력창 토글 :: 메소드 분리
                commons.util.chckboxLabel("useAcsIpYn", "사용//미사용", "blue//gray", function(){
                    $("#acsIpListSpan").show()
                }, function(){
                    $("#acsIpListSpan").hide()
                });

                // 접속IP tagify
                User.tagifyAcsIp();

                /** jquery :: validation */
                $(function(){
                    $("#userRegForm").validate({
                        submitHandler: function() {
                            if ($("#useAcsIpYn").is(":checked") && $("#acsIpListStr").val() === "") {
                                Swal.fire("접속 IP는 최소 한 개 이상 입력해야 합니다.");
                                return false;
                            }
                            Swal.fire({
                                text: <#if isMdf><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                                showCancelButton: true,
                            }).then(function(result) {
                                if (!result.value) return;
                                Page.regAjax();
                            });
                        },
                        errorPlacement : function(error, element) {
                            commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                        },
                        ignore: [],         // hidden 필드도 검증하기 위함
                        rules: {
                            <#if isReg>
                                userId: { required:true, minlength:4, maxlength:16 },
                                ipDupChckPassed: { dupChckValid:true },
                                password: { minlength:9, maxlength:20, regex:commons.validate.pwRegex },
                                passwordCf: { equalTo:"#password", maxlength:20 },
                            </#if>
                            "userProfl.userNm": { required:function() {return $("#userProflYn").val() === "Y"}, maxlength:20 },
                            "userProfl.cmpyCd": { required:function() {return $("#userProflYn").val() === "Y"} },
                            "userProfl.teamCd": { required:function() {return $("#userProflYn").val() === "Y"} },
                            "userInfo.emplymCd": { required:function() {return $("#userProflYn").val() === "Y"} },
                        },
                        messages: {
                            <#if isReg>
                                userId: { required:"아이디는 필수 항목입니다.", minlength:"아이디는 {0}자 이상으로 입력해야 합니다.", maxlength:"아이디는 {0}자 이하로 입력해야 합니다." },
                                ipDupChckPassed: { dupChckValid:"아이디 중복 체크는 필수 항목입니다." },
                                password: { minlength:"비밀번호는 {0}자 이상으로 입력해야 합니다.", maxlength:"비밀번호는 {0}자 이하로 입력해야 합니다.", regex:"비밀번호가 형식에 맞지 않습니다." },
                                passwordCf: { equalTo:"비밀번호에 입력한 값과 동일하게 입력해야 합니다.", maxlength:"비밀번호 확인은 {}자 이하로 입력해야 합니다." },
                            </#if>
                            "userInfo.userNm": { required:"이름은 필수 항목입니다.", maxlength:"이름은 {0}자 이하로 입력해야 합니다." },
                            "userInfo.cmpyCd": { required:"소속회사는 필수 항목입니다." },
                            "userInfo.teamCd": { required:"소속팀은 필수 항목입니다." },
                            "userInfo.emplymCd": { required:"재직구분은 필수 항목입니다." },
                        },
                    });
                });
                $.validator.addMethod("dupChckValid", function(value) {
                    return (value === "Y");
                });
                $.validator.addMethod("regex", function(value, element, regex) {
                    const regExp = new RegExp(regex);
                    return (this.optional(element) || regExp.test(value));
                });
            },
            /** 등록/수정 처리(Ajax) */
            regAjax: function() {
                const url = "<#if isReg>${SiteUrl.USER_REG_AJAX!}</#if><#if isMdf>${SiteUrl.USER_MDF_AJAX!}</#if>";
                let ajaxData = new FormData($("#userRegForm")[0]);
                commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) Page.list();
                        });
                });
            },
            /** 목록 화면 이동 */
            list: function() {
                commons.util.blockUIReplace("${SiteUrl.USER_LIST!}<#if isMdf>?isBackToList=Y</#if>");
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
    });

    const User = function() {
        return {
            /** 아이디 중복 체크(Ajax) */
            tagifyAcsIp: function() {
                // 접속IP tagify
                const acsIpInput = document.querySelector("#acsIpListStr");
                new Tagify(acsIpInput, {
                    maxTags: 5,
                    pattern: commons.validate.ipv4regex,
                    keepInvalidTags: false,
                    skipInvalid: true,
                    duplicates: false,
                    editTags: {
                        clicks: 2,
                        keepInvalid: false      // if after editing, tag is invalid, auto-revert
                    },
                });
            },
            /** 아이디 중복 체크(Ajax) */
            idDupChckAjax: function() {
                const userIdValidSpan = $("#userId_valid_span");
                let userId = $("#userId").val();
                const idRegex = /^(?=.*[a-z])[a-z\d]{4,16}$/g;
                if (!idRegex.test(userId)) {
                    userIdValidSpan.text("아이디가 형식에 맞지 않습니다.").removeClass("text-success").addClass("text-danger");
                    return false;
                }
                const url = "${SiteUrl.USER_ID_DUP_CHK_AJAX!}";
                let ajaxData = { "userId": userId };
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    userIdValidSpan.text(res.message);
                    if (res.rslt) {
                        userIdValidSpan.removeClass("text-danger").addClass("text-success");
                        $("#ipDupChckPassed").val("Y");
                        $("#ipDupChckPassed_valid_span").text("");
                        $("#idDupChckBtn").removeClass("blink").addClass("btn-success").removeClass("btn-secondary").attr("disabled", "disabled");
                    } else {
                        userIdValidSpan.removeClass("text-success").addClass("text-danger");
                        $("#ipDupChckPassed").val("N");
                    }
                });
            },
        }
    }();
</script>
<style>
    @media (max-width: 768px) {
        #idDupChckBtn{
            font-size:12px;
        }
    }
</style>

<div class="card post">
    <!--begin::Tab -->
    <ul class="nav nav-tabs nav-tabs-line ps-5 my-5">
        <li class="nav-item">
            <a class="nav-link px-6 active" data-bs-toggle="tab" href="#kt_tab_pane_1">
                <span class="nav-icon">
                    <i class="bi bi-sort-down-alt"></i>
                </span>
                <span class="nav-text">계정 정보</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_2" onclick="Page.logStatsUserList();">
                <span class="nav-icon">
                    <i class="bi bi-people"></i>
                </span>
                <span class="nav-text">프로필 정보</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_3" onclick="Page.logStatsUserList();">
                <span class="nav-icon">
                    <i class="bi bi-grid-1x2"></i>
                </span>
                <span class="nav-text">직원 인사정보</span>
            </a>
        </li>
    </ul>
    <!--end: Tab-->
    <!--begin::Form-->
    <form name="userRegForm" id="userRegForm" class="form mt-10" method="post" enctype="multipart/form-data">
        <input type="hidden" name="userNo" value="${user.userNo!}">
        <input type="hidden" name="atchFileNo" value="${user.atchFileNo!}">
        <!--begin::Row-->
        <div class="row mb-4">
            <div class="col-xl-2">
                <div class="fw-bold col-form-label text-lg-center text-sm-end <#if isReg>required</#if>">
                    <label for="userId">아이디</label>
                </div>
            </div>
            <#if isReg>
                <div class="col-xl-2 col-8">
                    <input type="text" class="form-control form-control-solid required no-space" name="userId" id="userId" value="${user.userId!}" maxlength="16" />
                    <div class="form-text">(※영문소문자, 숫자 포함 4~16자)</div>
                    <div id="userId_valid_span" class="text-danger"></div>
                    <div id="ipDupChckPassed_valid_span" class="text-danger"></div>
                </div>
                <div class="col-xl-2 col-4">
                    <button type="button" class="btn btn-sm btn-secondary required blink" id="idDupChckBtn"
                            onclick="User.idDupChckAjax();"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="입력한 아이디에 대한&#10;중복 여부를 확인합니다.">
                        <span class="hidden-table">아이디 </span>중복 확인
                    </button>
                    <input type="hidden" name="ipDupChckPassed" id="ipDupChckPassed" value="N"/>
                </div>
            </#if>
            <#if isMdf>
                <div class="col-xl-2">
                    <input type="hidden" class="form-control form-control-solid" name="userId" id="userId" value="${user.userId!}" maxlength="16" autocomplete="off" />
                    <div class="fw-bold col-form-label text-start">${user.userId!}</div>
                </div>
            </#if>
        </div>
        <#if isReg>
            <!--begin::Row-->
            <div class="row mb-4">
                <div class="col-xl-2">
                    <div class="fw-bold col-form-label text-lg-center text-sm-end  required">
                        <label for="password">비밀번호</label>
                    </div>
                </div>
                <div class="col-xl-2">
                    <input type="password" class="form-control form-control-solid required no-space" name="password" id="password" autocomplete="off" />
                    <div class="form-text text-noti">(※특수문자, 숫자, 영문 포함 9자리 이상)</div>
                    <div id="password_valid_span"></div>
                </div>
                <div class="col-xl-2">
                    <div class="fw-bold col-form-label text-sm-start text-lg-end required">
                        <label for="passwordCf">비밀번호 확인</label>
                    </div>
                </div>
                <div class="col-xl-2">
                    <input type="password" class="form-control form-control-solid no-space" name="passwordCf" id="passwordCf" autocomplete="off" />
                    <div id="passwordCf_valid_span"></div>
                </div>
            </div>
        </#if>
        <!--begin::Row-->
        <div class="row mb-4">
            <div class="col-xl-2">
                <div class="fw-bold col-form-label text-lg-center text-sm-end required">
                    <label for="authCd" class="cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="계정 권한을 설정합니다.">
                        권한
                        <i class="bi bi-question-circle"></i>
                    </label>
                </div>
            </div>
            <div class="col-xl-2">
                <select name="authListStr" id="authCd" class="form-select form-select-solid required"
                        data-control="select2" data-allow-clear="true" data-hide-search="true" data-close-on-select="false" multiple="multiple">
                    <#list authRoleList![] as auth>
                        <#assign is_selected = user.authStrList?? && user.authStrList?seq_contains(auth.authCd) />
                        <option value="${auth.authCd!}" <#if is_selected>selected</#if>>${auth.authNm!}</option>
                    </#list>
                </select>
                <div id="authCd_valid_span"></div>
            </div>
        </div>
        <!--begin::Row-->
        <!-- 가리고 숨기고를 반복하는 곳은 style로 선언해야 한다. -->
        <div class="row mb-4" id="nickNmShowDiv" <#if user.userInfo??>style="display:none;"</#if>>
            <div class="col-xl-2">
                <div class="fw-bold col-form-label text-lg-center text-sm-start required"
                     data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="직원정보 미입력시&#10;사용할 이름입니다.&#10;(외부사용자, 임시계정 등)">
                    <label for="nickNm" class="cursor-help">
                        표시이름
                        <i class="bi bi-question-circle"></i>
                    </label>
                </div>
            </div>
            <div class="col-xl-2">
                <input type="text" class="form-control form-control-solid required" name="nickNm" id="nickNm"
                       placeholder="표시이름" maxlength="20" value="${user.nickNm!}" />
                <div class="form-text text-noti">(※한글/영문 최대 20자)</div>
                <span id="nickNm_valid_span"></span>
            </div>
        </div>
        <!--begin::Row-->
        <div class="row mb-4">
            <div class="col-xl-2">
                <div class="fw-bold col-form-label text-sm-start text-lg-center required">
                    <label for="emailId" class="cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="이메일 주소입니다. 알림 및 계정 복구 등에 사용됩니다.">
                        E-mail
                        <i class="bi bi-question-circle"></i>
                    </label>
                </div>
            </div>
            <div class="col-lg-2 col-4">
                <input type="text" class="form-control form-control-solid no-space" name="emailId" id="emailId"
                       maxlength="20" placeholder="이메일 ID 입력" value="${user.emailId!}" />
                <div id="emailId_valid_span"></div>
            </div>
            <div class="col-xl-2 col-1 d-flex-center fw-bold text-gray-700" style="width:2%;">
                <label for="emailDomain"><label for="emailDomainSelect">@</label></label>
            </div>
            <div class="col-lg-2 col-5 vertical-center">
                <input type="text" class="form-control form-control-solid no-space" name="emailDomain" id="emailDomain"
                       maxlength="20" placeholder="직접입력" value="${user.emailDomain!"gmail.com"}" />
                <div id="emailDomain_valid_span"></div>
            </div>
            <div class="col-xl-2 col-1">
                <select name="emailDomainSelect" id="emailDomainSelect" class="form-select form-select-solid">
                    <option value="">직접입력</option>
                    <option value="gmail.com" <#if isReg!false>selected</#if>>gmail.com</option>
                    <option value="naver.com">naver.com</option>
                    <option value="kakao.com">kakao.com</option>
                </select>
            </div>
        </div>
        <!--begin::Row-->
        <div class="row mb-4">
            <div class="col-xl-2">
                <div class="fw-bold col-form-label text-sm-start text-lg-center">
                    <label for="cttpc">
                        전화번호
                    </label>
                </div>
            </div>
            <div class="col-xl-2">
                <input type="text" class="form-control form-control-solid" name="cttpc" id="cttpc"
                       maxlength="20" value="${user.cttpc!}" />
                <div class="form-text text-noti">(※숫자만 입력)</div>
                <span id="cttpc_valid_span"></span>
            </div>
        </div>
        <!--begin::Row-->
        <div class="row mb-4">
            <div class="col-xl-2">
                <div class="fw-bold col-form-label text-lg-center text-sm-start ">
                    <label for="acsIpListStr" class="cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="특정 IP에서만&#10;접속 가능하도록 설정합니다.">
                        접속 IP
                        <i class="bi bi-question-circle"></i>
                    </label>
                </div>
            </div>
            <div class="col-xl-2">
                <div class="form-check form-switch form-check-custom form-check-solid mt-2">
                    <input class="form-check-input" type="checkbox" value="Y" name="useAcsIpYn" id="useAcsIpYn" <#if user.useAcsIp!false>checked="checked"</#if> />
                    <label class="form-check-label fw-bold ms-3" for="useAcsIpYn" id="useAcsIpYnLabel" style="color:<#if user.useAcsIp!false>blue<#else>gray</#if>;">
                        <#if !user.useAcsIp!false>미</#if>사용
                    </label>
                </div>
                <!-- 가리고 숨기고를 반복하는 곳은 style로 선언해야 한다. -->
                <div id="acsIpListSpan" class="mt-2 mb-0" <#if !(user.useAcsIp!false)>style=display:none;"</#if>>
                    <input name="acsIpListStr" id="acsIpListStr" class="form-control form-control-solid no-space" autocomplete="off" value="${user.acsIpListStr!}" />
                    <div class="form-text text-noti">(※최대 5개)</div>
                    <div id="acsIpListStrSpan"></div>
                </div>
            </div>
        </div>
        <!--begin::Row-->
        <div class="row mb-4">
            <div class="col-xl-2">
                <div class="fw-bold col-form-label text-sm-start text-lg-center">
                    <label for="cn" class="cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="계정에 대한 관리용 정보를&#10;입력합니다.&#10;(입력한 내용은 사용자에게&#10;노출되지 않습니다.)">
                        계정 설명
                        <i class="bi bi-question-circle"></i>
                    </label>
                </div>
            </div>
            <div class="col-xl-9">
                <textarea name="cn" id="cn" class="form-control form-control-solid h-100px" wrap="hard" maxlength="4000" placeholder="비고 사항을 입력합니다.">${user.cn!}</textarea>
            </div>
        </div>

        <div class="separator my-8"></div>

        <!-- 사용자 프로필 정보 영역 -->
        <div id="user_profl_div">
            <!-- handlebars template goes in here -->
        </div>

        <!-- 사용자 인사정보 정보 영역 -->
        <div id="user_emplym_div">
            <!-- handlebars template goes in here -->
        </div>

        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-between">
            <div>
                <div class="d-flex gap-2">
                    <div id="user_profl_reg_btn_div">
                        <button type="button" id="userProflBtn" class="btn btn-sm btn-primary blink"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="사용자 프로필 정보를&#10;추가합니다."
                                onclick="UserProfl.enableUserProfl();">
                            프로필 정보 추가+
                        </button>
                    </div>
                    <div>
                        <button type="button" id="userEmplymBtn" class="btn btn-sm btn-primary blink"
                                onclick="UserEmplym.enableUserEmplym();"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="입사인원에 대하여&#10;직원 인사정보를 추가합니다.">
                            직원 인사정보 추가+
                        </button>
                        <div class="text-noti">
                            ※회사 소속인 경우 직원 인사정보를 입력해주세요.
                        </div>
                    </div>
                </div>
            </div>
            <div class="gap-2">
                <button type="button" class="btn btn-primary me-2"
                        onclick="commons.util.submit('#userRegForm');"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                    <i class="bi bi-pencil-square"></i>저장
                </button>
                <!-- 버튼 :: 목록으로 가기 -->
                <@component.btn_post_toList "Page" />
            </div>
        </div>
    </form>
    <!--end:Form-->
</div>
<form name="procForm" id="procForm">
    <input type="hidden" name="userNo" value="${user.userNo!}">
</form>

<#include "/view/user/profl/_user_profl_reg_area.ftlh">       <!-- 사용자 프로필 정보 영역 -->
<#include "/view/user/emplym/_user_emplym_reg_area.ftlh">       <!-- 사용자 인사정보 영역 -->

</@layout.layout>