<#--
  -- 게시판 > 상세 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout>

<script>
    const Page = function() {
        return {
            init: function () {
                //
            },
            /**
             * 노션Notion 모달 페이지 호출
             */
            notionModal: function() {
                let notionPageId = "${post.notionPageId!}";
                if (notionPageId === "") return;

                const iframe = $('<iframe>', {
                    src: 'http://115.144.8.81:3000/' + notionPageId,
                    id: 'myFrame',
                    frameborder: 0,
                    scrolling: 'yes',
                    width: '100%',
                    height: '700px',
                    css: {
                        border: 'none'
                    }
                }).appendTo('#notion_page_div');

                $("#notion_page_modal").modal("show");
            },
            /** 수정 화면 이동 */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${SiteUrl.BOARD_POST_MDF_FORM!}");
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.BOARD_POST_DEL_AJAX!}";
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
                const listUrl = "${SiteUrl.BOARD_POST_LIST!}?boardCd=${boardCd!}&isBackToList=Y"
                commons.util.blockUIReplace(listUrl);
            },
        }
    }();
</script>

<div class="card post">
    <!--begin::Question-->
    <div class="mb-0">
        <!-- 게시물post 헤더 제목 영역 -->
        <@component.post_title post />

        <!-- 게시물post 헤더 메타 영역 -->
        <@component.post_meta post />

        <!-- 게시물post 조치자 표시 영역 -->
        <@component.managtr_area post />

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
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                  title="최종 작업일시: &#10;${managtr.regDt!}">
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
        <!--end::Foot-->
        <div class="separator separator-dashed border-gray-300 my-8"></div>
        <!--begin::Question-->
        <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
            ${post.cn?no_esc}
        </div>

        <div class="separator separator-dashed border-gray-300 mb-8"></div>

        <!-- #include: 첨부파일 표시 영역 -->
        <#include "/view/_component/atch_file/_atch_file_dtl_area.ftlh">

        <!-- 파일시스템 참조 표시 영역 -->

        <!-- 노션 참조 표시 영역 -->

        <!-- 태그 표시 영역 -->
        <#include "/view/_component/tag/_tag_dtl_area.ftlh">
    </div>

    <div class="separator separator-dashed border-gray-300 my-8"></div>

    <!-- 열람자 표시 영역 -->
    <@component.viewer_area post />

    <!-- 버튼 영역 -->
    <div class="d-flex justify-content-end">
        <@component.btn_mdfable post isMngr />
    </div>

    <!-- 댓글 영역 -->
    <#include "/view/_component/comment/_comment_area.ftlh">

    <form name="procForm" id="procForm">
        <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
        <input type="hidden" name="boardCd" id="boardCd" value="${post.boardCd!}" />
    </form>
</div>

</@layout.layout>
