<#--
  -- 사용자 관리 > 계정 및 권한 관리 > 등록 화면
  -->
<#import "/layout/layout_without_aside.ftlh" as layout >

<@layout.layout_without_aside>

<#assign isReg = (isReg!false)>
<#assign isMdf = (isMdf!false)>

<script>
    const Page = function() {
        return {
            init: function () {
                commons.validate.noSpaces(".no-space");        // 공백 자동 제거
                commons.validate.toLowerCase("#userId");       // 자동 대문자->소문자처리

                <#if isMdf>
                    commons.util.chckboxLabel("lockedYn", "잠김//해제", "red//gray");  // 계정잠금여부 클릭시 글씨 변경
                </#if>

                // 권한 유효성검사 바꿀때마다 지우기
                $("#authCd").on("change", function() {
                    if ($(this).val() !== "") $("#authCdSpan").text("");
                });

                <#if isReg>
                    // 등록화면:: 사용자 ID 변경입력시 중복체크 통과여부 초기화
                    $("#userId").on("keyup", function() {
                        $("#userIdSpan").empty();
                        $("#isIdDupChckValid").val("N");
                        $("#idDupChckBtn").addClass("blink").removeClass("btn-success").addClass("btn-secondary").removeAttr("disabled");
                    }).on("keydown", function() {
                        $("#userIdSpan").text("");
                        $("#isIdDupChckValid").val("N");
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
                const acsIpInput = document.querySelector("#acsIpListStr");
                <#if isMdf>
                    <#if user.acsIpList?has_content>
                        let tagifyStr = "";
                        <#list user.acsIpList![] as acsIpInfo>
                            tagifyStr += "${acsIpInfo.acsIp},";
                        </#list>
                        tagifyStr.replace(/,$/, '');     // 마지막에 쉼표가 있을 경우 제거
                        acsIpInput.value = tagifyStr;
                    </#if>
                </#if>
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

                /** jquery :: validation */
                $(function(){
                    $("#userReqstForm").validate({
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
                            authCd: { required:true },
                            nickNm: { required:function() {return $("#userProflYn").val() === "N"}, maxlength:20 },
                            <#if isReg>
                                userId: { required:true, minlength:4, maxlength:16 },
                                isIdDupChckValid: { dupChck:true },
                                password: { required:true, minlength:9, maxlength:20, regex:commons.validate.pwRegex },
                                passwordCf: { equalTo:"#password", maxlength:20 },
                            </#if>
                            "userProfl.userNm": { required:function() {return $("#userProflYn").val() === "Y"}, maxlength:20 },
                            "userProfl.cmpyCd": { required:function() {return $("#userProflYn").val() === "Y"} },
                            "userProfl.teamCd": { required:function() {return $("#userProflYn").val() === "Y"} },
                            "userProfl.emplymCd": { required:function() {return $("#userProflYn").val() === "Y"} },
                            "userProfl.cttpc": { required:function() {return $("#userProflYn").val() === "Y"}, minlength:11, maxlength:20 },
                            "userProfl.emailId": { required:function() {return $("#userProflYn").val() === "Y"}, maxlength:20 },
                            "userProfl.emailDomain": { required:function() {return $("#userProflYn").val() === "Y"}, maxlength:20 },
                        },
                        messages: {
                            authCd: { required:"권한은 필수 항목입니다." },
                            nickNm: { required:"표시이름은 필수 항목입니다.", maxlength:"표시이름은 {0}자 이하로 입력해야 합니다." },
                            <#if isReg>
                                userId: { required:"아이디는 필수 항목입니다.", minlength:"아이디는 {0}자 이상으로 입력해야 합니다.", maxlength:"아이디는 {0}자 이하로 입력해야 합니다." },
                                isIdDupChckValid: { dupChck:"아이디 중복 체크는 필수 항목입니다." },
                                password: { required:"비밀번호는 필수 항목입니다.", minlength:"비밀번호는 {0}자 이상으로 입력해야 합니다.", maxlength:"비밀번호는 {0}자 이하로 입력해야 합니다.", regex:"비밀번호가 형식에 맞지 않습니다." },
                                passwordCf: { equalTo:"비밀번호에 입력한 값과 동일하게 입력해야 합니다.", maxlength:"비밀번호 확인은 {}자 이하로 입력해야 합니다." },
                            </#if>
                            "userProfl.userNm": { required:"이름은 필수 항목입니다.", maxlength:"이름은 {0}자 이하로 입력해야 합니다." },
                            "userProfl.cmpyCd": { required:"소속회사는 필수 항목입니다." },
                            "userProfl.teamCd": { required:"소속팀은 필수 항목입니다." },
                            "userProfl.emplymCd": { required:"재직구분은 필수 항목입니다." },
                            "userProfl.cttpc": { required:"전화번호는 필수 항목입니다.", minlength:"전화번호는 {0}자 이상으로 입력해야 합니다.", maxlength:"전화번호는 {0}자 이하로 입력해야 합니다." },
                            "userProfl.emailId": { required:"이메일 ID는 필수 항목입니다.", maxlength:"이메일 ID는 {0}자 이하로 입력해야 합니다." },
                            "userProfl.emailDomain": { required:"이메일 도메인은 필수 항목입니다.", maxlength:"이메일 도메인은 {0}자 이하로 입력해야 합니다." },
                        },
                    });
                });
                $.validator.addMethod("dupChck", function(value) {
                    return (value === "Y");
                });
                $.validator.addMethod("regex", function(value, element, regex) {
                    const regExp = new RegExp(regex);
                    return (this.optional(element) || regExp.test(value));
                });
            },
            /** 아이디 중복 체크(Ajax) */
            userIdDupChckAjax: function() {
                const userIdSpan = $("#userIdSpan");
                let userId = $("#userId").val();
                const idRegex = /^(?=.*[a-z])[a-z\d]{4,16}$/g;
                if (!idRegex.test(userId)) {
                    userIdSpan.text("아이디가 형식에 맞지 않습니다.\n(영문소문자 (+숫자 포함 가능) 4~16자)").css("color", "red");
                    return false;
                }
                const url = "${SiteUrl.USER_ID_DUP_CHK_AJAX!}";
                let ajaxData = { "userId": userId };
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    userIdSpan.text(res.message);
                    if (res.result) {
                        userIdSpan.css("color", "blue");
                        $("#isIdDupChckValid").val("Y");
                        $("#isIdDupChckValidSpan").text("");
                        $("#idDupChckBtn").removeClass("blink").addClass("btn-success").removeClass("btn-secondary").attr("disabled", "disabled");
                    } else {
                        userIdSpan.css("color", "red");
                        $("#isIdDupChckValid").val("N");
                    }
                });
            },
            /** 등록/수정 처리(Ajax) */
            regAjax: function() {
                const url = "${SiteUrl.USER_REQST_REG_AJAX!}";
                let ajaxData = new FormData($("#userReqstForm")[0]);
                commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.result) Page.main();
                        });
                });
            },
            /** 패스워드 초기화 */
            pwResetAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.resetPw"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.USER_PW_RESET_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            return: function() {
                Swal.fire({
                    text: "메인 화면으로 이동합니다.\n(현재까지 입력한 내용은 사라집니다.)\n계속하겠습니까?",
                    showCancelButton: true,
                }).then(function(result) {
                    if (result.value) Page.main();
                });
            },
            main: function() {
                commons.util.blockUIReplace("${SiteUrl.MAIN!}");
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>
<style>
    @media (max-width: 768px) {
        #idDupChckBtn{
            font-size:12px;
        }
    }
</style>

<div class="card post" style="margin-top:1rem !important">
    <!--begin::Form-->
    <form name="userReqstForm" id="userReqstForm" class="form" method="post" enctype="multipart/form-data">
        <input type="hidden" name="userNo" value="${user.userNo!}">
        <input type="hidden" name="userProflYn" id="userProflYn" value="<#if (user.userProfl.userProflNo)??>Y<#else>N</#if>">
        <input type="hidden" name="atchFileNo" value="${user.atchFileNo!}">
        <!--begin::Card body-->
        <div class="row">           <!--begin::Row-->
            <div class="col-xl-2">
                <div class="fs-6 fw-bold col-form-label text-lg-center text-sm-end required"><label for="userId">아이디</label></div>
            </div>
            <#if isReg>
                <div class="col-xl-2 col-8">
                    <input type="text" class="form-control form-control-solid no-space" name="userId" id="userId" value="${user.userId!}" maxlength="16" />
                    <div class="form-text">(※영문소문자, 숫자 포함 4~16자)</div>
                </div>
                <div class="col-xl-2 col-4">
                    <button type="button" class="btn btn-sm btn-secondary required blink" id="idDupChckBtn" onclick="Page.userIdDupChckAjax();"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="입력한 아이디에 대한&#10;중복 여부를 확인합니다.">
                        <span class="hidden-table">아이디 </span>중복 확인
                    </button>
                    <input type="hidden" name="isIdDupChckValid" id="isIdDupChckValid" value="N"/>
                </div>
            </#if>
            <#if isMdf>
                <div class="col-xl-2">
                    <input type="hidden" class="form-control form-control-solid" name="userId" id="userId" value="${user.userId!}" maxlength="16" autocomplete="off" />
                    <div class="fs-6 fw-bold col-form-label text-start">${user.userId!}</div>
                </div>
            </#if>
        </div>
        <div class="row mb-4">      <!-- errorMsgSpan -->
            <div class="col-xl-2"></div>
            <div class="col-xl-10">
                <#if isReg>
                    <div id="userIdSpan" class="text-red"></div>
                    <div id="isIdDupChckValidSpan" style="color:red;"></div>
                </#if>
            </div>
        </div>
        <div class="row">           <!--begin::Row-->
            <div class="col-xl-2">
                <div class="fs-6 fw-bold col-form-label text-lg-center text-sm-end  required">
                    <label for="password">비밀번호</label>
                </div>
            </div>
            <#if isReg>
                <div class="col-xl-2">
                    <input type="password" class="form-control form-control-solid no-space" name="password" id="password" autocomplete="off" />
                </div>
                <div class="col-xl-2">
                    <div class="fs-6 fw-bold col-form-label text-sm-start text-lg-end required">
                        <label for="passwordCf">비밀번호 확인</label>
                    </div>
                </div>
                <div class="col-xl-2">
                    <input type="password" class="form-control form-control-solid no-space" name="passwordCf" id="passwordCf" autocomplete="off" />
                </div>
                <div class="col-xl-4">
                </div>
            </#if>
            <#if isMdf>
                <div class="col-xl-2">
                    <div class="fs-6 fw-bold mb-3 text-start">
                        <button type="button" class="btn btn-sm btn-secondary" onclick="Page.pwResetAjax();"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="비밀번호를 초기화합니다.&#10;로그인 정책 관리 메뉴에서&#10;초기화 값을 설정할 수&#10;있습니다.">
                            비밀번호 초기화
                        </button>
                    </div>
                </div>
                <div class="col-xl-2">
                    <div id="pwResetSpan"></div>
                </div>
                <div class="col-xl-6">
                </div>
            </#if>
            <div class="col-xl-2">
            </div>
            <div class="col-xl-10">
                <div class="form-text text-noti">(※특수문자, 숫자, 영문 포함 9자리 이상)</div>
            </div>
        </div>
        <div class="row mb-4">      <!-- errorMsgSpan -->
            <div class="col-xl-2"></div>
            <div class="col-xl-4">
                <div id="passwordSpan"></div>
            </div>
            <div class="col-xl-6">
                <div id="passwordCfSpan"></div>
            </div>
        </div>
        <div class="row">           <!--begin::Row-->
            <div class="col-xl-2">
                <div class="fs-6 fw-bold col-form-label text-lg-center text-sm-end required">
                    <label for="authCd" class="cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="계정 권한을 설정합니다.&#10;계정 신청시 '사용자' 권한으로&#10;고정됩니다.">
                        권한
                    </label>
                </div>
            </div>
            <div class="col-xl-2 col-form-label">
                사용자<i class="bi bi-people-fill ms-1 blink-slow"></i>
                <input type="hidden" name="authCd" id="authCd" class="form-select form-select-solid" value="${Constant.AUTH_USER!}">
            </div>
        </div>
        <div class="row mb-4">      <!-- errorMsgSpan -->
            <div class="col-xl-2"></div>
            <div class="col-xl-10">
                <div id="authCdSpan"></div>
            </div>
        </div>
        <!-- 가리고 숨기고를 반복하는 곳은 style로 선언해야 한다. -->
        <div class="row" id="nickNmShowDiv" <#if user.userProfl??>style="display:none;"</#if>>           <!--begin::Row-->
            <div class="col-xl-2">
                <div class="fs-6 fw-bold col-form-label text-lg-center text-sm-start required"
                     data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="(외부사용자, 임시계정 등)&#10;직원정보 미입력시&#10;사용할 이름입니다.&#10;(직원정보 입력시 미사용)">
                    <label for="nickNm" class="cursor-help">표시이름</label>
                </div>
            </div>
            <div class="col-xl-2">
                <input type="text" class="form-control form-control-solid" name="nickNm" id="nickNm" value="${user.nickNm!}" maxlength="20" />
                <div class="form-text">(※한글/영문 최대 20자)</div>
            </div>
        </div>
        <div class="row mb-4">      <!-- errorMsgSpan -->
            <div class="col-xl-2"></div>
            <div class="col-xl-10">
                <span id="nickNmSpan"></span>
            </div>
        </div>
        <#if isMdf>
            <div class="row mb-4">      <!--begin::Row-->
                <div class="col-xl-2">
                    <div class="fs-6 fw-bold col-form-label text-lg-center text-sm-start">
                        <label class="cursor-help"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="잠금 여부를 설정합니다.&#10;(잠금 상태인 계정은&#10;접속할 수 없습니다.)">
                            잠금여부
                        </label>
                    </div>
                </div>
                <div class="col-xl-9">
                    <div class="form-check form-switch form-check-custom form-check-solid mt-2">
                        <input class="form-check-input" type="checkbox" value="Y" name="lockedYn" id="lockedYn" <#if user.isLocked!false>checked="checked"</#if> />
                        <label class="form-check-label fw-bold ms-3" for="lockedYn" id="lockedYnLabel" style="color:<#if user.isLocked!false>red<#else>gray</#if>;"><#if user.isLocked!false>잠김<#else>해제</#if></label>
                    </div>
                </div>
            </div>
        </#if>
        <div class="row">           <!--begin::Row-->
            <div class="col-xl-2">
                <div class="fs-6 fw-bold col-form-label text-lg-center text-sm-start ">
                    <label for="acsIpListStr" class="cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="특정 IP에서만&#10;접속 가능하도록 설정합니다.">
                        접속 IP
                    </label>
                </div>
            </div>
            <div class="col-xl-2">
                <div class="form-check form-switch form-check-custom form-check-solid mt-2">
                    <input class="form-check-input" type="checkbox" value="Y" name="useAcsIpYn" id="useAcsIpYn" <#if user.isAcsIpY!false>checked="checked"</#if> />
                    <label class="form-check-label fw-bold ms-3" for="useAcsIpYn" id="useAcsIpYnLabel" style="color:<#if user.isAcsIpY!false>blue<#else>gray</#if>;"><#if !user.isAcsIpY!false>미</#if>사용</label>
                </div>
            </div>
        </div>
        <div class="row">           <!--begin::Row-->
            <div class="col-xl-2"></div>
            <div class="col-xl-6">
                <!-- 가리고 숨기고를 반복하는 곳은 style로 선언해야 한다. -->
                <div id="acsIpListSpan" class="mb-0" style="display:none;">
                    <input name="acsIpListStr" id="acsIpListStr" class="form-control form-control-solid no-space" autocomplete="off"/>
                    <div class="form-text">(※최대 5개)</div>
                </div>
            </div>
        </div>
        <div class="row mb-4">      <!-- errorMsgSpan -->
            <div class="col-xl-2"></div>
            <div class="col-xl-10">
                <span id="acsIpListStrSpan"></span>
            </div>
        </div>
        <div class="row">           <!--begin::Row-->
            <div class="col-xl-2">
                <div class="fs-6 fw-bold col-form-label text-sm-start text-lg-center">
                    <label for="userDc" class="cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="비고 사항을 입력합니다.">
                        계정 설명
                    </label>
                </div>
            </div>
            <div class="col-xl-9">
                <textarea name="userDc" id="userDc" class="form-control form-control-solid h-100px" wrap="hard" maxlength="500">${user.userDc!}</textarea>
            </div>
        </div>

        <#include "/view/user/info/user_info_reg_template.ftlh">    <!-- 직원정보 부분 파일 분리 -->

        <div class="separator my-6"></div>

        <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-primary me-2" onclick="commons.util.submit('#userReqstForm');"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                <i class="bi bi-pencil-square"></i>저장
            </button>
            <button type="button" class="btn btn-light" onclick="Page.return();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="메인 화면으로 돌아갑니다.">
                <i class="bi bi-backspace"></i>돌아가기
            </button>
        </div>
    </form>
    <!--end:Form-->

    <form name="procForm" id="procForm">
        <input type="hidden" name="userNo" value="${user.userNo!}">
        <input type="hidden" name="proflImgUrl" value="${user.proflImgUrl!}">
        <#if user.userProfl??>
            <input type="hidden" name="userProflNo" value="${user.userProfl.userProflNo!}">
        </#if>
    </form>
</div>

<#include "/view/user/info/user_info_reg_item_template.ftlh">       <!-- 기타정보 추가추가 영역 template-->
<!-- #include:: component:: 첨부파일 추가 영역 template-->
<#include "/view/_component/atch_file/_atch_file_area.ftlh">

</@layout.layout_without_aside>