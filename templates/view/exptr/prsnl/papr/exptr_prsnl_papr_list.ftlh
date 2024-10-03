<#--
  -- 경비지출서(exptr_prsnl_papr) > 목록 화면
  -->

<#import "/layout/layout.ftlh" as layout >

<#--toolbar::버튼 추가-->
<#assign toolbar>
    <#include "/view/exptr/prsnl/papr/_exptr_prsnl_papr_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<#--include::script modules-->
<#include "/view/exptr/prsnl/papr/_exptr_prsnl_papr_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            init: function () {
                /* 모든 table 헤더에 클릭 이벤트를 설정한다. */
                commons.util.initSortTable();
            },
        }
    })();
    document.addEventListener("DOMContentLoaded", function() {
        Page.init();
    });
</script>

<#--#include:: component:: 태그 클릭 필터링-->
<#include "/view/_component/tag/_tag_list_header.ftlh">

<div class="position-relative me-md-2 searchType">
    <button type="button" class="btn btn-sm btn-light btn-active-secondary mx-1"
            onclick="$('#exptr_prsnl_rule_modal').modal('show');"
            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="작성 안내 팝업을 호출합니다.">
        <i class="fas fa-info-circle"></i>작성 안내
    </button>
    <#include "/view/exptr/prsnl/papr/_exptr_prsnl_papr_rule_modal.ftlh">
</div>

<div class="card post">
    <#--begin::Table-->
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <#--begin::Table head-->
        <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                <th class="text-center wb-keepall w-10 hidden-table">번호</th>
                <th class="col-lg-3 col-9 text-center wb-keepall">제목</th>
                <th class="col-lg-2 col-3 text-center wb-keepall">작성자</th>
                <th class="text-center wb-keepall hidden-table">총액</th>
                <th class="text-center wb-keepall hidden-table">최종수정일</th>
                <th class="text-center wb-keepall hidden-table">영수증(사본/원본)제출여부</th>
                <th class="col-lg-1 text-center wb-keepall hidden-table">영수증 취합<br/>PDF 파일</th>
                <th class="text-center wb-keepall hidden-table">취합여부</th>
            </tr>
        </thead>
        <#--begin::Table body-->
        <tbody>
            <#list exptrPrsnlList![] as post>
                <!-- 년도 또는 월이 넘어가면 구분 간격 표시 -->
                <#assign divideYy = listYy?? && (post.yy?number lt listYy)>
                <#assign divideMnth = listMnth?? && (post.mnth?number lt listMnth)>
                <tr class="bg-hover-secondary<#if post.isRegstr!false> bg-fxd-list</#if> <#if divideYy || divideMnth>border-top-5</#if>"
                    onclick="ExptrPrsnlPapr.dtl('${post.postNo!}')">
                    <td class="text-center hidden-table">${post.rnum!}</td>
                    <td class="text-start" role="button">
                        <a href="${Url.EXPTR_PRSNL_PAPR_DTL!}?postNo=${post.postNo!}" class="text-dark">
                            ${post.title!"-"}
                        </a>
                        <#--목록:: 댓글 표시-->
                        <@component.list_comment post />
                        <#--목록:: 새 글이면 'N' 표시-->
                        <#if post.isNew>
                            <div class="badge border-0 text-white bg-noti blink fs-8 ms-2">N</div>
                        </#if>
                    </td>
                    <td class="text-center">
                        <#if (post.managtrInfo.proflImgUrl)??>
                            <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                <img src="${(post.managtrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                            </div>
                        </#if>
                        <span role="button" onclick="ExptrPrsnlPapr.dtl('${post.postNo!}');">
                            ${post.regstrNm!"-"}
                        </span>
                        <#if post.isRegstr!false>
                            <span class="badge badge-secondary opacity-75 mx-1 fs-9">나</span>
                        </#if>
                    </td>
                    <td class="text-end hidden-table">
                        <span role="button">${post.totAmt!?string.number}원 (${post.itemCnt!?string.number}건)</span>
                    </td>
                    <td class="text-center hidden-table">
                        <#--목록:: 최종수정일 표시-->
                        <@component.list_managtDt post />
                    </td>
                    <td class="text-center hidden-table">
                        <#if post.itemCnt == 0>
                            - / -
                        <#else>
                            <span class="text-${post.atchRciptStus.classNm}">${post.atchRciptStus.nm}</span>
                            /
                            <span class="text-${post.orgnlRciptStus.classNm}">${post.orgnlRciptStus.nm}</span>
                        </#if>
                    </td>
                    <td class="text-center col-form-label hidden-table">
                        <a class="badge badge-secondary p-2 btn-white blank blink-slow"
                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="첨부파일">
                            <i class="bi bi-file-earmark-arrow-down fs-5 text-info opacity-hover" onclick="ExptrPrsnlPapr.pdfDownload('${post.postNo}');"></i>
                        </a>
                    </td>
                    <td class="text-center hidden-table">
                        <#if post.cfYn! == "Y">
                            <span class="text-success">
                                취합<i class="bi bi-check text-success"></i>
                            </span>
                        <#else>
                            -
                        </#if>
                    </td>
                </tr>
                <!-- 년도 또는 월 저장 -->
                <#assign listYy = post.yy?number />
                <#assign listMnth = post.mnth?number />
            <#else>
                <tr>
                    <td colspan="7" class="text-center"><@spring.message "view.list.empty"/></td>
                </tr>
            </#list>
        </tbody>
    </table>

    <#--pagination::begin-->
    <#include "/view/_component/_pagination.ftlh">
    <#--pagination::end-->
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="postNo" id="postNo" value=""/>
    <input type="hidden" name="prevYn" id="prevYn" value="N"/>
</form>

<#include "/view/_component/comment/_comment_list_modal.ftlh">

</@layout.layout>