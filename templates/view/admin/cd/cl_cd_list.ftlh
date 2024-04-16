<#--
 -- 분류 코드(CL_CD) > 목록 화면
 -->
<#import "/layout/layout.ftlh" as layout >

<#-- 툴바 버튼 추가 -->
<#assign toolbar>
    <#include "/view/admin/cd/_cl_cd_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/draggable/draggable.bundle.js?${releaseDate!}"></script>

<!--include:: script modules -->
<#include "/view/admin/cd/_cl_cd_scripts.ftlh">

<script>
    const Page = function() {
        return {
            init: function() {
                commons.util.initSortTable();       // 모든 table 헤더에 클릭 이벤트를 설정한다.

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
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <!--begin::Card Body-->
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <!--begin::Table head-->
        <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                <th class="col-1 text-center wb-keepall w-8 hidden-table">번호</th>
                <th class="col-2 text-center wb-keepall">분류코드</th>
                <th class="col-2 text-center wb-keepall">분류코드명</th>
                <th class="col-4 text-center wb-keepall">분류코드 설명</th>
                <th class="col-1 text-center wb-keepall">상세코드 수</th>
                <th class="col-1 text-center wb-keepall">사용 여부</th>
                <th class="col-1 text-center wb-keepall">-</th>
            </tr>
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody class="draggable-zone">
            <#list clCdList![] as clCd>
                <tr class="sortable-item draggable bg-hover-secondary" id="${clCd.clCd!}" onclick="ClCd.dtl('${clCd.clCd!}');">
                    <td class="text-center hidden-table">
                        <div class="draggable-handle cursor-move pt-2"
                             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="드래그하여&#10;항목을 이동합니다.">
                            <i class="ki-duotone ki-abstract-14 fs-2x">
                                <span class="path1"></span>
                                <span class="path2"></span>
                            </i>
                        </div>
                    </td>
                    <td class="text-start px-6 cursor-pointer">
                        ${clCd.clCd!}
                        <a class="badge badge-secondary ms-2 p-2 btn-white blank blink-slow float-end"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="모달 팝업으로 보기">
                            <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="ClCd.dtlAjax('${clCd.clCd!}');"></i>
                        </a>
                    </td>
                    <td class="text-start ps-6">
                        <span class="border-bottom">
                            ${clCd.clCdNm!}
                        </span>
                    </td>
                    <td class="text-start ps-6 hidden-table">
                        ${clCd.clCdDc!}
                    </td>
                    <td class="text-center hidden-table">
                        ${clCd.dtlCdCnt!0}
                    </td>
                    <td class="text-center hidden-table">
                        <#if (clCd.useYn!"Y") == "Y">
                            <div class="badge badge-success cursor-pointer"
                                 onclick="ClCd.unuseAjax('${clCd.clCd}');"
                                 data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                 title="클릭하여 사용 상태를&#10;변경합니다.">
                                <i class="bi bi-check text-light blink-slow"></i>사용
                            </div>
                        <#else>
                            <div class="badge badge-secondary btn-white cursor-pointer"
                                 onclick="ClCd.useAjax('${clCd.clCd}');"
                                 data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                 title="클릭하여 사용 상태를&#10;변경합니다.">
                                <i class="bi bi-x blink-slow"></i>미사용
                            </div>
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        <div class="badge badge-danger badge-outlined cursor-pointer"
                             onclick="ClCd.delAjax('${clCd.clCd}');"
                             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                             title="<@spring.message "bs.tooltip.del"/>">
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
    <!-- pagination::begin -->
    <#include "/view/_component/_pagination.ftlh">
    <!-- pagination::end -->
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" id="clCd" name="clCd" />
</form>

<!--include:: Modals-->
<#include "/view/admin/cd/_cl_cd_reg_modal.ftlh">
<#include "/view/admin/cd/_cl_cd_dtl_modal.ftlh">

</@layout.layout>