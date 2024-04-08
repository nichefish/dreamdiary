<#--
  -- 게시판 정의 > 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<#-- 툴바 버튼 추가 -->
<#assign toolbar>
    <#include "/view/board/def/_board_def_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/draggable/draggable.bundle.js?${releaseDate!}"></script>

<script>
    const Page = function() {
        return {
            init: function () {
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
                        const $newLi = $("li#"+id);
                        const $newParent = $newLi.parents("li.sortable-item");
                        const newParentId = $newParent.closest("li.sortable-item").attr('id');
                        $newLi.addClass("border-noti border-dotted border-1 blink");
                        $newLi.attr('upper-menu-no', newParentId);
                        $newLi.removeClass('dragging');
                    }, 0); // 지연 시간을 0으로 설정하여 다음 이벤트 루프에서 실행되도록 함
                });
            },
            /** 등록 모달 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "board_def_reg", "show");
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
                commons.validate.validateForm("#boardDefRegForm");
                commons.validate.replaceBlankRegex("#boardDefRegForm .cddata");
                commons.validate.onlyEngNum("#boardDefRegForm .cddata");
                // commons.validate.toUpperCase("#boardDefRegForm .cddata");
                commons.validate.onlyNum(".number");
            },
            /** 등록 (ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${SiteUrl.BOARD_DEF_REG_AJAX!}';
                    let ajaxData = $("#boardDefRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 수정 모달 호출 */
            mdfItemForm: function(boardCd, mdfItem, mdfItemNm, mdfItemVal) {
                event.stopPropagation();
                // 태그 상세 정보
                let data = { "boardCd": boardCd, "mdfItem": mdfItem, "mdfItemNm": mdfItemNm, "mdfItemVal": mdfItemVal };
                commons.util.handlebarsTemplate(data, "board_def_mdf_item", "show");
            },
            /** 항목 수정 (Ajax) */
            mdfItemAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.mdf"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${SiteUrl.BOARD_DEF_MDF_ITEM_AJAX!}';
                    let ajaxData = $("#boardDefMdfItemForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 사용으로 변경 (Ajax) */
            useAjax: function(boardCd) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.use"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${SiteUrl.BOARD_DEF_USE_AJAX!}';
                    let ajaxData = { "boardCd": boardCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 미사용으로 변경 (Ajax) */
            unuseAjax: function(boardCd) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.unuse"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = '${SiteUrl.BOARD_DEF_UNUSE_AJAX!}';
                    let ajaxData = { "boardCd": boardCd };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 삭제 처리 (ajax) */
            delAjax: function(boardCd) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.BOARD_DEF_DEL_AJAX!}";
                    let ajaxData = { "boardCd": boardCd };
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
                    orderData.push({ "boardCd": $(item).attr("id"), "state": { "sortOrdr": index }});
                });
                const url = "${SiteUrl.BOARD_DEF_SORT_ORDR_AJAX!}";
                let ajaxData = { "sortOrdr": orderData };
                console.log(ajaxData);
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
    <div class="mb-5 fs-5 fw-bold">
        <span class="opacity-75 me-5 cursor-help"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
              title="사용자 포탈에 표시되는&#10;'일반게시판' 대메뉴 번호입니다.&#10;게시판 메뉴 번호 작성시&#10;해당 번호 아래에 둘 것을&#10;권장합니다.">
            ※ 현재 게시판 상위메뉴 번호 : </span>
        <span class="text-noti ls-1 text-decoration-underline">${SiteMenu.BOARD.menuNo!}</span>
    </div>
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed mb-3">
        <!--begin::Table head-->
        <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 text-muted">
                <th class="col-1 text-center wb-keepall w-5 hidden-table">번호</th>
                <th class="col-lg-2 col-2 text-center wb-keepall">게시판 이름</th>
                <th class="col-lg-1 col-6 text-center wb-keepall">게시판 코드</th>
                <th class="col-2 text-center wb-keepall w-15">글분류코드</th>
                <th class="col-1 text-center wb-keepall w-10">메뉴 번호</th>
                <th class="col-1 text-center wb-keepall w-10">글 갯수</th>
                <th class="col-1 text-center wb-keepall w-5">사용 여부</th>
                <th class="col-1 text-center wb-keepall w-5">-</th>
            </tr>
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody class="draggable-zone">
        <#list boardDefMngList![] as boardDef>
            <tr class="sortable-item draggable" id="${boardDef.boardCd!}">
                <td class="text-center hidden-table">
                    <div class="draggable-handle cursor-move pt-2"
                         data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="드래그하여&#10;항목을 이동합니다.">
                        <i class="ki-duotone ki-abstract-14 fs-2x">
                            <span class="path1"></span>
                            <span class="path2"></span>
                        </i>
                    </div>
                </td>
                <td class="text-start ps-15">
                    <b>${boardDef.boardNm!}</b>
                    <a class="badge badge-secondary ms-2 p-2 btn-primary blank blink-slowcard mt-4"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                       title="항목 수정 팝업을 호출합니다.">
                        <i class="bi bi-pencil-square fs-5 text-noti" onclick="Page.mdfItemForm('${boardDef.boardCd!}', 'boardNm', '게시판 이름', '${boardDef.boardNm!}');"></i>
                    </a>
                </td>
                <td class="text-start ps-15">
                    <b class="cursor-help"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                       title="해당 게시판에 등록된 게시물이 있을 경우, 게시판 코드는 변경 불가능합니다.">
                        ${boardDef.boardCd!}
                    </b>
                </td>
                <td class="text-start ps-10 hidden-table">
                    ${boardDef.ctgrClCd!}
                    <a class="badge badge-secondary ms-2 p-2 btn-primary blank blink-slowcard mt-4"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="항목 수정 팝업을 호출합니다.">
                        <i class="bi bi-pencil-square fs-5 text-noti" onclick="Page.mdfItemForm('${boardDef.boardCd!}', 'ctgrClCd', '글분류 코드', '${boardDef.ctgrClCd!}');"></i>
                    </a>
                </td>
                <td class="text-start hidden-table">
                    ${boardDef.menuNo!}
                    <a class="badge badge-secondary ms-2 p-2 btn-primary blank blink-slowcard mt-4"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="항목 수정 팝업을 호출합니다.">
                        <i class="bi bi-pencil-square fs-5 text-noti" onclick="Page.mdfItemForm('${boardDef.boardCd!}', 'menuNo', '메뉴 번호', '${boardDef.menuNo!}');"></i>
                    </a>
                </td>
                <td class="text-center hidden-table">
                    -
                </td>
                <td class="text-center hidden-table">
                    <#if (boardDef.useYn!"Y") == "Y">
                        <div class="badge badge-success cursor-pointer" onclick="Page.unuseAjax('${boardDef.boardCd!}');"
                             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="클릭하여 사용 상태를&#10;변경합니다.">
                            <i class="bi bi-check text-light blink-slow "></i>사용
                        </div>
                    <#else>
                        <div class="badge badge-secondary cursor-pointer" onclick="Page.useAjax('${boardDef.boardCd!}');"
                             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="클릭하여 사용 상태를&#10;변경합니다.">
                            <i class="bi bi-x blink-slow "></i>미사용
                        </div>
                    </#if>
                </td>
                <td class="text-center hidden-table">
                    <div class="badge badge-danger badge-outlined cursor-pointer" onclick="Page.delAjax('${boardDef.boardCd!}');"
                         data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.del"/>">
                        <i class="bi bi-trash text-danger"></i>삭제
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

<div class="card post">
    <div class="mb-5 fs-5 fw-bold">
        <span class="opacity-75 me-5 cursor-help"
              data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="사용이 금지된&#10;게시판 코드입니다.">
            ※ 예약 게시판 코드 목록:
        </span>
        <span class="text-noti fs-7">
            일반게시판과 기본 구조를 공유하는 다른 엔티티에서 이미 점유 중인 게시판 코드입니다. (중복하여 사용할 경우 데이터 참조가 오작동합니다.)
        </span>
    </div>
    <table class="table align-middle table-row-dashed fs-small gy-3 table-fixed mb-3">
        <!--begin::Table head-->
        <thead>
        <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 text-muted">
            <th class="col-lg-2 col-3 text-start ps-15 wb-keepall">예약 코드</th>
            <th class="col-lg-10 col-9 text-start ps-15 wb-keepall">사용 중인 엔티티</th>
        </tr>
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody>
        <#list POST_CD![] as postCd>
            <tr>
                <td class="text-start ps-15">
                    <b class="border-bottom">${postCd.dtlCd!}</b>
                </td>
                <td class="text-start ps-15">
                    ${postCd.dtlCdNm!}
                </td>
            </tr>
        </#list>
        <!--end::Table row-->
        </tbody>
    </table>

    <form name="procForm" id="procForm">
        <input type="hidden" name="postNo" id="postNo" value=""/>
        <input type="hidden" name="boardCd" id="boardCd" value="${boardCd!}"/>
    </form>
</div>

<#include "/view/board/def/_board_def_mdf_item_modal.ftlh">
<#include "/view/board/def/_board_def_reg_modal.ftlh">

</@layout.layout>

