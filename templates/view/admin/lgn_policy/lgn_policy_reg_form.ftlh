<#--
 -- 로그인 정책 관리 화면
 -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = function() {
        return {
            init: function () {
                /** 개별 input 유효성검사 */
                commons.validate.onlyNum(".number");

                /** jquery :: validation */
                $(function(){
                    $("#lgnPolicyForm").validate({
                        submitHandler: function() {
                            Page.regAjax();
                        },
                        errorPlacement : function(error, element) {
                            commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                        },
                        rules: {
                            lgnLockDy: { maxlength:3 },
                            lgnTryLmt: { maxlength:3 },
                            pwChgDy: { maxlength:3 },
                            pwForReset: { minlength:8, maxlength:16 },
                        },
                        messages: {
                            lgnLockDy: { maxlength:"장기간 미로그인 잠금일수는 {0}자 이하로 입력해야 합니다." },
                            lgnTryLmt: { maxlength:"로그인 제한 횟수는 {0}자 이하로 입력해야 합니다." },
                            pwChgDy: { maxlength:"비밀번호 변경 일수는 {0}자 이하로 입력해야 합니다." },
                            pwForReset: { minlength:"비밀번호 초기화 설정값은 {0}자 이상으4로 입력해야 합니다.", maxlength:"비밀번호 초기화 설정값은 {0}자 이상으로 입력해야 합니다." }
                        }
                    });
                });
            },
            /** 등록 또는 수정 처리(Ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.LGN_POLICY_REG_AJAX!}";
                    let ajaxData = $("#lgnPolicyForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReplace("${SiteUrl.LGN_POLICY_FORM!}");
                            });
                    });
                });
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <!--begin::Form-->
    <form name="lgnPolicyForm" id="lgnPolicyForm" class="form">
        <input type="hidden" name="lgnPolicyNo" value="${lgnPolicy.lgnPolicyNo!}">
        <!--begin::Card body-->
        <div class="card-body p-9">
            <div class="row">      <!--begin::Row-->
                <div class="col-xl-2 col-form-label fs-6 fw-bold">
                    <label for="lgnLockDy">장기간 미로그인 잠금일수</label>
                </div>
                <div class="col-xl-1">
                    <input type="text" class="form-control form-control-solid text-end required number" name="lgnLockDy" id="lgnLockDy" value="${lgnPolicy.lgnLockDy!}" maxlength="3"/>
                </div>
                <div class="col-xl-1 col-form-label w-5">
                    일
                </div>
                <div class="col-xl-8 text-noti">
                    ※ 마지막 로그인 이후 해당 일수 동안 미로그인시 자동 계정 잠금<br />
                    (로그인 성공시 카운트 초기화)
                </div>
            </div>
            <div class="row mb-8">      <!-- errorMsgSpan -->
                <div class="col-xl-2"></div>
                <div class="col-xl-10">
                    <div id="lgnLockDySpan"></div>
                </div>
            </div>
            <div class="row">           <!--begin::Row-->
                <div class="col-xl-2 col-form-label fs-6 fw-bold">
                    <label for="lgnTryLmt">로그인 제한 횟수</label>
                </div>
                <div class="col-xl-1">
                    <input type="text" class="form-control form-control-solid text-end required number" name="lgnTryLmt" id="lgnTryLmt" value="${lgnPolicy.lgnTryLmt!}" maxlength="3" />
                </div>
                <div class="col-xl-1 col-form-label w-5">
                    회
                </div>
                <div class="col-xl-8 text-noti">
                    ※ 해당 횟수만큼 연속 로그인 실패시 자동 계정 잠금<br />
                    (로그인 성공시 카운트 초기화)
                </div>
            </div>
            <div class="row mb-8">      <!-- errorMsgSpan -->
                <div class="col-xl-2"></div>
                <div class="col-xl-10">
                    <div id="lgnTryLmtSpan"></div>
                </div>
            </div>
            <div class="row">           <!--begin::Row-->
                <div class="col-xl-2 col-form-label fs-6 fw-bold">
                    <label for="pwChgDy">비밀번호 변경 일수</label>
                </div>
                <div class="col-xl-1">
                    <input type="text" class="form-control form-control-solid text-end required number" name="pwChgDy" id="pwChgDy" value="${lgnPolicy.pwChgDy!}" maxlength="3" />
                </div>
                <div class="col-xl-1 col-form-label w-5">
                    일
                </div>
                <div class="col-xl-8 text-noti">
                    ※ 비밀번호 변경 이후 해당 일수 이후부터 비밀번호 변경해야 로그인 가능<br />
                    (비밀번호 변경시 카운트 초기화)
                </div>
            </div>
            <div class="row mb-8">      <!-- errorMsgSpan -->
                <div class="col-xl-2"></div>
                <div class="col-xl-10">
                    <div id="pwChgDySpan"></div>
                </div>
            </div>
            <div class="row">           <!--begin::Row-->
                <div class="col-xl-2 col-form-label fs-6 fw-bold">
                    <label for="pwForReset">비밀번호 초기화 설정값</label>
                </div>
                <div class="col-xl-2">
                    <input type="password" class="form-control form-control-solid required" name="pwForReset" id="pwForReset" value="${lgnPolicy.pwForReset!}" maxlength="16" />
                </div>
                <div class="col-xl-8 text-noti">
                    ※ 계정 '비밀번호 초기화'시 초기값으로 설정할 비밀번호<br/>
                    ※ 개인에게 초기화 비밀번호 발급시 변경 필수 + 평소 주기적으로 변경 요망
                </div>
            </div>
            <div class="row mb-8">      <!-- errorMsgSpan -->
                <div class="col-xl-2"></div>
                <div class="col-xl-10">
                    <div id="pwForResetSpan"></div>
                </div>
            </div>
            <!--end::Row-->
        </div>
        <!--end::Card body-->
        <!--begin::Card footer-->
        <div class="card-footer d-flex justify-content-end py-6 px-9">
            <button type="button" class="btn btn-primary" id="lgnPolicyMdfBtn"
                    onclick="commons.util.submit('#lgnPolicyForm');"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                <i class="bi bi-save"></i>저장
            </button>
        </div>
        <!--end::Card footer-->
    </form>
</div>
<!--end::Card-->

</@layout.layout>