<#--
 * 공지사항(notice) > 등록 화면
 * tinyMCE 정책 때문에 문서 최상단에 <!DOCTYPE html> 추가
 -->

<!DOCTYPE html>
<#import "/layout/layout.ftlh" as layout >

<script src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js?${releaseDate!}"></script>
<script type="text/javascript" src="/js/commons/commons-tinymce.js?${releaseDate!}"></script>
<script type="text/javascript" src="/lib/prism/js/prism.js?${releaseDate!}"></script>

<#assign isReg = isReg!false>
<#assign isMdf = isMdf!false>

<@layout.layout toolbar=toolbar!"">

<#--include::script modules-->
<#include "/view/notice/_notice_scripts.ftlh">

<script>
    /** tinymce 글접기 토글 테스트중... */
    $(document).ready(function() {
        const toggleElements = $("[id^='tinymce_toggle_']");
        toggleElements.on('click', function() {
            const toggleId = $(this).attr('id');
            const sectionContentId = toggleId.replace('tinymce_toggle_', 'tinymce_section_content_');
            const contentElements = $("#" + sectionContentId);
            if (contentElements.length > 0) {
                contentElements.toggleClass('tinymce-collapse-div tinymce-collapsed');
            }
        });
    });

    const Page = (function() {
        return {
            init: function () {
                /* jquery :: validation */
                Notice.initValidateForm()

                /* tinymce init */
                commons.tinymce.init("#tinymce_cn", commons.tinymce.imgRegFunc);
                /* tagify */
                commons.util.tagify("#tagListStr");
                /* 공백 자동 제거 */
                commons.validate.noSpaces(".no-space");

                // 중요 여부 클릭시 글씨 변경
                commons.util.chckboxLabel("imprtcYn", "중요//해당없음", "red//gray");
                // 상단고정 여부 클릭시 글씨 변경
                commons.util.chckboxLabel("fxdYn", "고정//해제", "blue//gray");
                // 팝업공지여부 클릭시 글씨 변경
                commons.util.chckboxLabel("popupYn", "노출//해당없음", "blue//gray");
                // 수정일자변경여부 클릭시 글씨 변경
                commons.util.chckboxLabel("managtDtUpdtYn", "변경//미변경", "blue//gray");
                // 잔디발송여부 클릭시 글씨 변경
                commons.util.chckboxLabel("jandiYn", "발송//미발송", "blue//gray", function () {
                    $("#trgetTopicSpan").show();
                }, function () {
                    $("#trgetTopicSpan").hide();
                });
                <#if isReg!false>
                    $("#jandiYn").click();
                </#if>
            },
        }
    })();
    $(document).ready(function () {
        Page.init();
    });
 </script>

<div class="card post">
    <#--begin::Card body-->
    <div class="card-body">
        <!--begin:Form-->
        <form name="noticeRegForm" id="noticeRegForm" class="form" method="post" enctype="multipart/form-data">
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
                        <#list NOTICE_CTGR_CD![] as ctgr>
                            <option value="${ctgr.dtlCd!}" <#if ctgr.dtlCd == (post.ctgrCd)!>selected="selected"</#if>>[${ctgr.dtlCdNm!}]</option>
                        </#list>
                    </select>
                </div>
                <div class="col-lg-10">
                    <input type="text" class="form-control required" name="title" id="title" maxlength="50" value="${post.title!}"/>
                    <span id="title_valid_span"></span>
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
                    <@component.checkbox post "imprtcYn" "중요//해당없음", "red//gray" />
                </div>
                <div class="col-lg-1 col-3 d-flex align-items-center">
                    <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" title="해당 글은 페이지에 상관없이&#10;항상 목록의 상단에 노출됩니다.">
                        상단 고정
                        <i class="bi bi-question-circle"></i>
                    </label>
                </div>
                <div class="col-lg-2 col-9 d-flex align-items-center">
                    <div class="form-check form-switch form-check-custom form-check-solid">
                        <input class="form-check-input ms-3" type="checkbox" value="Y" name="fxdYn" id="fxdYn" <#if (((post.fxdYn)!"N") == "Y")!false>checked="checked"</#if> />
                        <label class="form-check-label fw-bold ms-3" for="fxdYn" id="fxdYnLabel" style="color:<#if (((post.fxdYn)!"N") == "Y")!false>blue<#else>gray</#if>;"><#if ((post.fxdYn)! == "Y")!false>고정<#else>해제</#if></label>
                    </div>
                </div>
                <div class="col-lg-1 col-3 d-flex align-items-center">
                    <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" title="해당 글은 사이트 접속시&#10;화면에 팝업으로 표출됩니다.">
                        팝업공지 여부
                        <i class="bi bi-question-circle"></i>
                    </label>
                </div>
                <div class="col-lg-2 col-9 d-flex align-items-center">
                    <div class="form-check form-switch form-check-custom form-check-solid">
                        <input class="form-check-input ms-3" type="checkbox" value="Y" name="popupYn" id="popupYn" <#if (((post.popupYn)!"N") == "Y")!false>checked="checked"</#if> />
                        <label class="form-check-label fw-bold ms-3" for="noticeYn" id="popupYnLabel" style="color:<#if (((post.popupYn)!"N") == "Y")!false>blue<#else>gray</#if>;"><#if ((post.popupYn)! == "Y")!false>노출<#else>해당없음</#if></label>
                    </div>
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
                               data-bs-toggle="tooltip" data-bs-placement="top"
                               title="미체크시 수정 일자가&#10;기존 일자로 유지됩니다.&#10;(기본 : ON)">
                            수정일자 변경
                            <i class="bi bi-question-circle"></i>
                        </label>
                    </div>
                    <div class="col-lg-2 col-9 d-flex align-items-center">
                        <div class="form-check form-switch form-check-custom form-check-solid">
                            <input class="form-check-input ms-3" type="checkbox" value="Y" name="managtDtUpdtYn" id="managtDtUpdtYn" checked="checked" />
                            <label class="form-check-label fw-bold ms-3" for="managtDtUpdtYn" id="managtDtUpdtYnLabel" style="color:blue;">변경</label>
                        </div>
                    </div>
                </div>
            </#if>

            <div class="separator my-8"></div>

            <div class="d-flex flex-column mb-8">
                <!--begin::Label-->
                <label class="mb-2" for="tinymce_cn">
                    <span class="text-gray-700 fs-6 fw-bolder">내용</span>
                    <span class="text-gray-500 fs-9">&nbsp;</span>
                </label>
                <!--end::Label-->
                <!--begin::Form control-->
                <div class="mb-3">
                    <textarea class="form-control required" rows="10" placeholder="내용을 입력하세요" name="cn" id="tinymce_cn">${post.cn!}</textarea>
                </div>
                <div id="tinymce_cn_valid_span"></div>
                <!--end::Form control-->
            </div>

            <#--페이지:: 첨부파일 추가추가 영역-->
            <#include "/view/_component/page_area/atch_file/_atch_file_reg_area.ftlh">

            <!-- #include:: component:: 파일시스템 참조 -->
            <#include "/view/_component/page_area/flsys/_flsys_reg_area.ftlh">

            <!-- #include:: component:: 태그 태그 -->
            <#include "/view/_component/page_area/tag/_tag_reg_area.ftlh">

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
            <@component.post_btn_preview_pop "Notice.preview();" />
            <div>
                <#--페이지:: 버튼: 저장하기-->
                <@component.post_btn_submit "Notice.submit();" />
                <#--페이지:: 버튼: 목록으로 가기-->
                <@component.post_btn_toList "Notice.list();" />
            </div>
        </div>
    </div>
</div>

<#--include:: component:: tinymce 이미지 추가 영역 template-->
<#include "/view/_component/_tinymce_img.ftlh">

</@layout.layout>