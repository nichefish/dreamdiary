<#--
  -- 공지사항 > 상세 화면
  -->
<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

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
    <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
</form>
<div class="card post">
    <!--begin::Question-->
    <div class="mb-0">
        <!-- 게시물post 헤더 제목 영역 -->
        <@component.post_title post />

        <!-- 게시물post 헤더 메타 영역 -->
        <@component.post_meta post />

        <!-- 게시물post 조치자 표시 영역 -->
        <@component.managtr_area post />

        <div class="separator separator-dashed border-gray-300 my-8"></div>

        <!--begin::cn-->
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

    <!-- 열람자 표시 영역 -->
    <@component.viewer_area post />

    <!-- 버튼 영역 -->
    <div class="d-flex justify-content-end">
        <!-- 버튼 :: 수정하기 -->
        <@component.btn_post_mdf "Page" post isMngr />
        <!-- 버튼 :: 삭제하기 -->
        <@component.btn_post_delete "Page" post isMngr />
        <!-- 버튼 :: 목록으로 가기 -->
        <@component.btn_post_toList "Page" />
    </div>

    <!-- 댓글 영역 -->
    <#include "/view/_component/comment/_comment_area.ftlh">
</div>

</@layout.layout>