<#--
  -- 사용자 관리 > 계정 및 권한 관리 > 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >
<#import "/spring.ftl" as spring />

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                /** 검색인자 세팅 */
                <#if searchParamMap?has_content >
                    <#list searchParamMap?keys as key>
                        $("#${key!}").val("${searchParamMap[key]!}");
                    </#list>
                </#if>

                // 모든 table 헤더에 클릭 이벤트를 설정한다.
                Page.sortMode = "FORWARD";
                commons.util.sortTable();
            },
            /** 목록 검색 */
            search: function() {
                $("#pageNo").val("");
                commons.util.blockUISubmit("#listForm", "${SiteUrl.USER_LIST!}?actionTyCd=SEARCH");
            },
            fnCrdtUserList: function() {

            },
            fnCrdtUserList: function() {

            },
            /** 등록 화면 이동 */
            regForm: function() {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.USER_REG_FORM!}");
            },
            /** 상세 화면 이동 */
            dtl: function(userNo) {
                $("#procForm #userNo").val(userNo);
                commons.util.blockUISubmit("#procForm", "${SiteUrl.USER_DTL!}");
            },
            /** 엑셀 다운로드 */
            fnXlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${SiteUrl.USER_LIST_XLSX_DOWNLOAD!}").submit();
                });
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <form name="procForm" id="procForm">
        <input type="hidden" name="userNo" id="userNo" />
    </form>
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/user/info/user_list_header.ftlh">
    <!--begin::Tab -->
    <ul class="nav nav-tabs nav-tabs-line ps-5 my-5">
        <li class="nav-item">
            <a class="nav-link px-6 active" data-bs-toggle="tab" href="#kt_tab_pane_1" onclick="Page.fnCrdtUserList();">
                <span class="nav-icon">
                    <i class="bi bi-sort-down-alt"></i>
                </span>
                <span class="nav-text">재직 인원</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link px-6" data-bs-toggle="tab" href="#kt_tab_pane_2" onclick="Page.fnNonCrdtUserList();">
                <span class="nav-icon">
                    <i class="bi bi-people"></i>
                </span>
                <span class="nav-text">비재직 인원</span>
            </a>
        </li>
    </ul>
    <!--end: Tab-->
    <!--end::Card header-->
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <!--begin::Table head-->
        <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                <th class="text-center wb-keepall w-10 hidden-table">번호</th>
                <th class="col-2 text-center wb-keepall">이름 (아이디)</th>
                <th class="col-1 text-center wb-keepall hidden-table">소속</th>
                <th class="col-1 text-center wb-keepall hidden-table">팀</th>
                <th class="col-1 text-center wb-keepall">직급</th>
                <th class="col-3 text-center wb-keepall hidden-table">E-MAIL</th>
                <th class="text-center wb-keepall hidden-table">권한</th>
                <th class="text-center wb-keepall hidden-table">잠금여부</th>
            </tr>
        </thead>
        <!--begin::Table body-->
        <tbody>
            <#list userList![] as user>
                <!-- 계정신청 상태인 계정에 대해 구분 간격 표시 -->
                <#assign divideCfYn = prevCfYn?? && user.cfYn! != prevCfYn>
                <#assign divideRetireYn = prevRetireYn?? && user.retireYn! != prevRetireYn>
                <#assign divide = divideCfYn || divideRetireYn>
                <tr class="<#if user.isMe!false>bg-light</#if> bg-hover-secondary <#if divide>border-top-5</#if>" onclick="Page.dtl(${user.userNo!});">
                    <td class="text-center hidden-table">
                        ${user.rnum}
                    </td>
                    <td class="text-start ps-8">
                        <a href="javascript:void(0);" class="text-dark">
                            <#if (user.proflImgUrl)??>
                                <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                    <img src="${(user.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                                </div>
                            </#if>
                            <span class="pe-1">${user.userNm!"-"} (${user.userId!"-"})</span>
                        </a>
                        <#if (user.userProflYn!"Y") == "N">
                            <i class="bi bi-person-dash vertical-middle text-muted"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="직원정보가 없습니다.&#10;(접속은 가능하지만&#10;일부 기능이 제한됩니다.)"></i>
                        <#elseif (user.retireYn!"N") == "Y">
                            <i class="bi bi-person-dash vertical-middle text-danger"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="퇴사한 인원입니다.&#10;(접속이 불가능합니다.)"></i>
                        <#elseif (user.reqstYn!"N") == "Y" && (user.cfYn!"N") == "N">
                            <i class="bi bi-person-plus vertical-middle text-muted blink"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="신규 신청 계정입니다.&#10;(승인 처리 이후&#10;접속 가능합니다.)"></i>
                        <#else>
                            <i class="bi bi-person-check vertical-middle text-success"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="소속된 직원입니다."></i>
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        ${(user.cmpyNm)!"-"}
                    </td>
                    <td class="text-center hidden-table">
                        ${(user.teamNm)!"-"}
                    </td>
                    <td class="text-center">
                        ${(user.jobTitleNm)!"-"}
                        <#if (user.jobTitleNm)! == "사원" && user.apntcYn! == "Y"><span class="text-muted">(수습)</span></#if>
                    </td>
                    <td class="text-start ps-8 hidden-table">
                        ${(user.email)!"-"}
                    </td>
                    <td class="text-center hidden-table">
                        ${user.authNm!"-"}
                        <#if (user.authCd! == Constant.AUTH_MNGR)>
                            <i class="bi bi-person-lines-fill text-info ms-1 opacity-75"></i>   <!-- 관리자 -->
                        <#elseif (user.authCd! == Constant.AUTH_USER)>
                            <i class="bi bi-people-fill ms-1"></i>        <!-- 사용자 -->
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        <#if !user.isCf!true>
                            <span class="text-muted">
                                미승인<i class="bi bi-dash fs-9 text-muted blink"></i>
                            </span>
                        <#elseif user.isLocked!false>
                            <span class="text-danger">
                                잠김<i class="bi bi-lock fs-9 text-danger"></i>
                            </span>
                        <#else>
                            <span class="text-success">
                                사용<i class="bi bi-check text-success"></i>
                            </span>
                        </#if>
                    </td>
                </tr>
                <!-- 계정 승인여부 상태 저장 -->
                <#assign prevRetireYn = user.retireYn! />
                <#assign prevCfYn = user.cfYn! />
            <#else>
                <tr>
                    <td colspan="7" class="text-center">
                        <@spring.message "view.list.empty"/>
                    </td>
                </tr>
            </#list>
            <!--end::Table row-->
        </tbody>
    </table>
    <!-- pagination::begin -->
    <#include "/view/_component/_pagination.ftlh">
    <!-- pagination::end -->
</div>

</@layout.layout>