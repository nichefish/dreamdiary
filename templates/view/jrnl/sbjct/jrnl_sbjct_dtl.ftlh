<#--
 * 저널 주제(jrnl_sbjct) > 상세 화면
 -->

<#import "/layout/layout.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<#--include::script modules-->
<#include "/view/jrnl/sbjct/_jrnl_sbjct_scripts.ftlh">

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::title-->
        <div class="mb-0">
            <#--페이지:: 헤더: 제목 영역-->
            <@component.post_header_title post />
            <#--페이지:: 헤더: 메타 영역-->
            <@component.post_header_meta post />
            <#--페이지:: 조치자 표시 영역-->
            <@component.post_header_managtr_area post />
        </div>

        <div class="separator separator-dashed border-gray-300 my-8"></div>

        <#--begin::cn-->
        <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6 min-h-150px">
            ${post.cn?no_esc}
        </div>

        <div class="separator separator-dashed border-gray-300 mb-8"></div>

        <#--begin::meta-->
        <div class="mb-0">
            <#--페이지:: 첨부파일 표시 영역-->
            <#include "/view/_component/page_area/atch_file/_atch_file_dtl_area.ftlh">

            <#--페이지:: 파일시스템 참조 표시 영역-->

            <#--페이지:: 노션 참조 표시 영역-->

            <#--페이지:: 태그 표시 영역-->
            <#include "/view/_component/page_area/tag/_tag_dtl_area.ftlh">

            <#--페이지:: 열람자 표시 영역-->
            <@component.post_viewer_area post />
        </div>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--페이지:: 버튼: 수정하기-->
            <@component.post_btn_modify "JrnlSbjct.mdfForm();" post isMngr />
            <#--페이지:: 버튼: 삭제하기-->
            <@component.post_btn_delete "JrnlSbjct.delAjax();" post isMngr />
            <#--페이지:: 버튼: 목록으로 가기-->
            <@component.post_btn_toList "JrnlSbjct.list();" />
        </div>
        <#--페이지:: 댓글 영역-->
        <#include "/view/_component/page_area/comment/_comment_area.ftlh">
    </div>
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
</form>

</@layout.layout>