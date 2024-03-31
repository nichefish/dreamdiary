<#--
  -- 경비 > 물품구매/경조사비 신청 > 목록 화면
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
                commons.util.blockUISubmit("#listForm", "${SiteUrl.EXPTR_REQST_LIST!}?actionTyCd=SEARCH");
            },
            /** 내가 작성한 글 보기 */
            myPapr: function () {
                const url = "${SiteUrl.EXPTR_REQST_LIST!}";
                let param = "?searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },
            /** 전체 목록 조회 */
            resetSearch: function() {
                commons.util.blockUIReplace("${SiteUrl.EXPTR_REQST_LIST!}");
            },
            /** 등록 화면 이동 */
            regForm: function () {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.EXPTR_REQST_REG_FORM!}");
            },
            /** 상세 화면 이동 */
            dtl: function (postNo) {
                commons.util.blockUIRequest();
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${SiteUrl.EXPTR_REQST_DTL!}");
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function (postNo) {
                event.stopPropagation();
                const url = "${SiteUrl.EXPTR_REQST_DTL_AJAX!}";
                let ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.resultObj, "exptr_reqst_dtl", "show");
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
    <!--begin::Card header 검색+헤더 UI -->
    <#include "/view/exptr/reqst/exptr_reqst_list_header.ftlh">
    <!--end::Card header-->
    <!--begin::Card Body-->
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <!--begin::Table head-->
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
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody>
            <#list exptrReqstFxdList![] as post>
                <tr class="bg-fxd-list bg-hover-secondary" onclick="Page.dtl('${post.postNo!}');">
                    <td class="text-center hidden-table">
                        -
                    </td>
                    <td class="text-start cursor-pointer title-fxd">
                        <div class="d-flex align-items-center justify-content-between">
                            <span>
                                <a href="${SiteUrl.EXPTR_REQST_DTL!}?postNo=${post.postNo!}"
                                   class="title-fxd vertical-middle <#if (((post.imprtcYn)!"N") == "Y")!false>fw-bold</#if>">
                                    <#if (post.ctgrNm!"") != "">
                                        <span class="ctgr-span ctgr-imprtc">${post.ctgrNm!}</span>
                                    </#if>
                                    ${post.title!}
                                </a>
                                <#if (((post.imprtcYn)!"N") == "Y")!false>
                                    <span class="fs-1">&#128680;</span>
                                </#if>
                                <#if post.comment.cnt gt 0>
                                    <span class="mx-1 text-noti btn-active-warning fs-x-small cursor-pointer opacity-hover" onclick="CommentList.fnModal('${post.postNo!}');"
                                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 모달 팝업 호출">
                                        [${post.comment.cnt!}]
                                    </span>
                                </#if>
                                <!-- 수정권한 표시 -->
                                <#if ((post.mdfable!"") == (Constant.MDFABLE_MNGR)) && isMngr>
                                    <i class="bi bi-person-lines-fill text-info ms-1 opacity-75"
                                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="관리자 수정 가능"></i>
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
                        <span class="cursor-help"
                              data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최초 작성일:&#10;${post.regDt!"-"}">
                            ${post.managtDt!}
                        </span>
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
                <tr class="bg-hover-secondary" onclick="Page.dtl('${post.postNo!}');">
                    <td class="text-center hidden-table">
                        ${post.rnum!}
                    </td>
                    <td class="text-start cursor-pointer">
                        <div class="d-flex align-items-center justify-content-between">
                            <span>
                                <a href="${SiteUrl.EXPTR_REQST_DTL!}?postNo=${post.postNo!}"
                                   class="text-dark vertical-middle <#if (((post.imprtcYn)!"N") == "Y")!false>fw-bold text-danger</#if>">
                                    <#if (post.ctgrNm!"") != "">
                                        <span class="ctgr-span ctgr-gray">${post.ctgrNm!}</span>
                                    </#if>
                                    ${post.title!}
                                </a>
                                <#if (((post.imprtcYn)!"N") == "Y")!false>
                                    <span class="fs-1">&#128680;</span>
                                </#if>
                                <#if post.comment.cnt gt 0>
                                    <span class="mx-1 text-noti btn-active-warning fs-x-small cursor-pointer opacity-hover" onclick="CommentList.fnModal('${post.postNo!}');"
                                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 모달 팝업 호출">
                                        [${post.comment.cnt!}]
                                    </span>
                                </#if>
                                <#if ((post.mdfable!"") == (Constant.MDFABLE_MNGR)) && isMngr>
                                    <i class="bi bi-person-lines-fill text-info ms-1 opacity-75"
                                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="관리자 수정 가능"></i>
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
                    <td class="text-center col-form-label">
                        <#if (post.managtrInfo.proflImgUrl)??>
                            <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                <img src="${(post.managtrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                            </div>
                        </#if>
                        <span class="cursor-help"
                              data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최초 작성자:&#10;${post.regstrNm!"-"}">
                            ${post.managtrNm!"-"}
                            <#if post.isRegstr!false><span class="badge badge-secondary opacity-75 mx-1 fs-9">나</span></#if>
                        </span>
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
            <!--end::Table row-->
        </tbody>
    </table>
    <!-- pagination::begin -->
    <#include "/view/_component/_pagination.ftlh">
    <!-- pagination::end -->
</div>

<#include "/view/exptr/reqst/exptr_reqst_dtl_modal.ftlh">
<#include "/view/exptr/reqst/exptr_reqst_dtl_template.ftlh">

<#include "/view/_component/comment/_comment_list_modal.ftlh">
<#include "/view/_component/comment/_comment_list_template.ftlh">

<#include "/view/_component/atch_file/_atch_file_list_modal.ftlh">
<#include "/view/_component/atch_file/_atch_file_list_template.ftlh">

</@layout.layout>