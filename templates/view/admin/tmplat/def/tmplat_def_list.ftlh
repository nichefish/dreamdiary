<#--
 -- 템플릿 관리 > 목록 화면
 -->
<#import "/layout/layout.ftlh" as layout >

<#-- 툴바 버튼 추가 -->
<#assign toolbar>
    <#include "/view/admin/tmplat/def/_tmplat_def_list_header.ftlh">
</#assign>

<@layout.layout toolbar=toolbar!"">

<script>
    const Page = function() {
        return {
            init: function () {
                commons.util.initSortTable();       // 모든 table 헤더에 클릭 이벤트를 설정한다.
            },
            /** 목록 검색 */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${SiteUrl.TMPLAT_DEF_LIST!}?actionTyCd=SEARCH");
            },
            /** 내가 작성한 글 보기 */
            myPapr: function () {
                const url = "${SiteUrl.TMPLAT_DEF_LIST!}";
                let param = "?searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },
            /** 전체 목록 조회 */
            resetSearch: function() {
                commons.util.blockUIReplace("${SiteUrl.NOTICE_LIST!}");
            },
            /** 등록 화면 이동 */
            regForm: function() {
                commons.util.blockUIRequest();
                commons.util.handlebarsTemplate({}, "tmplat_def_reg", "show");
            },
            /** 등록 처리 (ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("#clCdRegForm #regYn").val("Y");
                    const url = "${SiteUrl.TMPLAT_DEF_REG_AJAX!}";
                    let ajaxData = $("#clCdRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReplace("${SiteUrl.CL_CD_LIST!}");
                            });
                    });
                });
            },
            /** 상세 화면 이동 */
            dtl: function(postNo) {
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${SiteUrl.TMPLAT_DEF_DTL!}");
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function (postNo) {
                event.stopPropagation();
                const url = "${SiteUrl.TMPLAT_DEF_DTL_AJAX!}";
                let ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "tmplat_dtl", "show");
                });
            },
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<form name="procForm" id="procForm">
    <input type="hidden" name="postNo" id="postNo" value=""/>
</form>
<div class="card post">
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <!--begin::Table head-->
        <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 text-muted">
                <th class="text-center wb-keepall w-10 hidden-table">번호</th>
                <th class="col-9 col-6 text-center wb-keepall">제목</th>
                <th class="col-3 col-2 text-center wb-keepall">작성자</th>
                <th class="text-center wb-keepall w-15 hidden-table">등록일</th>
                <th class="text-center wb-keepall hidden-table" style="width:13%;">조회수</th>
            </tr>
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody>
        <!-- 공지사항 상단고정 목록 -->
        <#list tmplatFxdList![] as tmplat>
            <tr class="bg-fxd-list bg-hover-secondary" onclick="Page.dtl('${tmplat.postNo!}');">
                <td class="text-center hidden-table">
                    -
                </td>
                <td class="text-start title-fxd pe-0">
                    <a href="javascript:void(0);">
                        <span class="vertical-middle<#if (((tmplat.imprtcYn)!"N") == "Y")!false> fw-bold</#if>">
                            <#if (tmplat.ctgrNm!"") != "">
                                <span class="ctgr-span ctgr-imprtc">${tmplat.ctgrNm!}</span>
                            </#if>
                            ${tmplat.title!}
                        </span>
                        <#if (((tmplat.imprtcYn)!"N") == "Y")!false>
                            <span class="fs-1">&#128680;</span>
                        </#if>
                        <!-- 목록 수정권한 표시 -->
                        <@component.mdfable post />
                        <!-- 목록 댓글 표시 -->
                        <@component.list_comment post />
                    </a>
                    <a class="badge badge-secondary p-2 btn-white blank blink-slow float-end"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="모달 팝업으로 보기">
                        <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="Page.dtlAjax('${tmplat.postNo!}');"></i>
                    </a>
                </td>
                <td class="text-center col-form-label">
                    ${tmplat.regstrNm!"-"}
                </td>
                <td class="text-center hidden-table">
                    ${tmplat.regDt!}
                </td>
                <td class="text-center hidden-table">
                    ${tmplat.hitCnt!0?string.number}
                </td>
            </tr>
        </#list>
        <!-- 공지사항 목록 -->
        <#list tmplatList![] as tmplat>
            <tr class="bg-hover-secondary" onclick="Page.dtl('${tmplat.postNo!}');">
                <td class="text-center hidden-table">
                    ${tmplat.rnum!}
                </td>
                <td class="text-start">
                    <span class="vertical-middle<#if (((tmplat.imprtcYn)!"N") == "Y")!false> fw-bold text-danger</#if>">
                        <#if (tmplat.ctgrNm!"") != "">
                            <span class="ctgr-span ctgr-gray">${tmplat.ctgrNm!}</span>
                        </#if>
                        ${tmplat.title!}
                    </span>
                    <#if (((tmplat.imprtcYn)!"N") == "Y")!false>
                        <span class="fs-1">&#128680;</span>
                    </#if>
                    <!-- 목록 수정권한 표시 -->
                    <@component.mdfable post />
                    <!-- 목록 댓글 표시 -->
                    <@component.list_comment post />
                    <a class="badge badge-secondary p-2 btn-white blank blink-slow float-end"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="모달 팝업으로 보기">
                        <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="Page.dtlAjax('${tmplat.postNo!}');"></i>
                    </a>
                </td>
                <td class="text-center col-form-label">${tmplat.regstrNm!"-"}
                    <#if tmplat.isRegstr!false>
                        <span class="badge badge-secondary opacity-75 mx-1 fs-9">나</span>
                    </#if>
                </td>
                <td class="text-center hidden-table">
                    ${tmplat.regDt!}
                </td>
                <td class="text-center hidden-table">
                    ${tmplat.hitCnt!0?string.number}
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

<#include "/view/admin/tmplat/def/_tmplat_def_reg_modal.ftlh">
<#include "/view/admin/tmplat/def/_tmplat_def_dtl_modal.ftlh">

</@layout.layout>