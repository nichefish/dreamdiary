<#--
  -- 게시판(board_post) > 상세 화면
  -->

<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<!--include:: script modules -->
<#include "/view/board/post/_board_post_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            /**
             * 노션Notion 모달 페이지 호출
             */
            notionModal: function() {
                let notionPageId = "${(post.notionPageId)!}";
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
        }
    })();
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

        <div class="separator separator-dashed border-gray-300 my-8"></div>

        <!--begin::cn-->
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
        <!-- 버튼 :: 수정하기 -->
        <@component.btn_post_modify "BoardPost.mdfForm();" post isMngr />
        <!-- 버튼 :: 삭제하기 -->
        <@component.btn_post_delete "BoardPost.delAjax();" post isMngr />
        <!-- 버튼 :: 목록으로 가기 -->
        <@component.btn_post_toList "BoardPost.list();" />
    </div>

    <!-- 댓글 영역 -->
    <#include "/view/_component/comment/_comment_area.ftlh">
</div>

<form name="procForm" id="procForm">
    <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
    <input type="hidden" name="boardCd" id="boardCd" value="${post.boardCd!}" />
</form>

</@layout.layout>
