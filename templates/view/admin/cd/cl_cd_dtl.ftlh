<#--
 -- 분류 코드(CL_CD) > 상세 화면
 -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/draggable/draggable.bundle.js?${releaseDate!}"></script>

<script>
    const Page = function() {
        return {
            init: function () {
                const containers = document.querySelectorAll(".draggable-zone");
                if (containers.length === 0) return false;
                Page.swappable = new Draggable.Sortable(containers, {
                    draggable: ".draggable",
                    handle: ".draggable .draggable-handle",
                    mirror: {
                        //appendTo: selector,
                        appendTo: "body",
                        constrainDimensions: true
                    },
                }).on('drag:start', (event) => {
                    $(event.data.source).addClass('dragging');
                }).on('drag:stop', (event) => {
                    const id = $(event.data.source).attr("id");
                    setTimeout(() => {
                        const $newTr = $("tr#"+id);
                        $newTr.removeClass('dragging').addClass("draggable-modified");
                    }, 0); // 지연 시간을 0으로 설정하여 다음 이벤트 루프에서 실행되도록 함
                });
            },
            /** 수정 화면 이동 */
            mdfForm: function() {
                const url = "${SiteUrl.CL_CD_DTL_AJAX!}";
                let ajaxData = { "clCd": $("#clCd").val() };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltObj = res.rsltObj;
                    rsltObj.isMdf = true;
                    commons.util.handlebarsTemplate(rsltObj, "cl_cd_reg", "show");
                    commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                    commons.validate.replaceBlankRegex("#clCdRegForm #clCd");
                    $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
                });
            },
            /** 등록 또는 수정 처리(Ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.save"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("#clCdRegForm #regYn").val("N");
                    const url = "${SiteUrl.CL_CD_MDF_AJAX!}";
                    let ajaxData = $("#clCdRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 상세코드 등록 팝업 호출 */
            dtlCdRegModal: function() {
                commons.util.handlebarsTemplate({ "clCd": $("#clCd").val() }, "dtl_cd_reg", "show");
                commons.validate.replaceBlankRegex("#dtlCdRegForm dtlCd");
                commons.validate.toUpperCase("#dtlCdRegForm #dtlCd");
                commons.validate.onlyNum("#dtlCdRegForm sortOrdr");
                commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
            },
            /** 상세코드 등록 (Ajax) */
            dtlCdRegAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("#clCdRegForm #regYn").val("Y");
                    const url = "${SiteUrl.DTL_CD_REG_AJAX!}";
                    let ajaxData = $("#dtlCdRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
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
                    const url = "${SiteUrl.BOARD_DEF_DEL_AJAX!}";
                    let ajaxData = { "clCd": $("#clCd").val() };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) Page.list();
                            });
                    });
                });
            },
            /** 목록 화면 이동 */
            list: function() {
                const listUrl = "${SiteUrl.CL_CD_LIST!}?isBackToList=Y";
                commons.util.blockUIReplace(listUrl);
            },
            /** 수정 화면 이동 */
            dtlCdMdfForm: function(dtlCd) {
                const url = "${SiteUrl.DTL_CD_DTL_AJAX!}";
                let ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltObj = res.rsltObj;
                    rsltObj.isMdf = true;
                    commons.util.handlebarsTemplate(rsltObj, "dtl_cd_reg", "show");
                    commons.validate.replaceBlankRegex("#dtlCdRegForm #dtlCd");
                    commons.validate.toUpperCase("#dtlCdRegForm #dtlCd");
                    commons.validate.onlyNum("#dtlCdRegForm #sortOrdr");
                    commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                    $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
                });
            },
            /** 사용으로 변경 (Ajax) */
            dtlCdUseAjax: function(dtlCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.use"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${SiteUrl.DTL_CD_USE_AJAX!}';
                    let ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 미사용으로 변경 (Ajax) */
            dtlCdUnuseAjax: function(dtlCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.unuse"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${SiteUrl.DTL_CD_UNUSE_AJAX!}';
                    let ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 삭제 처리 (ajax) */
            dtlCdDelAjax: function(dtlCd) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.DTL_CD_DEL_AJAX!}";
                    let ajaxData = { "clCd": $("#clCd").val(), "dtlCd": dtlCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 정렬순서 저장 */
            sortOrdr: function() {
                let orderData = [];
                document.querySelectorAll('.sortable-item').forEach((item, index) => {
                    orderData.push({ "clCd": $("#clCd").val(), "dtlCd": $(item).attr("id"), "state": { "sortOrdr": index }});
                });
                const url = "${SiteUrl.DTL_CD_SORT_ORDR_AJAX!}";
                let ajaxData = { "sortOrdr": orderData };
                commons.util.blockUIJsonAjax(url, 'post', JSON.stringify(ajaxData), function(res) {
                    if (res.result) {
                        if (commons.util.isNotEmpty(res.message)) {
                            Swal.fire({ text: res.message }).then(function() { commons.util.blockUIReload(); });
                        } else {
                            commons.util.blockUIReload();
                        }
                    }
                });
            }
        }
    }();
    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <!--begin::Question-->
    <div class="mb-0">
        <!--begin::Head-->
        <div class="d-flex align-items-center mb-9">
            <!--begin::Title-->
            <div class="col-12">
                <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                    ${clCd.clCd!} (${clCd.clCdNm!})
                </h1>
            </div>
            <!--end::Title-->
        </div>
        <!--end::Head-->
        <!--end::Foot-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
        <!--begin::Question-->
        <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
            ${clCd.clCdDc?no_esc}
        </div>
    </div>

    <div class="separator separator-dashed border-gray-300 my-8"></div>

    <!-- 버튼 영역 -->
    <div class="d-flex justify-content-end">
        <button type="button" class="btn btn-sm btn-light btn-active-primary me-2"
                onclick="Page.mdfForm();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="구분 코드 수정 팝업을&#10;호출합니다.">
            <i class="bi bi-pencil-square"></i>수정
        </button>
        <!-- 등록자 또는 관리자가 삭제 가능 -->
        <button type="button" class="btn btn-sm btn-light btn-active-danger me-2"
                onclick="Page.delAjax();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="구분 코드를 삭제합니다.">
            <i class="bi bi-trash"></i>삭제
        </button>
        <!-- 버튼 :: 목록으로 가기 -->
        <@component.btn_post_toList "Page" />
    </div>

    <div class="separator separator-dashed border-gray-300 my-8"></div>

    <div class="card-header border-0 pt-6">
        <div class="d-flex justify-content-left align-items-center">
            <h5>상세 코드 목록</h5>
        </div>
        <div class="card-title text-end">
            <div class="d-flex align-items-center position-relative my-1">
            </div>
        </div>
        <div class="card-toolbar gap-2">
            <!--begin::Toolbar-->
            <div>
                <button type="button" class="btn btn-sm btn-primary"
                        onclick="Page.dtlCdRegModal();"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="상세 코드 등록 팝업을&#10;호출합니다.">
                    <i class="fas fa-plus"></i>상세코드 추가
                </button>
            </div>
            <div>
                <!-- 버튼:: 정렬순서 -->
                <@component.btn_header_sort_ordr "Page.sortOrdr();" />
            </div>
            <!--end::Toolbar-->
        </div>
    </div>
    <div>
        <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
            <!--begin::Table head-->
            <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                <th class="col-1 text-center wb-keepall w-8">번호</th>
                <th class="col-2 text-center wb-keepall">
                    <span class="cursor-help"
                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                          title="코드를 특정할 때 내부적으로&#10;사용되는 고유 코드입니다.&#10;(등록 후 수정할 수 없습니다.)">
                        상세코드
                    </span>
                </th>
                <th class="col-2 text-center wb-keepall">
                    <span class="cursor-help"
                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                          title="외부적으로 사용되는&#10;코드 이름입니다.">
                        상세 코드명
                    </span>
                </th>
                <th class="col-4 text-center wb-keepall">상세코드 설명</th>
                <th class="col-1 text-center wb-keepall">
                    <span class="cursor-help"
                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                          title="표시될 순서를 입력합니다.&#10;순서가 겹칠 경우&#10;내부 로직에 따라 정렬됩니다.">
                        정렬 순서
                    </span>
                </th>
                <th class="col-1 text-center wb-keepall">
                    <span class="cursor-help"
                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                          title="'사용'으로 체크된 코드만&#10;실제 코드 사용시 조회됩니다.">
                        사용 여부
                    </span>
                </th>
                <th class="text-center wb-keepall w-8">-</th>
            </tr>
            </thead>
            <!--end::Table head-->
            <!--begin::Table body-->
            <tbody class="draggable-zone">
            <#list clCd.dtlCdList![] as dtlCd>
                <tr class="sortable-item draggable bg-hover-secondary" id="${dtlCd.dtlCd!}" onclick="Page.dtlCdMdfForm('${dtlCd.dtlCd!}');">
                    <td class="text-center">
                        <div class="draggable-handle cursor-move pt-2"
                             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="드래그하여&#10;항목을 이동합니다.">
                            <i class="ki-duotone ki-abstract-14 fs-2x">
                                <span class="path1"></span>
                                <span class="path2"></span>
                            </i>
                        </div>
                    </td>
                    <td class="text-start ps-10 cursor-pointer">
                        <span class="fw-bold border-bottom">
                            ${dtlCd.dtlCd!}
                        </span>
                    </td>
                    <td class="text-start ps-10">
                        <span class="border-bottom">
                            ${dtlCd.dtlCdNm!}
                        </span>
                    </td>
                    <td class="text-start ps-10">
                        ${dtlCd.dtlCdDc!}
                    </td>
                    <td class="text-center">
                        ${dtlCd.sortOrdr!}
                    </td>
                    <td class="text-center">
                        <#if (dtlCd.useYn!"Y") == "Y">
                            <div class="badge badge-success cursor-pointer"
                                 onclick="Page.dtlCdUnuseAjax('${dtlCd.dtlCd!}');"
                                 data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="클릭하여 사용 상태를&#10;변경합니다.">
                                <i class="bi bi-check text-light blink-slow"></i>사용
                            </div>
                        <#else>
                            <div class="badge badge-secondary btn-white cursor-pointer"
                                 onclick="Page.dtlCdUseAjax('${dtlCd.dtlCd!}');"
                                 data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="클릭하여 사용 상태를&#10;변경합니다.">
                                <i class="bi bi-x blink-slow"></i>미사용
                            </div>
                        </#if>
                    </td>
                    <td class="text-center">
                        <div class="badge badge-danger badge-outlined cursor-pointer"
                             onclick="Page.dtlCdDelAjax('${dtlCd.dtlCd!}')"
                             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.del"/>">
                            <i class="bi bi-trash text-danger fs-7 blink-slow"></i>삭제
                        </div>
                    </td>
                </tr>
            <#else>
                <tr>
                    <td colspan=8" class="text-center"><@spring.message "view.list.empty"/></td>
                </tr>
            </#list>
            <!--end::Table row-->
            </tbody>
        </table>
    </div>

    <form name="procForm" id="procForm">
        <input type="hidden" name="clCd" id="clCd" value="${clCd.clCd!}" />
    </form>
</div>

<#include "/view/admin/cd/_cl_cd_reg_modal.ftlh">
<#include "/view/admin/cd/_dtl_cd_reg_modal.ftlh">

</@layout.layout>