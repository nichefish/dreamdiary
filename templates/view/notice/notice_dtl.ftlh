<#--
  -- 공지사항(notice) > 상세 화면
  -->

<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<#--include::script modules-->
<#include "/view/notice/_notice_scripts.ftlh">

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <!--begin::title-->
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
    </div>
    <!--begin::Card footer-->
    <div class="card-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <!-- 버튼 :: 수정하기 -->
            <@component.btn_post_modify "Notice.mdfForm();" post isMngr />
            <!-- 버튼 :: 삭제하기 -->
            <@component.btn_post_delete "Notice.delAjax();" post isMngr />
            <!-- 버튼 :: 목록으로 가기 -->
            <@component.btn_post_toList "Notice.list();" />
        </div>
        <!-- 댓글 영역 -->
        <#include "/view/_component/comment/_comment_area.ftlh">
    </div>
</div>

<form name="procForm" id="procForm">
    <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
</form>

</@layout.layout>