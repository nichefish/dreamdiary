<#--
  -- 휴가계획서 > 상세 화면
  -->
<#import "/layout/layout.ftlh" as layout >
<#import "/view/_component/_dtl_btn_area.ftlh" as dtlBtn >

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                commons.validate.noSpaces(".no-space");        // 공백 자동 제거
            },
            /** 관리자 확인 여부 변경 (Ajax) */
            cfAjax: function() {
                const url = "${SiteUrl.VCATN_PAPR_CF_AJAX!}";
                let ajaxData = $("#procForm").serializeArray();
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.result) commons.util.blockUIReload();
                        });
                });
            },
            /** 수정 화면 이동 */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.VCATN_PAPR_MDF_FORM!}");
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.VCATN_PAPR_DEL_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) Page.list();
                            });
                    });
                });
            },
            /** 목록 화면 이동 */
            list: function() {
                commons.util.blockUIReplace("${SiteUrl.VCATN_PAPR_LIST!}?isBackToList=Y");
            }
        }
    }();

    $(document).ready(function () {
        Page.init();
    });
</script>

<div class="card post">
    <form name="procForm" id="procForm">
        <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}"/>
        <input type="hidden" name="contentType" id="contentType" value="${post.contentType!}"/>
    </form>
    <!--begin::Question-->
    <div class="mb-0">
        <!--begin::Head-->
        <div class="d-flex align-items-center mb-9">
            <!--begin::Title-->
            <div class="col-12">
                <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                    <#list post.schdulList![] as item>
                        <span class="fw-bolder fs-2 ctgr-span ctgr-gray">
                            ${item.vcatnNm!}
                        </span>
                        <span href="javascript:void(0);" class="fw-bolder mx-3">
                            ${(item.bgnDt)!}<#if item.bgnDt != item.endDt> ~ ${(item.endDt)!}</#if>
                        </span>
                    </#list>
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
                <button class="btn btn-sm btn-light px-4 me-2 disabled">조회수 ${post.hitCnt!}</button>
            </div>
            <!--end::Info-->
        </div>
        <!--end::Foot-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
        <!--begin::Question-->
        <#if post.schdulList?has_content>
            <div class="row mb-4">
                <div class="col-form-label">
                    <#list post.schdulList![] as item>
                        <div class="row d-flex flex-column mx-5 my-4">
                            <div class="d-flex align-items-center position-relative mb-1">
                                <div class="position-absolute top-0 start-0 rounded h-100 bg-secondary w-4px"></div>
                                <div class="fw-bold ms-5">
                                    <span class="fs-5 fw-bolder text-dark">
                                        ${(item.bgnDt)!}<#if item.bgnDt != item.endDt> ~ ${(item.endDt)!}</#if>
                                    </span>
                                    <span href="javascript:void(0);" class="fs-5 fw-bolder text-gray-700 mx-3">
                                        ${item.vcatnNm!}
                                    </span>
                                    <div class="fs-7 text-muted">${item.vcatnResn!}</div>
                                </div>
                            </div>
                        </div>
                    </#list>
                    <span class="text-noti fs-7 ps-5">※주말 및 공휴일은 자동으로 휴가일자 산정에서 제외됩니다. (포함해서 올려도 상관없음)</span>
                </div>
            </div>
        </#if>

        <!-- #include: 첨부파일 표시 영역 -->
        <#include "/view/_component/atch_file/_atch_file_dtl_area.ftlh">

    </div>
    <div class="separator separator-dashed border-gray-300 my-8"></div>
    <div class="row mb-5">
        <div class="col-xl-2">
            <div class="col-form-label fs-6 fw-bold mx-5">열람자</div>
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
                <div class="col-form-label fs-6 fw-bold mx-5">관리자 확인</div>
            </div>
            <div class="col-xl-10">
                <div class="form-check form-switch mt-2 form-check-custom form-check-solid">
                    <#if post.cfYn == "N">
                        <span class="text-muted me-10">
                            미확인<i class="bi bi-dash text-muted"></i>
                        </span>
                        <button class="btn btn-sm btn-primary" onclick="Page.cfAjax();">확인하기</button>
                    <#else>
                        <button class="btn btn-sm btn-success" disabled>
                            확인완료<i class="bi bi-check success"></i>
                        </button>
                        <span class="ms-3 text-success">확인 처리되었습니다.</span>
                    </#if>
                </div>
            </div>
        </div>
        <div class="separator separator-dashed border-gray-300 my-8"></div>
    </#if>

    <!-- 버튼 영역 -->
    <div class="d-flex justify-content-end">
        <!-- 등록자: 수정,삭제 가능, 관리자: 삭제 가능, 확인 시: 삭제만 가능 -->
        <#if post.isRegstr!false && (post.cfYn)! == "N">
            <button type="button" class="btn btn-light btn-active-primary me-2 btn-sm" onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>수정
            </button>
        </#if>
        <#if (post.isRegstr)!false || isMngr>
            <button type="button" class="btn btn-light btn-active-danger me-2 btn-sm" onclick="Page.delAjax();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.del"/>">
                <i class="bi bi-trash"></i>삭제
            </button>
        </#if>
        <button type="button" class="btn btn-light btn-sm" onclick="Page.list();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.list"/>">
            <i class="bi bi-list"></i>목록
        </button>
    </div>

    <!-- 댓글 영역 -->
    <#include "/view/_component/comment/_comment_area.ftlh">
</div>

</@layout.layout>