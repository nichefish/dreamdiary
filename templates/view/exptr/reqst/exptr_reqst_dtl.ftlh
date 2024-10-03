<#--
 * 물품구매/경조사비 신청(exptr_reqst) > 상세 화면
 -->

<#import "/layout/layout_default.ftlh" as layout >

<@layout.layout toolbar=toolbar!"">

<#--include::script modules-->
<#include "/view/exptr/reqst/_exptr_reqst_scripts.ftlh">

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <#--begin::title-->
        <div class="mb-0">
            <#--페이지:: 헤더: 제목 영역-->
            <@component.post_header_title post />
            <#--페이지:: 헤더: 미리보기 메타 영역-->
            <@component.post_header_meta_preview post />
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
            <#include "/view/_component/atch_file/_atch_file_page_dtl_area.ftlh">

            <#--페이지:: 열람자 표시 영역-->
            <@component.post_viewer_area post />

            <#--페이지:: 관리자 확인 영역-->
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
                                <button class="btn btn-sm btn-primary" onclick="ExptrReqst.cfAjax();">처리완료하기</button>
                                <button class="btn btn-sm btn-secondary ms-4" onclick="ExptrReqst.dismissAjax();">취소(기각) 처리하기</button>
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
            </#if>
        </div>
    </div>
    <#--begin::Card footer-->
    <div class="card-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--페이지:: 버튼: 수정하기-->
            <@component.post_btn_modify "ExptrReqst.mdfForm();" post isMngr />
            <#--페이지:: 버튼: 삭제하기-->
            <@component.post_btn_delete "ExptrReqst.delAjax(${post.postNo});" post isMngr />
            <#--페이지:: 버튼: 목록으로 가기-->
            <@component.post_btn_toList "ExptrReqst.list();" />
        </div>
    </div>

    <#--페이지:: 댓글 영역-->
    <#include "/view/_component/comment/_comment_page_area.ftlh">
</div>

<form name="procForm" id="procForm" method="get">
    <input type="hidden" name="postNo" id="postNo" value="${post.postNo!}" />
</form>

</@layout.layout>