<#--
  -- 휴가계획서 > 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >

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
                commons.util.sortTable();
            },
            /** 목록 검색 */
            search: function () {
                $("#pageNo").val("");
                commons.util.blockUISubmit("#listForm", "${SiteUrl.VCATN_PAPR_LIST!}?actionTyCd=SEARCH");
            },
            /** 내가 작성한 글 보기 */
            myPapr: function () {
                const url = "${SiteUrl.VCATN_PAPR_LIST!}";
                let param = "?searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },
            /** 전체 목록 조회 */
            resetSearch: function() {
                commons.util.blockUIReplace("${SiteUrl.VCATN_PAPR_LIST!}");
            },
            /** 등록 화면 이동 */
            regForm: function () {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.VCATN_PAPR_REG_FORM!}");
            },
            /** 상세 화면 이동 */
            dtl: function (postNo) {
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${SiteUrl.VCATN_PAPR_DTL!}");
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function (postNo) {
                event.stopPropagation();
                const url = "${SiteUrl.VCATN_PAPR_DTL_AJAX!}";
                let ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.resultObj, "vcatn_papr_dtl", "show");
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
        <input type="hidden" name="postNo" id="postNo" />
    </form>
    <#include "/view/vcatn/papr/vcatn_papr_list_header.ftlh">
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <!--begin::Table head-->
        <thead>
            <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                <th class="text-center wb-keepall w-10 hidden-table">번호</th>
                <th class="col-8 col-6 text-center wb-keepall text-center" style="width:40%;">제목</th>
                <th class="col-lg-1 text-center wb-keepall hidden-table">첨부파일</th>
                <th class="col-3 col-3 text-center wb-keepall w-12">작성자</th>
                <th class="text-center wb-keepall w-8 hidden-table">확인 여부</th>
                <th class="text-center wb-keepall w-10 hidden-table">등록일</th>
                <th class="text-center wb-keepall w-10 hidden-table">조회수</th>
            </tr>
        </thead>
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody class="cursor-pointer">
            <!-- 공지사항 상단고정 목록 -->
            <#list vcatnPaprFxdList![] as post>
                <tr class="bg-fxd-list <#if post.isRegstr!false>bg-fxd-list</#if> bg-hover-secondary" onclick="Page.dtl('${post.postNo!}');">
                    <td class="text-center hidden-table">
                        ${post.rnum!"-"}
                    </td>
                    <td class="text-start title-fxd">
                        <div class="d-flex align-items-center justify-content-between">
                            <span>
                                <a href="${SiteUrl.VCATN_PAPR_DTL!}?postNo=${post.postNo!}" class="title-fxd">
                                    <span class="pe-4">(작성예시)</span>
                                    <#list post.schdulList![] as schdul>
                                        <span class="pe-4">
                                            <span class="ctgr-span ctgr-imprtc">${schdul.vcatnNm!}</span>
                                            ${schdul.bgnDt!}
                                            <#if schdul.endDt?? && schdul.bgnDt != schdul.endDt>~ ${schdul.endDt!}</#if>
                                        </span>
                                    </#list>
                                </a>
                                <#if post.comment.cnt gt 0>
                                    <span class="mx-1 text-noti btn-active-warning fs-x-small cursor-pointer opacity-hover" onclick="CommentList.fnModal('${post.postNo!}');"
                                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 모달 팝업 호출">
                                        [${post.comment.cnt!}]
                                    </span>
                                </#if>
                            </span>
                            <a class="badge badge-secondary p-2 btn-white blank blink-slow float-end"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="모달 팝업으로 보기">
                                <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="Page.dtlAjax('${post.postNo!}');"></i>
                            </a>
                        </div>
                    </td>
                    <td class="text-center col-form-label hidden-table">
                        <#if post.atchFileInfo?? && (post.atchFileInfo.atchFileList)?has_content>
                            <a class="badge badge-secondary p-2 btn-white blank blink-slow"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="첨부파일">
                                <i class="bi bi-file-earmark-arrow-down fs-5 text-info opacity-hover" onclick="AtchFileList.fnModal('${post.atchFileId!}');"></i>
                            </a>
                        <#else>
                            -
                        </#if>
                    </td>
                    <td class="text-center ">
                        <#if (post.regstrInfo.proflImgUrl)??>
                            <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                <img src="${(post.regstrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                            </div>
                        </#if>
                        ${post.regstrNm!"-"}
                        <#if post.isRegstr!false>
                            <span class="badge badge-secondary opacity-75 mx-1 fs-9">나</span>
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        <#if post.cfYn! == "Y">
                            <span class="text-success">
                                확인<i class="bi bi-check text-success"></i>
                            </span>
                        <#else>
                            -
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        ${post.regDt!}
                    </td>
                    <td class="text-center hidden-table">
                        ${post.postHit!0?string.number}
                    </td>
                </tr>
            </#list>
            <#list vcatnPaprList![] as post>
                <tr class="<#if post.isRegstr!false>bg-fxd-list</#if> bg-hover-secondary" onclick="Page.dtl('${post.postNo!}');">
                    <td class="text-center hidden-table">
                        ${post.rnum!}
                    </td>
                    <td class="text-start">
                        <div class="d-flex align-items-center justify-content-between">
                            <span>
                                <a href="${SiteUrl.VCATN_PAPR_DTL!}?postNo=${post.postNo!}" class="text-dark">
                                    <#list post.schdulList as schdul>
                                        <span class="pe-4">
                                            <span class="ctgr-span ctgr-gray">${schdul.vcatnNm!}</span>
                                            ${schdul.bgnDt!}
                                            <#if schdul.endDt?? && schdul.bgnDt != schdul.endDt>~ ${schdul.endDt!}</#if>
                                        </span>
                                    </#list>
                                </a>
                                <#if post.comment.cnt gt 0>
                                    <span class="mx-1 text-noti btn-active-warning fs-x-small cursor-pointer opacity-hover" onclick="CommentList.fnModal('${post.postNo!}');"
                                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 모달 팝업 호출">
                                        [${post.comment.cnt!}]
                                    </span>
                                </#if>
                            </span>
                            <a class="badge badge-secondary p-2 btn-white blank blink-slow float-end"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="모달 팝업으로 보기">
                                <i class="bi bi-stickies fs-5 text-noti opacity-hover" onclick="Page.dtlAjax('${post.postNo!}');"></i>
                            </a>
                        </div>
                    </td>
                    <td class="text-center col-form-label hidden-table">
                        <#if post.atchFileInfo?? && (post.atchFileInfo.atchFileList)?has_content>
                            <a class="badge badge-secondary p-2 btn-white blank blink-slow"
                               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="첨부파일">
                                <i class="bi bi-file-earmark-arrow-down fs-5 text-info opacity-hover" onclick="AtchFileList.fnModal('${post.atchFileId!}');"></i>
                            </a>
                        <#else>
                            -
                        </#if>
                    </td>
                    <td class="text-center ">
                        <#if (post.regstrInfo.proflImgUrl)??>
                            <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                <img src="${(post.regstrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                            </div>
                        </#if>
                        ${post.regstrNm!"-"}
                        <#if post.isRegstr!false>
                            <span class="badge badge-secondary opacity-75 mx-1 fs-9">나</span>
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        <#if post.cfYn! == "Y">
                            <span class="text-success">
                                확인<i class="bi bi-check text-success"></i>
                            </span>
                        <#else>
                            -
                        </#if>
                    </td>
                    <td class="text-center hidden-table">
                        ${post.regDt!}
                    </td>
                    <td class="text-center hidden-table">
                        ${post.postHit!0?string.number}
                    </td>
                </tr>
            <#else>
                <tr>
                    <td colspan=8" class="text-center"><@spring.message "view.list.empty"/></td>
                </tr>
            </#list>
        </tbody>
    </table>
    <#include "/view/_component/_pagination.ftlh">
</div>
<!--end::post-->

<#include "/view/vcatn/papr/vcatn_papr_dtl_modal.ftlh">
<#include "/view/vcatn/papr/vcatn_papr_dtl_template.ftlh">

<#include "/view/_component/comment/_comment_list_modal.ftlh">
<#include "/view/_component/comment/_comment_list_template.ftlh">

<#include "/view/_component/atch_file/_atch_file_list_modal.ftlh">
<#include "/view/_component/atch_file/_atch_file_list_template.ftlh">

</@layout.layout>