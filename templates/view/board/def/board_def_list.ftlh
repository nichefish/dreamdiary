<#--
  -- 게시판 정의 > 목록 화면
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
                commons.util.fnSortTable();
            },
            /** 등록 모달 호출 */
            regForm: function() {
                commons.util.handlebarsTemplate({}, "board_def_reg", "show");
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
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
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <form name="procForm" id="procForm">
        <input type="hidden" name="postNo" id="postNo" value=""/>
        <input type="hidden" name="boardCd" id="boardCd" value="${boardCd!}"/>
    </form>
    <#include "/view/board/def/board_def_list_header.ftlh">
    <div class="mb-5 fs-5 fw-bold">
        <span class="opacity-75 me-5 cursor-help"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
              title="사용자 포탈에 표시되는&#10;'일반게시판' 대메뉴 번호입니다.&#10;게시판 메뉴번호 작성시&#10;해당 번호 아래에 둘 것을&#10;권장합니다.">
            ※ 현재 게시판 상위메뉴번호 : </span>
        <span class="text-noti ls-1 text-decoration-underline">${SiteMenu.TOP_MENU_NO_BOARD_POST!}</span>
    </div>
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed mb-3">
        <!--begin::Table head-->
        <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 text-muted">
                <th class="col-1 text-center wb-keepall w-5 hidden-table">번호</th>
                <th class="col-lg-1 col-6 text-center wb-keepall">게시판 코드</th>
                <th class="col-lg-2 col-2 text-center wb-keepall">게시판 이름</th>
                <th class="col-2 text-center wb-keepall w-15">글분류코드</th>
                <th class="col-1 text-center wb-keepall w-10">메뉴번호</th>
                <th class="col-1 text-center wb-keepall w-10">정렬 순서</th>
                <th class="col-1 text-center wb-keepall w-10">글 갯수</th>
                <th class="col-1 text-center wb-keepall w-5">사용여부</th>
                <th class="col-1 text-center wb-keepall w-5">-</th>
            </tr>
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody>
        <#list boardDefMngList![] as boardDef>
            <tr>
                <td class="text-center hidden-table">
                    ${boardDef.rnum!}
                </td>
                <td class="text-start ps-10">
                    <b class="cursor-help"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                       title="해당 게시판에 등록된 게시물이 있을 경우, 게시판 코드 변경은 권장되지 않습니다.">
                        ${boardDef.boardCd!}
                    </b>
                </td>
                <td class="text-start ps-15">
                    ${boardDef.boardNm!}
                    <a class="badge badge-secondary ms-2 p-2 btn-primary blank blink-slowcard mt-4"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                       title="항목 수정 팝업을 호출합니다.">
                        <i class="bi bi-pencil-square fs-5 text-noti" onclick="Page.mdfItemForm('${boardDef.boardCd!}', 'boardNm', '게시판 이름', '${boardDef.boardNm!}');"></i>
                    </a>
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
                        <i class="bi bi-pencil-square fs-5 text-noti" onclick="Page.mdfItemForm('${boardDef.boardCd!}', 'menuNo', '메뉴번호', '${boardDef.menuNo!}');"></i>
                    </a>
                </td>
                <td class="text-center hidden-table">
                    ${boardDef.sortOrdr!}
                    <a class="badge badge-secondary ms-2 p-2 btn-primary blank blink-slowcard mt-4"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="항목 수정 팝업을 호출합니다.">
                        <i class="bi bi-pencil-square fs-5 text-noti" onclick="Page.mdfItemForm('${boardDef.boardCd!}', 'sortOrdr', '정렬 순서', '${boardDef.sortOrdr!}');"></i>
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
</div>

<#include "/view/board/def/board_def_mdf_item_modal.ftlh">
<#include "/view/board/def/board_def_mdf_item_template.ftlh">
<#include "/view/board/def/board_def_reg_modal.ftlh">
<#include "/view/board/def/board_def_reg_template.ftlh">

</@layout.layout>

