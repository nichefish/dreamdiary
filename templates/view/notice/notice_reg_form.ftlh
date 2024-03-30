<#--
  -- 공지사항 > 등록 화면
  -- tinyMCE 정책 때문에 문서 최상단에 <!DOCTYPE html> 추가
  -->
<!DOCTYPE html>
<#import "/layout/layout.ftlh" as layout >

<script src="/metronic/assets/plugins/custom/tinymce/tinymce.bundle.js"></script>
<script type="text/javascript" src="/js/commons/commons-tinymce.js?20230918"></script>
<script type="text/javascript" src="/lib/prism/js/prism.js"></script>

<#assign isReg = isReg!false>
<#assign isMdf = isMdf!false>

<@layout.layout>

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

    const Page = function() {
        return {
            init: function () {
                /** tinymce init */
                commons.tinymce.init("#tinymce_cn", commons.tinymce.imgRegFunc);

                commons.validate.noSpaces(".no-space");        // 공백 자동 제거

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
                <#if isReg>
                    $("#jandiYn").click();
                </#if>

                /** jquery :: validation */
                $(function () {
                    $("#noticeRegForm").validate({
                        submitHandler: function () {
                            if (Page.submitMode === "preview") {
                                const popupNm = "preview";
                                const options = 'width=1280,height=1440,top=0,left=270';
                                commons.util.popup("", popupNm, options);
                                let popupUrl = "${SiteUrl.NOTICE_REG_PREVIEW_POP!}";
                                $("#noticeRegForm").attr("action", popupUrl).attr("target", popupNm);
                                return true;
                            } else if (Page.submitMode === "submit") {
                                $("#noticeRegForm").removeAttr("action");
                                Swal.fire({
                                    text: <#if isMdf><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                                    showCancelButton: true,
                                }).then(function(result) {
                                    if (!result.value) return;
                                    Page.<#if isMdf>mdf<#else>reg</#if>Ajax();
                                });
                            }
                        },
                        errorPlacement: function(error, element) {
                            commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                        },
                        ignore: [],         // hidden 필드도 검증하기 위함
                    });
                });
            },
            /** submit */
            submit: function() {
                tinymce.activeEditor.save();
                Page.submitMode = "submit";
                $("#noticeRegForm").submit();
            },
            /** 미리보기 팝업 호출 */
            preview: function() {
                tinymce.activeEditor.save();
                Page.submitMode = "preview";
                $("#noticeRegForm").submit();
            },
            /** 등록/수정 처리(Ajax) */
            <#if isMdf>mdf<#else>reg</#if>Ajax: function () {
                let ajaxData = new FormData($("#noticeRegForm")[0]);
                const url = " <#if isMdf>${SiteUrl.NOTICE_MDF_AJAX!}<#else>${SiteUrl.NOTICE_REG_AJAX!}</#if>";
                commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.result) Page.list();
                        });
                }, 'block');
            },
            /** 목록 화면 이동 */
            list: function() {
                const listUrl = "${SiteUrl.NOTICE_LIST!}<#if isMdf>?isBackToList=Y</#if>";
                commons.util.blockUIReplace(listUrl);
            }
        }
    }();

    $(document).ready(function () {
        Page.init();
    });
 </script>

<div class="card post">
    <!--begin:Form-->
    <form name="noticeRegForm" id="noticeRegForm" class="form" method="post" enctype="multipart/form-data">
        <input type="hidden" name="postNo" value="${post.postNo!}">
        <input type="hidden" name="atchFileNo" value="${post.atchFileNo!}">
        <input type="hidden" name="hitCnt" value="${post.hitCnt!0?string.number}">
        <!--begin::Row-->
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
                        <option value="${ctgr.dtlCd!}" id="${ctgr.dtlCd!}" <#if ctgr.dtlCd == (post.ctgrCd)!>selected="selected"</#if>>[${ctgr.dtlCdNm!}]</option>
                    </#list>
                </select>
            </div>
            <div class="col-lg-10">
                <input type="text" class="form-control required" name="title" maxlength='50' id="title" value="${post.title!}"/>
                <span id="titleSpan"></span>
            </div>
        </div>
        <div class="row <#if isMdf>mb-2</#if>">
            <div class="col-lg-1 col-3 d-flex align-items-center">
                <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                       data-bs-toggle="tooltip" data-bs-placement="top" title="해당 글은 굵은 글씨, 아이콘 및 눈에 띄는 색상으로 강조됩니다.">
                    중요 여부
                </label>
            </div>
            <div class="col-lg-2 col-9 d-flex align-items-center">
                <div class="form-check form-switch form-check-custom form-check-solid">
                    <input class="form-check-input ms-3" type="checkbox" value="Y" name="imprtcYn" id="imprtcYn" <#if (((post.imprtcYn)!"N") == "Y")!false>checked="checked"</#if> />
                    <label class="form-check-label fw-bold ms-3" for="imprtcYn" id="imprtcYnLabel" style="color:<#if (((post.imprtcYn)!"N") == "Y")!false>red<#else>gray</#if>;"><#if ((post.imprtcYn)! == "Y")!false>중요<#else>해당없음</#if></label>
                </div>
            </div>
            <div class="col-lg-1 col-3 d-flex align-items-center">
                <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                       data-bs-toggle="tooltip" data-bs-placement="top" title="해당 글은 페이지에 상관없이&#10;항상 목록의 상단에 노출됩니다.">
                    상단 고정
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
        <#if isMdf>
            <div class="row">
                <div class="col-lg-1 col-3 d-flex align-items-center">
                    <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top"
                           title="미설정시 수정 일자가&#10;기존 일자로 유지됩니다.&#10;(기본 : ON)">
                        수정일자 변경
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
            <div id="tinymce_cnSpan"></div>
            <!--end::Form control-->
        </div>

        <!-- #include:: component:: 첨부파일 추가추가 영역 -->
        <#include "/view/_component/atch_file/atch_file_reg_area.ftlh">

        <!-- #include:: component:: 파일시스템 참조 -->
        <#include "/view/_component/flsys/_flsys_reg_area.ftlh">

        <!-- #include:: component:: 태그 태그 -->
        <#include "/view/_component/tag/_tag_reg_area.ftlh">

        <div class="separator my-8"></div>
        <div class="row h-50px">
            <div class="col-lg-1 col-3 d-flex align-items-center">
                <label for="trgetTopic">
                    <span class="text-gray-700 fs-6 fw-bolder cursor-help"
                          data-bs-toggle="tooltip" data-bs-placement="top" title="해당 글이 작성되었음을&#10;사내 메신저(JANDI)를 통해&#10;알립니다.">
                        잔디 알림 발송
                    </span>
                </label>
            </div>
            <div class="col-lg-1 col-3 d-flex align-items-center">
                <div class="form-check form-switch form-check-custom form-check-solid">
                    <input class="form-check-input ms-3" type="checkbox" value="Y" name="jandiYn" id="jandiYn" <#if (((post.jandiYn)!"N") == "Y")!false>checked="checked"</#if> />
                    <label class="form-check-label fw-bold ms-3" for="jandiYn" id="jandiYnLabel" style="color:<#if (((post.jandiYn)!"N") == "Y")!false>#0095E8<#else>gray</#if>;">
                        <#if ((post.jandiYn)! == "Y")!false>발송<#else>미발송</#if>
                    </label>
                </div>
            </div>
            <div class="col-lg-2 col-6 d-flex align-items-center">
                <!-- 가리고 숨기고를 반복하는 곳은 style로 선언해야 한다. -->
                <div id="trgetTopicSpan" style="display:none;">
                    <select name="trgetTopic" id="trgetTopic" class="form-select form-select-solid"
                            data-bs-toggle="tooltip" data-bs-placement="top" title="잔디 메세지를 발송할&#10;토픽(대화방)을 선택합니다.">
                        <#list JANDI_TOPIC_CD![] as topic>
                            <#if topic.dtlCd == "NOTICE">
                                <option value="${topic.dtlCd!}" id="${topic.dtlCd!}" <#if topic.dtlCd == "NOTICE">selected="selected"</#if>>[${topic.dtlCdNm!}]</option>
                            </#if>
                        </#list>
                        <#if isDev>
                            <option value="TEST">[테스트]</option>
                        </#if>
                    </select>
                </div>
            </div>
        </div>
    </form>
    <div class="separator my-8"></div>
    <div class="d-flex justify-content-between">
        <button type="button" class="btn btn-primary mx-2 float-start" onclick="Page.preview();"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                title="작성한 내용에 대하여&#10;미리보기 팝업을 호출합니다.">
            <i class="bi bi-pencil-square"></i>
            <span class="indicator-label">미리보기</span>
            <span class="indicator-progress">Please wait...
            <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
        </button>
        <span>
            <button type="button" class="btn btn-primary mx-2" onclick="Page.submit();"
                    data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.save"/>">
                <i class="bi bi-pencil-square"></i>
                <span class="indicator-label">저장</span>
                <span class="indicator-progress">Please wait...
                    <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
            </button>
            <a href="javascript:void(0);" class="btn btn-light" onclick="Page.list(true);"
               data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.page.list"/>">
                <span class="indicator-label">취소</span>
            </a>
        </span>
    </div>
</div>

<!-- #include:: component:: 첨부파일 추가 영역 template-->
<#include "/view/_component/atch_file/_atch_file_area.ftlh">
<!-- #include:: component:: tinymce 이미지 추가 영역 template-->
<#include "/view/_component/_tinymce_img.ftlh">

</@layout.layout>