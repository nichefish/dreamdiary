<#--
 * 저널 주제(jrnl_sbjct) > 목록 화면
 -->

<#import "/layout/layout.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/jrnl/sbjct/_jrnl_sbjct_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<#--include::script modules-->
<#include "/view/jrnl/sbjct/_jrnl_sbjct_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            init: function () {
                /* 모든 table 헤더에 클릭 이벤트를 설정한다. */
                commons.util.initSortTable();
            },
        }
    })();
    $(document).ready(function() {
        Page.init();
    });
</script>

<#--#include:: component:: 태그 클릭 필터링-->
<#include "/view/_component/tag/_tag_list_header.ftlh">

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::Table-->
        <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
            <#--begin::Table head-->
            <thead>
                <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 text-muted">
                    <th class="text-center wb-keepall w-10 hidden-table">번호</th>
                    <th class="col-lg-8 col-9 text-center wb-keepall">제목</th>
                    <th class="col-lg-1 text-center wb-keepall hidden-table">첨부파일</th>
                    <th class="text-center wb-keepall w-10 hidden-table">조회수</th>
                </tr>
            </thead>
            <#--begin::Table body-->
            <tbody>
                <#--begin::상단고정 목록-->
                <#list jrnlSbjctFxdList![] as post>
                    <tr class="bg-fxd-list bg-hover-secondary" onclick="JrnlSbjct.dtl('${post.postNo!}');">
                        <td class="text-center hidden-table">
                            -
                        </td>
                        <td class="text-start title-fxd <#if post.tagStrList??>pb-4</#if>">
                            <div class="row d-flex align-items-center justify-content-between">
                                <div class="col-xl-8 col-12 d-flex align-items-center">
                                    <a href="${Url.JRNL_SBJCT_DTL!}?postNo=${post.postNo!}"
                                       class="title-fxd vertical-middle<#if (((post.imprtcYn)!"N") == "Y")!false> fw-bold</#if>">
                                        <#if (post.ctgrNm!"") != "">
                                            <span class="ctgr-span ctgr-imprtc">${post.ctgrNm!}</span>
                                        </#if>
                                        ${post.title!}
                                    </a>
                                    <#if (((post.imprtcYn)!"N") == "Y")!false>
                                        <span class="fs-4 blink ms-1">&#128680;</span>
                                    </#if>
                                    <#--목록:: 수정권한 표시-->
                                    <@component.mdfable post />
                                    <#--목록:: 댓글 표시-->
                                    <@component.list_comment post />
                                    <#--목록:: 새 글이면 'N' 표시-->
                                    <#if post.isNew>
                                        <div class="badge border-0 text-white bg-noti blink fs-8 ms-2">N</div>
                                    </#if>
                                </div>
                                <div class="col-xl-4 col-12 d-flex justify-content-end align-items-center">
                                    <#--목록:: 태그 표시-->
                                    <@component.list_tag post />
                                    <#--목록:: 모달 팝업 열기 버튼 표시-->
                                    <@component.list_dtl_modal post "JrnlSbjct.dtlAjax" />
                                </div>
                            </div>
                        </td>
                        <td class="text-center col-form-label hidden-table">
                            <#--목록:: 첨부파일 모달 열기 버튼 표시-->
                            <@component.list_atch_file post />
                        </td>
                        <td class="text-center hidden-table">
                            ${post.hitCnt!0?string.number}
                        </td>
                    </tr>
                </#list>
                <#--begin::목록-->
                <#list jrnlSbjctList![] as post>
                    <tr class="bg-hover-secondary" onclick="JrnlSbjct.dtl('${post.postNo!}');">
                        <td class="text-center hidden-table">
                            ${post.rnum!}
                        </td>
                        <td class="text-start <#if post.tagStrList??>pb-4</#if>">
                            <div class="row d-flex align-items-center justify-content-between">
                                <div class="col-xl-8 col-12 d-flex align-items-center">
                                    <a href="${Url.JRNL_SBJCT_DTL!}?postNo=${post.postNo!}"
                                       class="text-dark vertical-middle<#if (((post.imprtcYn)!"N") == "Y")!false> fw-bold text-danger</#if>">
                                        <#if (post.ctgrNm!"") != "">
                                            <span class="ctgr-span ctgr-gray">${post.ctgrNm!}</span>
                                        </#if>
                                        ${post.title!}
                                    </a>
                                    <#if (((post.imprtcYn)!"N") == "Y")!false>
                                        <span class="fs-4 blink ms-1">&#128680;</span>
                                    </#if>
                                    <#--목록:: 수정권한 표시-->
                                    <@component.mdfable post />
                                    <#--목록:: 댓글 표시-->
                                    <@component.list_comment post />
                                    <#--목록:: 새 글이면 'N' 표시-->
                                    <#if post.isNew>
                                        <div class="badge border-0 text-white bg-noti blink fs-8 ms-2">N</div>
                                    </#if>
                                </div>
                                <div class="col-xl-4 col-12 d-flex justify-content-end align-items-center">
                                    <#--목록:: 태그 표시-->
                                    <@component.list_tag post />
                                    <#--목록:: 모달 팝업 열기 버튼 표시-->
                                    <@component.list_dtl_modal post "JrnlSbjct.dtlAjax" />
                                </div>
                            </div>
                        </td>
                        <td class="text-center col-form-label hidden-table">
                            <#--목록:: 첨부파일 모달 열기 버튼 표시-->
                            <@component.list_atch_file post />
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
            </tbody>
        </table>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer">
        <#--pagination::begin-->
        <#include "/view/_component/list_area/_pagination.ftlh">
        <#--pagination::end-->
    </div>
</div>

<form name="procForm" id="procForm">
    <input type="hidden" name="postNo" id="postNo" value="" />
</form>

<#--include::modals-->
<#--<#include "/view/notice/_notice_dtl_modal.ftlh">-->
<#include "/view/_component/list_area/comment/_comment_list_modal.ftlh">
<#include "/view/_component/list_area/atch_file/_atch_file_list_modal.ftlh">

</@layout.layout>