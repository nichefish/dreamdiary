<#--
  -- 경비 > 경비지출서 > 목록 화면
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
            /** 목록 검색 */
            fnSearch: function () {
                $("#pageNo").val("");
                commons.util.blockUISubmit("#listForm", "${SiteUrl.EXPTR_PRSNL_PAPR_LIST!}?actionTyCd=SEARCH");
            },
            /** 내가 작성한 글 보기 */
            fnMyPapr: function () {
                const url = "${SiteUrl.EXPTR_PRSNL_PAPR_LIST!}";
                let param = "?searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },
            /** 전체 목록 조회 */
            fnResetSearch: function() {
                commons.util.blockUIReplace("${SiteUrl.EXPTR_PRSNL_PAPR_LIST!}");
            },
            /** 등록 화면 이동 */
            fnRegForm: function () {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.EXPTR_PRSNL_PAPR_REG_FORM!}");
            },
            /** 상세 화면 이동 */
            fnDtl: function (postNo) {
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${SiteUrl.EXPTR_PRSNL_PAPR_DTL!}");
            },
            /** 등록 전 점검 (Ajax) */
            // TODO: 로직 더 단순화할 수 있을 것 같은데???
            fnExistingChck: function() {
                $("#prevYn").val("N");
                const url = "${SiteUrl.EXPTR_PRSNL_PAPR_EXISTING_CHCK_AJAX!}";
                commons.util.blockUIAjax(url, 'post', "", function(res) {
                    if (res.resultMap === undefined) return;
                    let resultMap = res.resultMap;
                    let prevMnth = resultMap.prevMnth;
                    let currMnth = resultMap.currMnth;
                    const prevMnthCompt = (prevMnth !== undefined && prevMnth.cfYn === "Y");
                    const currMnthCompt = (currMnth !== undefined && currMnth.cfYn === "Y");
                    if (prevMnthCompt && currMnthCompt) {
                        alert("<@spring.message 'view.post.indvd.alreadyCompt'/>");
                        return false;
                    } else {
                        const prevMnthOngoing = (prevMnth !== undefined && prevMnth.cfYn !== "Y");
                        if (prevMnthOngoing) {
                            if (confirm("이미 등록중인 글(" + prevMnth.yy + "년 " + prevMnth.mnth + "월)이 있습니다. 해당 글로 이동하시겠습니까?")) {
                                $("#postNo").val(prevMnth.postNo);
                                commons.util.blockUISubmit("#procForm", "${SiteUrl.EXPTR_PRSNL_PAPR_MDF_FORM!}");
                                return false;   // 없으면 아래쪽 regForm()까지 내려가버림
                            }
                        }
                        const currMnthOngoing = (currMnth !== undefined && currMnth.cfYn !== "Y");
                        if (currMnthOngoing) {
                            if (confirm("이미 등록중인 글(" + currMnth.yy + "년 " + currMnth.mnth + "월)이 있습니다. 해당 글로 이동하시겠습니까?")) {
                                $("#postNo").val(currMnth.postNo);
                                commons.util.blockUISubmit("#procForm", "${SiteUrl.EXPTR_PRSNL_PAPR_MDF_FORM!}");
                                return false;   // 없으면 아래쪽 regForm()까지 내려가버림
                            }
                        }
                        // === 로 비교할 시 제대로 비교가 되지 않으므로(원인은?) ==로 비교
                        // TODO: 이쪽 로직 쫌 정리하자
                        if (prevMnth == undefined || currMnth == undefined) {
                            if (prevMnth == undefined && currMnth != undefined) $("#prevYn").val("Y");
                            Page.fnRegForm();
                        } else {
                            alert("경비지출서는 각 월당 한 개만 작성할 수 있습니다.");
                        }
                    }
                });
            },
            /** 개별 영수증 PDF 취합 다운로드 */
            fnPdfDownload: function(postNo) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#postNo").val(postNo);
                    $("#procForm").attr("action", "${SiteUrl.EXPTR_PRSNL_PAPR_RCIPT_PDF_DOWNLOAD!}").submit();
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
        <input type="hidden" name="prevYn" id="prevYn" value="N"/>
    </form>
    <#include "/view/exptr/prsnl/papr/exptr_prsnl_papr_list_header.ftlh">
    <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
        <!--begin::Table head-->
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
        <!--end::Table head-->
        <!--begin::Table body-->
        <tbody>
        <#list exptrIndvdList![] as post>
            <!-- 년도 또는 월이 넘어가면 구분 간격 표시 -->
            <#assign divideYy = listYy?? && (post.yy?number lt listYy)>
            <#assign divideMnth = listMnth?? && (post.mnth?number lt listMnth)>
            <tr class="bg-hover-secondary<#if post.isRegstr!false> bg-fxd-list</#if> <#if divideYy || divideMnth>border-top-5</#if>"
                onclick="Page.fnDtl('${post.postNo!}')">
                <td class="text-center hidden-table">${post.rnum!}</td>
                <td class="text-start" role="button">
                    <a href="${SiteUrl.EXPTR_PRSNL_PAPR_DTL!}?postNo=${post.postNo!}" class="text-dark">
                        ${post.title!"-"}
                    </a>
                    <#if post.comment.cnt gt 0>
                        <span class="mx-1 text-noti btn-active-warning fs-x-small cursor-pointer opacity-hover" onclick="CommentList.fnModal('${post.postNo!}');"
                              data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="댓글 모달 팝업 호출">
                            [${post.comment.cnt!}]
                        </span>
                    </#if>
                </td>
                <td class="text-center">
                    <#if (post.managtrInfo.proflImgUrl)??>
                        <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                            <img src="${(post.managtrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                        </div>
                    </#if>
                    <span role="button" onclick="Page.fnDtl('${post.postNo!}');">
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
                    <span class="cursor-help"
                          data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최초 작성일:&#10;${post.regDt!"-"}">
                        ${post.managtDt!}
                    </span>
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
                        <i class="bi bi-file-earmark-arrow-down fs-5 text-info opacity-hover" onclick="Page.fnPdfDownload('${post.postNo}');"></i>
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
        <!--end::Table row-->
        </tbody>
    </table>
    <!-- pagination::begin -->
    <#include "/view/_component/_pagination.ftlh">
    <!-- pagination::end -->
</div>

<#include "/view/_component/comment/_comment_list_modal.ftlh">
<#include "/view/_component/comment/_comment_list_template.ftlh">

</@layout.layout>