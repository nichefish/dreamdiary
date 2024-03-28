<#--
  -- 게시판 > 상세 화면
  -->
<#import "/layout/layout_no_aside.ftlh" as layout >
<#import "/spring.ftl" as spring />

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                //
            },
            close: function() {
                window.close();
            }
        }
    }();
</script>

<div class="card post">
    <form name="procForm" id="procForm">
        <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
        <input type="hidden" name="boardCd" id="boardCd" value="${post.boardCd!}" />
    </form>
    <!--begin::Question-->
    <div class="mb-0">
        <!--begin::Head-->
        <div class="d-flex align-items-center mb-9">
            <!--begin::Title-->
            <div class="col-12">
                <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                    <span class="vertical-middle">
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
                        <#if (authInfo.proflImgUrl)??>
                            <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                <img src="${(authInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                            </div>
                        </#if>
                        ${authInfo.nickNm!"-"}
                    </span>
                    <span class="text-muted fs-7 lh-1">
                        ${currDateStr!}
                    </span>
                </div>
                <!--end::Name-->
            </div>
            <!--end::Author-->
            <!--begin::Info-->
            <div class="d-flex align-items-center py-1">
                <a href="#answers" data-kt-scroll-toggle="true"
                   class="btn btn-sm btn-outline btn-outline-dashed btn-outline-default px-4 me-2">댓글 ${post.commentCnt!}</a>
                <button class="btn btn-sm btn-light px-4 me-2 disabled">조회수 ${post.hitCnt!}</button>
            </div>
            <!--end::Info-->
        </div>
        <!--end::Foot-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
        <!--begin::Question-->
        <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
            ${post.cn?no_esc}
        </div>
        <#if (post.atchFileInfo.atchFileList)?has_content>
            <div class="d-flex flex-stack flex-wrap mb-3 bg-light">
                <div class="col-xl-1 col-form-label fs-6 fw-bold px-5">
                    첨부파일
                </div>
                <div class="col-xl-11 my-3">
                    <#list (post.atchFileInfo.atchFileList)![] as file>
                        <div class="row my-1">
                            <div class="col-xl-8" id="itemContainer${file.atchFileDtlNo!}">
                                <a href="javascript:void(0);" onclick="commons.util.fileDownload('${file.atchFileNo!}', '${file.atchFileDtlNo!}');"
                                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                   title="<@spring.message "bs.tooltip.file.download"/>">
                                    <i class="fas fa-file-download fs-15 me-1"></i>
                                    ${file.orgnFileNm!}(${file.fileSize!}byte)</a>
                            </div>
                        </div>
                    <#else>
                        <span>첨부된 파일이 없습니다.</span>
                    </#list>
                </div>
            </div>
        </#if>
        <#if post.tagStrList?has_content>
            <!--begin::Row-->
            <div class="d-flex flex-stack flex-wrap mb-3 bg-light">
                <div class="col-xl-1 col-form-label fs-6 fw-bold px-5">
                    태그
                </div>
                <div class="col-xl-11 text-sm-start" id="tagSpan">
                    <#list post.tagStrList![] as tag>
                        <span class="ctgr-span ctgr-gray">
                            <span class="ps-1">&#35;</span><u class="text-primary fw-normal">${tag}</u>
                        </span>
                    </#list>
                </div>
            </div>
        </#if>
    </div>
    <div class="separator separator-dashed border-gray-300 my-8"></div>
    <div class="d-flex justify-content-end">
        <button type="button" class="btn btn-sm btn-light" onclick="Page.close();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="팝업 창을 닫습니다.">
            <i class="bi bi-list"></i>닫기
        </button>
    </div>
</div>

</@layout.layout>