<#--
 * 저널 주제(jrnl_sbjct) > 등록 화면
 * tinyMCE 정책 때문에 문서 최상단에 <!DOCTYPE html> 추가
 -->

<!DOCTYPE html>
<#import "/layout/layout_default.ftlh" as layout >

<script type="text/javascript" src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js?${releaseDate!}"></script>
<script type="text/javascript" src="/js/commons-tinymce.js?${releaseDate!}"></script>
<script type="text/javascript" src="/lib/prism/js/prism.js?${releaseDate!}"></script>

<#assign isReg = isReg!false>
<#assign isMdf = isMdf!false>

<@layout.layout toolbar=toolbar!"">

<script type="text/javascript" src="/metronic/assets/plugins/custom/draggable/draggable.bundle.js?${releaseDate!}"></script>

<#--include::script modules-->
<#include "/view/jrnl/sbjct/_jrnl_sbjct_scripts.ftlh">
<#include "/view/_component/sectn/_sectn_scripts.ftlh">
<#include "/view/_component/comment/_comment_modal_scripts.ftlh">

<script>
    const Page = (function() {
        return {
            /**
             * Page 객체 초기화
             */
            init: function() {
                /* initialize form. */
                JrnlSbjct.initForm();
                <#if isMdf>
                    /* 글 단락 init */
                    Sectn.init({
                        refreshFunc: function() {
                            setTimeout(function() {
                                Page.refreshFunc();
                            });
                        }
                    });
                    /* 글 단락 정렬순서 변경 init */
                    Sectn.initDraggable({
                        refreshFunc: Page.refreshFunc
                    });
                </#if>
                <#if isReg!false>
                    $("#jandiYn").click();
                </#if>
            },
            <#if isMdf>
                refreshFunc: function() {
                    Sectn.listAjax({ "refPostNo": ${post.postNo!}, "refContentType": "${post.contentType!}" });
                    $("#sectn_reg_modal").modal("hide");
                    Sectn.initDraggable({
                        refreshFunc: Page.refreshFunc
                    });
                }
            </#if>
        }
    })();
    document.addEventListener("DOMContentLoaded", function() {
        Page.init();
    });
 </script>

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <!--begin:Form-->
        <form name="jrnlSbjctRegForm" id="jrnlSbjctRegForm" class="form" method="post" enctype="multipart/form-data">
            <input type="hidden" name="postNo" value="${post.postNo!}">
            <input type="hidden" name="atchFileNo" value="${post.atchFileNo!}">
            <input type="hidden" name="hitCnt" value="${post.hitCnt!0?string.number}">

            <#--begin::Row-->
            <div class="row d-flex mb-8">
                <div class="col-12">
                    <label class="d-flex align-items-center mb-2" for="title">
                        <span class="text-gray-700 fs-6 fw-bolder">제목</span>
                        <span class="text-gray-500 fs-9">(최대 50자)</span>
                    </label>
                </div>
                <div class="col-lg-2">
                    <select name="ctgrCd" id="ctgrCd" class="form-select form-select-solid">
                        <option value="">--글분류--</option>
                        <#list JRNL_SBJCT_CTGR_CD![] as ctgr>
                            <option value="${ctgr.dtlCd!}" <#if ctgr.dtlCd == (post.ctgrCd)!>selected="selected"</#if>>[${ctgr.dtlCdNm!}]</option>
                        </#list>
                    </select>
                </div>
                <div class="col-lg-10">
                    <input type="text" name="title" id="title" class="form-control required"
                           value="${post.title!}" maxlength="100" />
                    <span id="title_validate_span"></span>
                </div>
            </div>
            <#--begin::Row-->
            <div class="row <#if isMdf!false>mb-2</#if>">
                <div class="col-lg-1 col-3 d-flex align-items-center">
                    <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" title="해당 글은 굵은 글씨, 아이콘 및 눈에 띄는 색상으로 강조됩니다.">
                        중요 여부
                        <i class="bi bi-question-circle"></i>
                    </label>
                </div>
                <div class="col-lg-2 col-9 d-flex align-items-center">
                    <@component.checkbox post "imprtcYn" "중요//해당없음", "red//gray" "defaultOff" />
                </div>
                <div class="col-lg-1 col-3 d-flex align-items-center">
                    <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" title="해당 글은 페이지에 상관없이&#10;항상 목록의 상단에 노출됩니다.">
                        상단 고정
                        <i class="bi bi-question-circle"></i>
                    </label>
                </div>
                <div class="col-lg-2 col-9 d-flex align-items-center">
                    <@component.checkbox post "fxdYn" "고정//해제", "blue//gray" "defaultOff" />
                </div>
                <div class="col-lg-1 col-3 d-flex align-items-center">
                    <label for="mdfable" class="text-gray-700 fs-6 fw-bolder cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top"
                           title="해당 글을 수정할 수 있는 권한의&#10;범위를 설정합니다.&#10;(기본 : 등록자)">
                        수정 권한
                        <i class="bi bi-question-circle"></i>
                    </label>
                </div>
                <div class="col-lg-2 col-9 d-flex align-items-center">
                    <select name="mdfable" id="mdfable" class="form-select form-select-solid">
                        <#list MDFABLE_CD![] as mdfable>
                            <option value="${mdfable.dtlCd!}" id="${mdfable.dtlCd!}" <#if (post.mdfable!"REGSTR") == (mdfable.dtlCd)>selected="selected"</#if>>[${mdfable.dtlCdNm!}]</option>
                        </#list>
                    </select>
                </div>
            </div>
            <#if isMdf!false>
                <#--begin::Row-->
                <div class="row">
                    <div class="col-lg-1 col-3 d-flex align-items-center">
                        <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                               data-bs-toggle="tooltip" data-bs-placement="top" title="미체크시 수정 일자가&#10;기존 일자로 유지됩니다.&#10;(기본 : ON)">
                            수정일자 변경
                            <i class="bi bi-question-circle"></i>
                        </label>
                    </div>
                    <div class="col-lg-2 col-9 d-flex align-items-center">
                        <@component.checkbox post "managtDtUpdtYn" "변경//미변경", "blue//gray", "defaultOn" />
                    </div>
                </div>
            </#if>

            <div class="separator my-8"></div>

            <div class="d-flex flex-column mb-8">
                <!--begin::Label-->
                <label class="mb-2 d-flex align-items-center" for="tinymce_cn">
                    <span class="text-gray-700 fs-6 fw-bolder">내용</span>
                    <span class="text-gray-500 fs-9">&nbsp;</span>
                </label>
                <!--end::Label-->
                <!--begin::Form control-->
                <div class="mb-3">
                    <textarea name="cn" id="tinymce_cn" class="form-control required" rows="10" placeholder="내용을 입력하세요.">${post.cn!}</textarea>
                </div>
                <div id="tinymce_cn_validate_span"></div>
                <!--end::Form control-->
            </div>

            <div class="separator separator-dashed border-gray-300 mb-8"></div>

            <#--begin::meta-->
            <div class="mb-0">
                <#--페이지:: 첨부파일 추가추가 영역-->
                <#include "/view/_component/atch_file/_atch_file_page_reg_area.ftlh">

                <!-- #include:: component:: 파일시스템 참조 -->
                <#include "/view/_component/flsys/_flsys_reg_area.ftlh">

                <!-- #include:: component:: 태그 태그 -->
                <#include "/view/_component/tag/_tag_page_reg_area.ftlh">
            </div>

            <div class="separator my-8"></div>

            <#--페이지:: 잔디 알림 발송 영역-->
            <@component.jandi_reg_area 1 "NOTICE" />
        </form>
    </div>

    <#--begin::Card footer-->
    <div class="card-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-between">
            <#--페이지:: 버튼: 미리보기-->
            <@component.post_btn_preview_pop "JrnlSbjct.preview();" />
            <div>
                <#--페이지:: 버튼: 저장하기-->
                <@component.post_btn_submit "JrnlSbjct.submit();" />
                <#--페이지:: 버튼: 목록으로 가기-->
                <@component.post_btn_toList "JrnlSbjct.list();" />
            </div>
        </div>
    </div>
</div>

<#if isMdf!false>
    <div class="card post">
        <#--begin::Card body-->
        <div class="card-body pt-0">
            <#--페이지:: 단락 영역-->
            <#include "/view/_component/sectn/_sectn_page_reg_area.ftlh">
        </div>
    </div>
</#if>

<#--include:: component:: tinymce 이미지 추가 영역 template-->
<#include "/view/_component/_tinymce_img.ftlh">
<#include "/view/_component/sectn/_sectn_reg_modal.ftlh">
<#include "/view/_component/sectn/_sectn_list_template.hbs">
<#include "/view/_component/comment/_comment_reg_modal.ftlh">

</@layout.layout>