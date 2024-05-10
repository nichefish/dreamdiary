<#--
 * 물품구매/경조사비 신청(exptr_reqst) > 목록 화면
 -->

<#import "/layout/layout.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/exptr/reqst/_exptr_reqst_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<#--include::script modules-->
<#include "/view/exptr/reqst/_exptr_reqst_scripts.ftlh">

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

<button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1"
        onclick="$('#exptr_reqst_exmpl_modal').modal('show');"
        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="작성 안내 팝업을 호출합니다.">
    <i class="fas fa-info-circle"></i>작성 안내
</button>
<#include "/view/exptr/reqst/_exptr_reqst_exmpl_modal.ftlh">
<button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1"
        onclick="$('#exptr_reqst_rule_modal').modal('show');"
        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="경조사비 기준 팝업을&#10;호출합니다.">
    <i class="fas fa-info-circle"></i>경조사비 기준
</button>
<#include "/view/exptr/reqst/_exptr_reqst_rule_modal.ftlh">

<#--#include:: component:: 태그 클릭 필터링-->
<#include "/view/_component/tag/_tag_list_header.ftlh">

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::Table-->
        <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
            <#--begin::Table head-->
            <thead>
                <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                    <th class="text-center wb-keepall w-8 hidden-table">번호</th>
                    <th class="col-lg-7 col-9 text-center wb-keepall">제목</th>
                    <th class="col-lg-1 text-center wb-keepall hidden-table">첨부파일</th>
                    <th class="col-lg-2 col-3 text-center wb-keepall">작성자</th>
                    <th class="text-center wb-keepall hidden-table" style="width: 12%;">등록일</th>
                    <th class="text-center wb-keepall w-8 hidden-table">조회수</th>
                    <th class="text-center wb-keepall w-8 hidden-table">처리 여부</th>
                </tr>
            </thead>
            <#--begin::Table body-->
            <tbody>
                <#list exptrReqstFxdList![] as post>
                    <tr class="bg-fxd-list bg-hover-secondary" onclick="ExptrReqst.dtl('${post.postNo!}');">
                        <td class="text-center hidden-table">
                            -
                        </td>
                        <td class="text-start cursor-pointer title-fxd">
                            <div class="d-flex align-items-center justify-content-between">
                                <span>
                                    <a href="${Url.EXPTR_REQST_DTL!}?postNo=${post.postNo!}"
                                       class="title-fxd vertical-middle <#if (((post.imprtcYn)!"N") == "Y")!false>fw-bold</#if>">
                                        <#if (post.ctgrNm!"") != "">
                                            <span class="ctgr-span ctgr-imprtc">${post.ctgrNm!}</span>
                                        </#if>
                                        ${post.title!}
                                    </a>
                                    <#if (((post.imprtcYn)!"N") == "Y")!false>
                                        <span class="fs-1">&#128680;</span>
                                    </#if>
                                    <#--목록:: 수정권한 표시-->
                                    <@component.mdfable post />
                                    <#--목록:: 댓글 표시-->
                                    <@component.list_comment post />
                                    <#--목록:: 새 글이면 'N' 표시-->
                                    <#if post.isNew>
                                        <div class="badge border-0 text-white bg-noti blink fs-8 ms-2">N</div>
                                    </#if>
                                </span>
                                <#--목록:: 모달 팝업 열기 버튼 표시-->
                                <@component.list_dtl_modal post "ExptrReqst.dtlAjax" />
                            </div>
                        </td>
                        <td class="text-center col-form-label hidden-table">
                            <#--목록:: 첨부파일 모달 열기 버튼 표시-->
                            <@component.list_atch_file post />
                        </td>
                        <td class="text-center col-form-label">
                            <#if (post.managtrInfo.proflImgUrl)??>
                                <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                    <img src="${(post.managtrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                                </div>
                            </#if>
                            <span class="cursor-help"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최초 작성자:&#10;${post.regstrNm!"-"}">
                                ${post.managtrNm!"-"}
                            </span>
                        </td>
                        <td class="text-center hidden-table">
                            <#--목록:: 최종수정일 표시-->
                            <@component.list_managtDt post />
                        </td>
                        <td class="text-center hidden-table">
                            ${post.hitCnt!0?string.number}
                        </td>
                        <td class="text-center hidden-table">
                            <#if post.cfYn! == "Y">
                                <span class="text-success">
                                    완료<i class="bi bi-check text-success"></i>
                                </span>
                            <#elseif post.cfYn == "X">
                                <span class="text-muted">
                                    취소<i class="bi bi-x text-muted"></i>
                                </span>
                            <#else>
                                -
                            </#if>
                        </td>
                    </tr>
                </#list>
                <#list exptrReqstList![] as post>
                    <tr class="bg-hover-secondary" onclick="ExptrReqst.dtl('${post.postNo!}');">
                        <td class="text-center hidden-table">
                            ${post.rnum!}
                        </td>
                        <td class="text-start cursor-pointer">
                            <div class="d-flex align-items-center justify-content-between">
                                <span>
                                    <a href="${Url.EXPTR_REQST_DTL!}?postNo=${post.postNo!}"
                                       class="text-dark vertical-middle <#if (((post.imprtcYn)!"N") == "Y")!false>fw-bold text-danger</#if>">
                                        <#if (post.ctgrNm!"") != "">
                                            <span class="ctgr-span ctgr-gray">${post.ctgrNm!}</span>
                                        </#if>
                                        ${post.title!}
                                    </a>
                                    <#if (((post.imprtcYn)!"N") == "Y")!false>
                                        <span class="fs-1">&#128680;</span>
                                    </#if>
                                    <#--목록:: 수정권한 표시-->
                                    <@component.mdfable post />
                                    <#--목록:: 댓글 표시-->
                                    <@component.list_comment post />
                                    <#--목록:: 새 글이면 'N' 표시-->
                                    <#if post.isNew>
                                        <div class="badge border-0 text-white bg-noti blink fs-8 ms-2">N</div>
                                    </#if>
                                </span>
                                <#--목록:: 모달 팝업 열기 버튼 표시-->
                                <@component.list_dtl_modal post "ExptrReqst.dtlAjax" />
                            </div>
                        </td>
                        <td class="text-center col-form-label hidden-table">
                            <#--목록:: 첨부파일 모달 열기 버튼 표시-->
                            <@component.list_atch_file post />
                        </td>
                        <td class="text-center col-form-label">
                            <#--목록:: 최종수정자 표시-->
                            <@component.list_managtr post />
                        </td>
                        <td class="text-center hidden-table">
                            <#--목록:: 최종수정일 표시-->
                            <@component.list_managtDt post />
                        </td>
                        <td class="text-center hidden-table">
                            ${post.hitCnt!0?string.number}
                        </td>
                        <td class="text-center hidden-table">
                            <#if post.cfYn! == "Y">
                                <span class="text-success">
                                    완료<i class="bi bi-check text-success"></i>
                                </span>
                            <#elseif post.cfYn == "X">
                                <span class="text-muted">
                                    취소<i class="bi bi-x text-muted"></i>
                                </span>
                            <#else>
                                <span class="text-muted blink">
                                    대기중<i class="bi bi-three-dots text-muted"></i>
                                </span>
                            </#if>
                        </td>
                    </tr>
                <#else>
                    <tr>
                        <td colspan=8" class="text-center">
                            <@spring.message "view.list.empty"/>
                        </td>
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
    <input type="hidden" name="postNo" id="postNo" />
</form>

<#include "/view/exptr/reqst/_exptr_reqst_dtl_modal.ftlh">
<#include "/view/_component/list_area/comment/_comment_list_modal.ftlh">
<#include "/view/_component/list_area/atch_file/_atch_file_list_modal.ftlh">

</@layout.layout>