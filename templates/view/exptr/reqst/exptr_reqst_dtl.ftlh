<#--
  -- 경비 > 물품구매/경조사비 신청 > 상세 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                //
            },
            /** 수정 화면 이동 */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.EXPTR_REQST_MDF_FORM!}");
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.EXPTR_REQST_DEL_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) Page.list();
                            });
                    });
                });
            },
            /** 관리자 확인 여부 변경 (Ajax) */
            cfAjax: function() {
                Swal.fire({
                    text: "완료 처리하시겠습니까?",
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.EXPTR_REQST_CF_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 항목 미처리 (Ajax) */
            fnDismissAjax: function() {
                Swal.fire({
                    text: "취소(기각) 처리하시겠습니까?",
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.EXPTR_REQST_DISMISS_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            },
            /** 목록 화면 이동 */
            list: function() {
                commons.util.blockUIReplace("${SiteUrl.EXPTR_REQST_LIST!}?isBackToList=Y");
            }
        }
    }();
</script>

<div class="card post">
    <form name="procForm" id="procForm">
        <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
    </form>
    <!--begin::Question-->
    <div class="mb-0">
        <!--begin::Head-->
        <div class="d-flex align-items-center mb-9">
            <!--begin::Title-->
            <div class="col-12">
                <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                    <#if (((post.imprtcYn)!"N") == "Y")!false><span class="badge badge-danger me-2">중요</span></#if>
                    <span class="vertical-middle<#if (((post.imprtcYn)!"N") == "Y")!false> fw-bold</#if>">
                        <#if (post.ctgrNm!"") != "">[${post.ctgrNm!}]</#if>
                        ${post.title!}
                     </span>
                </h1>
            </div>
            <!--end::Title-->
        </div>
        <!--end::Head-->
        <!--begin::Foot-->
        <div class="d-flex flex-stack flex-wrap mb-3">
            <!--begin::Author-->
            <div class="d-flex align-items-center">
                <!--begin::Name-->
                <div class="d-flex flex-column align-items-start justify-content-center">
                    <span class="text-gray-800 fs-6 fw-bold lh-1 mb-2">
                        <#if (post.regstrInfo.proflImgUrl)??>
                            <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                <img src="${(post.regstrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                            </div>
                        </#if>
                        ${post.regstrNm!"-"}
                    </span>
                    <span class="text-muted fs-7 lh-1">
                        ${post.regDt!}
                    </span>
                </div>
                <!--end::Name-->
            </div>
            <!--end::Author-->
            <!--begin::Info-->
            <div class="d-flex align-items-center py-1">
                <a href="#answers" data-kt-scroll-toggle="true"
                   class="btn btn-sm btn-outline btn-outline-dashed btn-outline-default px-4 me-2">댓글 ${post.comment.cnt!}</a>
                <button class="btn btn-sm btn-light px-4 me-2 disabled">조회수 ${post.hitCnt!0?string.number}</button>
            </div>
            <!--end::Info-->
        </div>
        <!--end::Foot-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
        <!--begin::Question-->
        <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
            ${post.cn?no_esc}
        </div>

        <div class="separator separator-dashed border-gray-300 mb-8"></div>

        <!-- #include: 첨부파일 표시 영역 -->
        <#include "/view/_component/atch_file/_atch_file_dtl_area.ftlh">

    </div>
    <div class="separator separator-dashed border-gray-300 my-8"></div>
    <div class="row mb-5">
        <div class="col-xl-2">
            <div class="col-form-label fs-6 fw-bold mx-5 ">열람자</div>
        </div>
        <div class="col-xl-10 col-form-label">
            <#list post.viewerList![] as viewer>
                ${viewer.regstrNm!"-"} <#if !viewer?is_last>/</#if>
            </#list>
        </div>
    </div>
    <#if isMngr>
        <div class="row mb-5">
            <div class="col-xl-2">
                <div class="col-form-label fs-6 fw-bold mx-5">
                    <label for="cfYn">관리자 확인</label>
                </div>
            </div>
            <div class="col-xl-10">
                <div class="form-check form-switch mt-2 form-check-custom form-check-solid">
                    <#if post.cfYn == "N">
                        <span class="text-muted me-10">
                            미처리<i class="bi bi-dash text-muted"></i>
                        </span>
                        <button class="btn btn-sm btn-primary" onclick="Page.cfAjax();">처리완료하기</button>
                        <button class="btn btn-sm btn-secondary ms-4" onclick="Page.fnDismissAjax();">취소(기각) 처리하기</button>
                    <#elseif post.cfYn == "X">
                        <button class="btn btn-sm btn-secondary" disabled>
                            취소<i class="bi bi-x text-dark"></i>
                        </button>
                        <span class="ms-3 text-muted">처리하지 않습니다.</span>
                    <#elseif post.cfYn == "Y">
                        <button class="btn btn-sm btn-success" disabled>
                            처리완료<i class="bi bi-check"></i>
                        </button>
                        <span class="ms-3 text-success">처리 완료되었습니다.</span>
                    </#if>
                </div>
            </div>
        </div>
        <!--end:Form-->
        <!--end::Card body-->
        <!--begin::Card footer-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
    </#if>
    <div class="d-flex justify-content-end">
        <!-- 수정권한에 따라 수정 권한 부여 -->
        <#if post.cfYn == "N" && (post.managt.mdfable!"") == (Constant.MDFABLE_REGSTR)>
            <#if post.isRegstr>
                <button type="button" class="btn btn-sm btn-light btn-active-primary me-2" onclick="Page.mdfForm();"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                    <i class="bi bi-pencil-square"></i>(내 글)수정
                </button>
            </#if>
        <#elseif (post.managt.mdfable!"") == (Constant.MDFABLE_MNGR)>
            <button type="button" class="btn btn-sm btn-light btn-active-primary me-2" onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>(관리자+)수정
            </button>
        <#elseif post.cfYn == "N" && (post.managt.mdfable!"") == (Constant.MDFABLE_USER)>
            <button type="button" class="btn btn-sm btn-light btn-active-primary me-2" onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>(사용자+)수정
            </button>
        <#elseif post.cfYn == "N" && (post.managt.mdfable!"") == (Constant.MDFABLE_ALL)>
            <button type="button" class="btn btn-sm btn-light btn-active-primary me-2" onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>(전체+)수정
            </button>
        </#if>
        <!-- 등록자+관리자만 삭제 가능 -->
        <#if (post.isRegstr)!false || isMngr>
            <button type="button" class="btn btn-sm btn-light btn-active-danger me-2" onclick="Page.delAjax();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.del"/>">
                <i class="bi bi-trash"></i>삭제
            </button>
        </#if>
        <button type="button" class="btn btn-sm btn-light" onclick="Page.list();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.list"/>">
            <i class="bi bi-list"></i>목록
        </button>
    </div>

    <#include "/view/_component/comment/_comment_area.ftlh">
</div>

</@layout.layout>