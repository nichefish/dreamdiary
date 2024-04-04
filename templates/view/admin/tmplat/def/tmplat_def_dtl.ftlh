<#--
  -- 공지사항 > 상세 화면
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
                commons.util.blockUISubmit("#procForm", "${SiteUrl.NOTICE_MDF_FORM!}");
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.NOTICE_DEL_AJAX!}";
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
                const listUrl = "${SiteUrl.NOTICE_LIST!}?isBackToList=Y";
                commons.util.blockUIReplace(listUrl);
            }
        }
    }();
</script>

<form name="procForm" id="procForm">
    <input type="hidden" name="postNo" id="postNo" value="${tmplat.postNo!}" />
</form>
<div class="card post">
    <!--begin::Question-->
    <div class="mb-0">
        <!--begin::Head-->
        <div class="d-flex align-items-center mb-9">
            <!--begin::Title-->
            <div class="col-12">
                <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                    <#if (((tmplat.imprtcYn)!"N") == "Y")!false><span class="badge badge-danger me-2">중요</span></#if>
                    <span class="vertical-middle<#if (((tmplat.imprtcYn)!"N") == "Y")!false> fw-bold</#if>">
                        <#if (tmplat.ctgrNm!"") != "">[${tmplat.ctgrNm!}]</#if>
                        ${tmplat.title!}
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
                    <span class="text-gray-800 fs-6 fw-bold lh-1 mb-2">${tmplat.regstrNm!"-"}</span>
                    <span class="text-muted fs-7 lh-1">${tmplat.regDt!}</span>
                </div>
                <!--end::Name-->
            </div>
            <!--end::Author-->
            <!--begin::Info-->
            <div class="d-flex align-items-center py-1">
                <a href="#answers" data-kt-scroll-toggle="true"
                   class="btn btn-sm btn-outline btn-outline-dashed btn-outline-default px-4 me-2">댓글 ${tmplat.commentCnt!}</a>
                <button class="btn btn-sm btn-light px-4 me-2 disabled">조회수 ${tmplat.hitCnt}</button>
            </div>
            <!--end::Info-->
        </div>
        <!--end::Foot-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
        <!--begin::Question-->
        <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
            ${tmplat.cn?no_esc}
        </div>
        <#if (tmplat.atchFileInfo.atchFileList)?has_content>
            <div class="d-flex flex-stack flex-wrap mb-3 bg-light">
                <div class="col-xl-1">
                    <div class="fs-6 fw-bold col-form-label px-5">
                        첨부파일
                    </div>
                </div>
                <div class="col-xl-11 my-3">
                    <#list (tmplat.atchFileInfo.atchFileList)![] as file>
                        <div class="row my-1">
                            <div class="col-xl-8">
                                <a href="javascript:void(0);" onclick="commons.util.fileDownload('${file.atchFileId!}', '${file.atchFileDtlId!}');"
                                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.file.download"/>">
                                    <i class="fas fa-file-download fs-15 me-1"></i>
                                    ${file.orgnFileNm!}(${file.fileSize!}byte)
                                </a>
                            </div>
                        </div>
                    <#else>
                        <span>첨부된 파일이 없습니다.</span>
                    </#list>
                </div>
            </div>
        </#if>
    </div>
    <div class="separator separator-dashed border-gray-300 my-8"></div>
    <#if isMngr>
        <div class="row mb-5">
            <div class="col-xl-2">
                <div class="col-form-label fs-6 fw-bold mx-5 ">열람자</div>
            </div>
            <div class="col-xl-10 col-form-label">
                <#list tmplat.viewerList![] as viewer>
                    ${viewer.regstrNm!"-"} <#if !viewer?is_last>/</#if>
                </#list>
            </div>
        </div>
        <!--end:Form-->
        <!--end::Card body-->
        <!--begin::Card footer-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
    </#if>
    <div class="d-flex justify-content-end">
        <!-- 수정권한에 따라 수정 권한 부여 -->
        <#if (tmplat.managt.mdfable!"") == (Constant.MDFABLE_REGSTR)>
            <#if tmplat.isRegstr>
                <button type="button" class="btn btn-light btn-active-primary me-2 btn-sm" onclick="Page.mdfForm();"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                    <i class="bi bi-pencil-square"></i>(내 글)수정
                </button>
            </#if>
        <#elseif (tmplat.managt.mdfable!"") == (Constant.MDFABLE_MNGR)>
            <button type="button" class="btn btn-light btn-active-primary me-2 btn-sm" onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>(관리자+)수정
            </button>
        <#elseif (tmplat.managt.mdfable!"") == (Constant.MDFABLE_USER)>
            <button type="button" class="btn btn-light btn-active-primary me-2 btn-sm" onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>(사용자+)수정
            </button>
        <#elseif (tmplat.managt.mdfable!"") == (Constant.MDFABLE_ALL)>
            <button type="button" class="btn btn-light btn-active-primary me-2 btn-sm" onclick="Page.mdfForm();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.page.mdf"/>">
                <i class="bi bi-pencil-square"></i>(전체+)수정
            </button>
        </#if>
        <!-- 등록자 또는 관리자가 삭제 가능 -->
        <#if (tmplat.isRegstr)!false || isMngr>
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
    <#assign post = tmplat>
    <#include "/view/_component/comment/_comment_area.ftlh">
</div>

</@layout.layout>