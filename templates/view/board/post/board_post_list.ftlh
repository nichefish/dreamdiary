<#--
  -- 게시판 > 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                commons.util.initSortTable();       // 모든 table 헤더에 클릭 이벤트를 설정한다.
            },
            /** 목록 검색 */
            search: function () {
                $("#pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${SiteUrl.BOARD_POST_LIST!}?actionTyCd=SEARCH");
            },
            /** 내가 작성한 글 보기 */
            myPapr: function () {
                let boardCd = $("#boardCd").val();
                const url = "${SiteUrl.BOARD_POST_LIST!}";
                let param = "?boardCd="+boardCd+"&searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },
            /** 전체 목록 조회 */
            resetSearch: function() {
                commons.util.blockUIReplace("${SiteUrl.BOARD_POST_LIST!}?boardCd="+$("#boardCd").val());
            },
            /** 등록 화면 이동 */
            regForm: function () {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.BOARD_POST_REG_FORM!}");
            },
            /** 상세 화면 이동 */
            dtl: function (postNo) {
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${SiteUrl.BOARD_POST_DTL!}");
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function (postNo) {
                event.stopPropagation();
                const url = "${SiteUrl.BOARD_POST_DTL_AJAX!}";
                let ajaxData = { "postNo": postNo, "boardCd": $("#boardCd").val() };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.resultObj, "board_post_dtl", "show");
                });
            },
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <#include "/view/board/post/board_post_list_header.ftlh">
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <!--begin::Table head-->
        <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 text-muted">
                <th class="text-center wb-keepall w-10 hidden-table">번호</th>
                <th class="col-lg-8 col-9 text-center wb-keepall">제목</th>
                <th class="col-lg-1 text-center wb-keepall hidden-table">첨부파일</th>
                <th class="col-lg-2 col-3 text-center wb-keepall">최종수정자</th>
                <th class="text-center wb-keepall w-15 hidden-table">최종수정일</th>
                <th class="text-center wb-keepall w-15 hidden-table">조회수</th>
            </tr>
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody>
        <!-- 상단고정목록 -->
        <#list postFxdList![] as post>
            <tr class="bg-fxd-list bg-hover-secondary" onclick="Page.dtl('${post.postNo!}', '${post.notionPageId!}');">
                <td class="text-center hidden-table">
                    -
                </td>
                <td class="text-start title-fxd <#if post.tagStrList??>pb-4</#if>">
                    <div class="row d-flex align-items-center justify-content-between">
                        <div class="col-xl-8 col-12">
                            <a href="${SiteUrl.BOARD_POST_DTL!}?boardCd=${post.boardCd!}&postNo=${post.postNo!}&notionPageId=${post.notionPageId!}"
                               class="title-fxd vertical-middle<#if (((post.imprtcYn)!"N") == "Y")!false> fw-bold</#if>">
                                <#if (post.ctgrNm!"") != "">
                                    <span class="ctgr-span ctgr-imprtc">${post.ctgrNm!}</span>
                                </#if>
                                ${post.title!}
                            </a>
                            <#if (((post.imprtcYn)!"N") == "Y")!false>
                                <span class="fs-4 blink">&#128680;</span>
                            </#if>
                            <!-- 목록 수정권한 표시 -->
                            <@component.mdfable post />
                            <!-- 목록 댓글 표시 -->
                            <@component.list_comment post />
                        </div>
                        <div class="col-xl-4 col-12 d-flex justify-content-end align-items-center">
                            <!-- 목록 태그 표시 -->
                            <@component.list_tag post />
                            <!-- 모달 팝업 표시 -->
                            <a class="badge badge-secondary p-2 btn-white blank blink-slow float-end"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="모달 팝업으로 보기">
                                <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="Page.dtlAjax('${post.postNo!}');"></i>
                            </a>
                        </div>
                    </div>
                </td>
                <td class="text-center col-form-label hidden-table">
                    <!-- 목록 첨부파일 표시 -->
                    <@component.list_managtr post />
                </td>
                <td class="text-center col-form-label">
                    <!-- 목록 최종수정자 표시 -->
                    <@component.list_managtr post />
                </td>
                <td class="text-center hidden-table">
                    <span class="cursor-help"
                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최초 작성일:&#10;${post.regDt!"-"}">
                        ${post.managtDt!}
                    </span>
                </td>
                <td class="text-center hidden-table">
                    ${post.hitCnt!0?string.number}
                </td>
            </tr>
        </#list>
        <#list postList![] as post>
            <tr class="bg-hover-secondary" onclick="Page.dtl('${post.postNo!}', '${post.notionPageId!}');">
                <td class="text-center hidden-table">
                    ${post.rnum!}
                </td>
                <td class="text-start <#if post.tagStrList??>pb-4</#if>">
                    <div class="row d-flex align-items-center justify-content-between align-items-center">
                        <div class="col-xl-8 col-12">
                            <a href="${SiteUrl.BOARD_POST_DTL!}?boardCd=${post.boardCd!}&postNo=${post.postNo!}&notionPageId=${post.notionPageId!}"
                               class="text-dark vertical-middle<#if (((post.imprtcYn)!"N") == "Y")!false> fw-bold text-danger</#if>">
                                <#if (post.ctgrNm!"") != "">
                                    <span class="ctgr-span ctgr-gray">${post.ctgrNm!}</span>
                                </#if>
                                ${post.title!}
                            </a>
                            <#if (((post.imprtcYn)!"N") == "Y")!false>
                                <span class="fs-4 blink">&#128680;</span>
                            </#if>
                            <!-- 목록 수정권한 표시 -->
                            <@component.mdfable post />
                            <!-- 목록 댓글 표시 -->
                            <@component.list_comment post />
                        </div>
                        <div class="col-xl-4 col-12 d-flex justify-content-end align-items-center">
                            <!-- 목록 태그 표시 -->
                            <@component.list_tag post />
                            <!-- 모달 팝업 표시 -->
                            <a class="badge badge-secondary p-2 btn-white blank blink-slow float-end"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="모달 팝업으로 보기">
                                <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="Page.dtlAjax('${post.postNo!}');"></i>
                            </a>
                        </div>
                    </div>
                </td>
                <td class="text-center col-form-label hidden-table">
                    <!-- 목록 첨부파일 표시 -->
                    <@component.list_managtr post />
                </td>
                <td class="text-center col-form-label">
                    <!-- 목록 최종수정자 표시 -->
                    <@component.list_managtr post />
                </td>
                <td class="text-center hidden-table">
                    <span class="cursor-help"
                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최초 작성일:&#10;${post.regDt!"-"}">
                        ${post.managtDt!}
                    </span>
                </td>
                <td class="text-center hidden-table">
                    ${post.hitCnt!0?string.number}
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

    <form name="procForm" id="procForm">
        <input type="hidden" name="postNo" id="postNo" value=""/>
        <input type="hidden" name="boardCd" id="boardCd" value="${boardCd!}"/>
    </form>

</div>

<#include "/view/board/post/board_post_dtl_modal.ftlh">
<#include "/view/board/post/board_post_dtl_template.ftlh">

<#include "/view/_component/comment/_comment_list_modal.ftlh">
<#include "/view/_component/comment/_comment_list_template.ftlh">

<#include "/view/_component/atch_file/_atch_file_list_modal.ftlh">
<#include "/view/_component/atch_file/_atch_file_list_template.ftlh">

</@layout.layout>

