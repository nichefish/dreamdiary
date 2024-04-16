<#--
  -- 게시판 정의(board_def) > 목록 화면
  -->

<#import "/layout/layout.ftlh" as layout >

<#-- 툴바 버튼 추가 -->
<#assign toolbar>
    <#include "/view/board/def/_board_def_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/draggable/draggable.bundle.js?${releaseDate!}"></script>

<!--include:: script modules -->
<#include "/view/board/def/_board_def_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            init: function () {
                /* 모든 table 헤더에 클릭 이벤트를 설정한다. */
                commons.util.initSortTable();
                /* init : Draggable */
                BoardDef.initDraggable();
            },
        }
    })();
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
                    <a class="badge badge-secondary ms-2 p-2 btn-primary blank blink-slowcard"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                       title="항목 수정 팝업을 호출합니다.">
                        <i class="bi bi-pencil-square fs-5 text-noti" onclick="BoardDef.mdfItemForm('${boardDef.boardCd!}', 'boardNm', '게시판 이름', '${boardDef.boardNm!}');"></i>
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
                    <a class="badge badge-secondary ms-2 p-2 btn-primary blank blink-slowcard"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="항목 수정 팝업을 호출합니다.">
                        <i class="bi bi-pencil-square fs-5 text-noti" onclick="BoardDef.mdfItemForm('${boardDef.boardCd!}', 'ctgrClCd', '글분류 코드', '${boardDef.ctgrClCd!}');"></i>
                    </a>
                </td>
                <td class="text-start hidden-table">
                    ${boardDef.menuNo!}
                    <a class="badge badge-secondary ms-2 p-2 btn-primary blank blink-slowcard"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="항목 수정 팝업을 호출합니다.">
                        <i class="bi bi-pencil-square fs-5 text-noti" onclick="BoardDef.mdfItemForm('${boardDef.boardCd!}', 'menuNo', '메뉴 번호', '${boardDef.menuNo!}');"></i>
                    </a>
                </td>
                <td class="text-center hidden-table">
                    -
                </td>
                <td class="text-center hidden-table">
                    <#if (boardDef.state.useYn!"Y") == "Y">
                        <div class="badge badge-success cursor-pointer" onclick="BoardDef.unuseAjax('${boardDef.boardCd!}');"
                             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="클릭하여 사용 상태를&#10;변경합니다.">
                            <i class="bi bi-check text-light blink-slow "></i>사용
                        </div>
                    <#else>
                        <div class="badge badge-secondary cursor-pointer" onclick="BoardDef.useAjax('${boardDef.boardCd!}');"
                             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="클릭하여 사용 상태를&#10;변경합니다.">
                            <i class="bi bi-x blink-slow "></i>미사용
                        </div>
                    </#if>
                </td>
                <td class="text-center hidden-table">
                    <div class="badge badge-danger badge-outlined cursor-pointer" onclick="BoardDef.delAjax('${boardDef.boardCd!}');"
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

<form name="procForm" id="procForm">
    <input type="hidden" name="postNo" id="postNo" value=""/>
    <input type="hidden" name="boardCd" id="boardCd" value="${boardCd!}"/>
</form>

<#include "/view/board/def/_board_def_mdf_item_modal.ftlh">
<#include "/view/board/def/_board_def_reg_modal.ftlh">

</@layout.layout>

