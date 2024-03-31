<#--
  -- 공지사항 > 상세 화면
  -->
<#import "/layout/layout.ftlh" as layout >
<#import "/view/_component/_dtl_btn_area.ftlh" as dtlBtn >

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
                <button class="btn btn-sm btn-light px-4 me-2 disabled">조회수 ${post.hitCnt!}</button>
            </div>
            <!--end::Info-->
        </div>
        <!--end::Foot-->
        <#if post.managtrList?? && post.managtrList?size gt 1>
            <div class="separator separator-dashed border-gray-300 mb-8"></div>
            <div class="d-flex flex-stack flex-wrap justify-content-start ">
                <!--begin::Author-->
                <div class="d-flex align-items-center me-5">
                    <span class="text-gray-400 fs-6 fw-bold lh-1">
                        작업자:
                    </span>
                </div>
                <div class="d-flex align-items-center">
                    <#list post.managtrList![] as managtr>
                        <!--begin::Name-->
                        <div class="text-gray-600 fs-6 me-4">
                            <#if (managtr.regstrInfo.proflImgUrl)??>
                                <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                    <img src="${(managtr.regstrInfo.proflImgUrl)!}" class="img-thumbnail p-0 w-100" />
                                </div>
                            </#if>
                            <span class="cursor-help"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="최종 작업일시: &#10;${managtr.regDt!}">
                                ${managtr.regstrNm!"-"}
                            </span>
                            <#if managtr?has_next>, </#if>
                        </div>
                        <!--end::Name-->
                    </#list>
                </div>
                <!--end::Author-->
            </div>
        </#if>
        <div class="separator separator-dashed border-gray-300 my-8"></div>
        <!--begin::Question-->
        <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
            ${post.cn?no_esc}
        </div>

        <div class="separator separator-dashed border-gray-300 mb-8"></div>

        <!-- #include: 첨부파일 표시 영역 -->
        <#include "/view/_component/atch_file/_atch_file_dtl_area.ftlh">

        <!-- 파일시스템 참조 표시 영역 -->

        <!-- 태그 표시 영역 -->
        <#include "/view/_component/tag/_tag_dtl_area.ftlh">
    </div>
    <div class="separator separator-dashed border-gray-300 my-8"></div>
    <#if isMngr>
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
        <!--end:Form-->
        <!--end::Card body-->
        <!--begin::Card footer-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
    </#if>

    <!-- 버튼 영역 -->
    <div class="d-flex justify-content-end">
        <@dtlBtn.managtBtn post isMngr />
    </div>

    <!-- 댓글 영역 -->
    <#include "/view/_component/comment/_comment_area.ftlh">
</div>

</@layout.layout>