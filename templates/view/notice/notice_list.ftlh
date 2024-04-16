<#--
  -- 공지사항(notice) > 목록 화면
  -->

<#import "/layout/layout.ftlh" as layout >

<#-- 툴바 버튼 추가 -->
<#assign toolbar>
    <#include "/view/notice/_notice_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<!--include:: script modules -->
<#include "/view/notice/_notice_scripts.ftlh">

<script>
    const Page = function() {
        return {
            init: function () {
                commons.util.initSortTable();       // 모든 table 헤더에 클릭 이벤트를 설정한다.
            },
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<!-- #include:: component:: 태그 클릭 필터링 -->
<#include "/view/_component/tag/_tag_list_header.ftlh">

<button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1" onclick="$('#crdt_user_cttpc_modal').modal('show');"
        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="직원 연락처 팝업을 호출합니다.">
    <i class="bi bi-telephone"></i>직원 연락처
</button>
<#include "/view/notice/_notice_list_crdt_user_cttpc_modal.ftlh">
<button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1" onclick="$('#extn_phone_modal').modal('show');"
        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="내선전화 관련 안내 팝업을&#10;호출합니다.">
    <i class="bi bi-telephone"></i>내선전화 관련
</button>
<#include "/view/notice/_notice_list_extn_phone_modal.ftlh">

<div class="card post">
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <!--begin::Table head-->
        <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 text-muted">
                <th class="text-center wb-keepall w-10 hidden-table">번호</th>
                <th class="col-lg-8 col-9 text-center wb-keepall">제목</th>
                <th class="col-lg-1 text-center wb-keepall hidden-table">첨부파일</th>
                <th class="col-lg-2 col-3 text-center wb-keepall">최종수정자</th>
                <th class="text-center wb-keepall w-15 hidden-table">최종수정일</th>
                <th class="text-center wb-keepall w-10 hidden-table">조회수</th>
            </tr>
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody>
        <!-- 공지사항 상단고정 목록 -->
        <#list noticeFxdList![] as post>
            <tr class="bg-fxd-list bg-hover-secondary" onclick="Notice.dtl('${post.postNo!}');">
                <td class="text-center hidden-table">
                    -
                </td>
                <td class="text-start title-fxd <#if post.tagStrList??>pb-4</#if>">
                    <div class="row d-flex align-items-center justify-content-between">
                        <div class="col-xl-8 col-12">
                            <a href="${Url.NOTICE_DTL!}?postNo=${post.postNo!}"
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
                            <!-- 새 글이면 'N' 표시 -->
                            <#if post.isNew>
                                <div class="badge border-0 text-white bg-noti blink fs-8 ms-2">N</div>
                            </#if>
                        </div>
                        <div class="col-xl-4 col-12 d-flex justify-content-end align-items-center">
                            <!-- 목록 태그 표시 -->
                            <@component.list_tag post />
                            <!-- 모달 팝업 표시 -->
                            <a class="badge badge-secondary p-2 btn-white blank blink-slow float-end"
                               data-bs-toggle="tooltip" data-bs-placement="top" title="모달 팝업으로 보기">
                                <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="Notice.dtlAjax('${post.postNo!}');"></i>
                            </a>
                        </div>
                    </div>
                </td>
                <td class="text-center col-form-label hidden-table">
                    <!-- 목록 첨부파일 표시 -->
                    <@component.list_atch_file post />
                </td>
                <td class="text-center col-form-label">
                    <!-- 목록 최종수정자 표시 -->
                    <@component.list_managtr post />
                </td>
                <td class="text-center hidden-table">
                    <span class="cursor-help"
                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최초 작성일:&#10;${post.regDt!"-"}">
                        ${post.managt.managtDt!}
                    </span>
                </td>
                <td class="text-center hidden-table">
                    ${post.hitCnt!0?string.number}
                </td>
            </tr>
        </#list>
        <!-- 공지사항 목록 -->
        <#list noticeList![] as post>
            <tr class="bg-hover-secondary" onclick="Notice.dtl('${post.postNo!}');">
                <td class="text-center hidden-table">
                    ${post.rnum!}
                </td>
                <td class="text-start <#if post.tagStrList??>pb-4</#if>">
                    <div class="row d-flex align-items-center justify-content-between">
                        <div class="col-xl-8 col-12">
                            <a href="${Url.NOTICE_DTL!}?postNo=${post.postNo!}"
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
                            <!-- 새 글이면 'N' 표시 -->
                            <#if post.isNew>
                                <div class="badge border-0 text-white bg-noti blink fs-8 ms-2">N</div>
                            </#if>
                        </div>
                        <div class="col-xl-4 col-12 d-flex justify-content-end align-items-center">
                            <!-- 목록 태그 표시 -->
                            <@component.list_tag post />
                            <!-- 모달 팝업 표시 -->
                            <a class="badge badge-secondary p-2 btn-white blank blink-slow float-end"
                               onclick="Notice.dtlAjax('${post.postNo!}');"
                               data-bs-toggle="tooltip" data-bs-placement="top" title="모달 팝업으로 보기">
                                <i class="bi bi-stickies fs-5 text-noti opacity-hover"></i>
                            </a>
                        </div>
                    </div>
                </td>
                <td class="text-center col-form-label hidden-table">
                    <!-- 목록 첨부파일 표시 -->
                    <@component.list_atch_file post />
                </td>
                <td class="text-center col-form-label">
                    <!-- 목록 최종수정자 표시 -->
                    <@component.list_managtr post />
                </td>
                <td class="text-center hidden-table">
                    <span class="cursor-help"
                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최초 작성일:&#10;${post.regDt!"-"}">
                        ${post.managt.managtDt!}
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
</div>

<form name="procForm" id="procForm">
    <input type="hidden" name="postNo" id="postNo" value="" />
</form>

<!--include:: Modals-->
<#include "/view/notice/_notice_dtl_modal.ftlh">
<#include "/view/_component/comment/_comment_list_modal.ftlh">
<#include "/view/_component/atch_file/_atch_file_list_modal.ftlh">

</@layout.layout>