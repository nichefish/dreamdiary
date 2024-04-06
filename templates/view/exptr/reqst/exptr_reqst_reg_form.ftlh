<#--
  -- 경비 > 물품구매/경조사비 신청 > 등록/수정 화면
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
    const Page = function() {
        return {
            init: function () {
                /** tinymce init */
                commons.tinymce.init("#tinymce_cn", commons.tinymce.imgRegFunc);

                commons.validate.noSpaces(".no-space");        // 공백 자동 제거

                // 상단고정 여부 클릭시 글씨 변경
                commons.util.chckboxLabel("fxdYn", "고정//해제", "blue//gray");
                // 수정일자변경여부 클릭시 글씨 변경
                commons.util.chckboxLabel("managtDtUpdtYn", "변경//미변경", "blue//gray");
                // 잔디발송여부 클릭시 글씨 변경
                commons.util.chckboxLabel("jandiYn", "발송//미발송", "blue//gray", function() {
                    $("#trgetTopicSpan").show();
                }, function() {
                    $("#trgetTopicSpan").hide();
                });
                <#if isReg>
                    $("#jandiYn").click();
                </#if>

                /** jquery :: validation */
                $(function(){
                    $("#exptrReqstRegForm").validate({
                        submitHandler: function() {
                            if (Page.submitMode === "preview") {
                                const popupNm = "preview";
                                const options = 'width=1280,height=1440,top=0,left=270';
                                commons.util.popup("", popupNm, options);
                                let popupUrl = "${SiteUrl.EXPTR_REQST_REG_PREVIEW_POP!}";
                                $("#exptrReqstRegForm").attr("action", popupUrl).attr("target", popupNm);
                                return true;
                            } else if (Page.submitMode === "submit") {
                                $("#exptrReqstRegForm").removeAttr("action");
                                Swal.fire({
                                    text: <#if isMdf><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                                    showCancelButton: true,
                                }).then(function(result) {
                                    if (!result.value) return;
                                    Page.<#if isMdf>mdf<#else>reg</#if>Ajax();
                                });
                            }
                        },
                        errorPlacement : function(error, element) {
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
                $("#exptrReqstRegForm").submit();
            },
            /** 미리보기 팝업 호출 */
            preview: function() {
                tinymce.activeEditor.save();
                Page.submitMode = "preview";
                $("#exptrReqstRegForm").submit();
            },
            /** 등록/수정 처리(Ajax) */
            <#if isReg>reg</#if><#if isMdf>mdf</#if>Ajax: function() {
                let ajaxData = new FormData($("#exptrReqstRegForm")[0]);
                const url = "<#if isReg>${SiteUrl.EXPTR_REQST_REG_AJAX!}</#if><#if isMdf>${SiteUrl.EXPTR_REQST_MDF_AJAX!}</#if>";
                commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.result) Page.list();
                        });
                });
            },
            /** 목록 화면 이동 */
            list: function() {
                const listUrl = "${SiteUrl.EXPTR_REQST_LIST!}<#if isMdf>?isBackToList=Y</#if>";
                commons.util.blockUIReplace(listUrl);
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
    });


</script>

<!--begin::Row-->
<div class="row">
    <div class="col-12 d-flex justify-content-end align-items-center mt-4">
        <button type="button" class="btn btn-sm btn-secondary btn-active-primary mx-1" onclick="$('#exptr_reqst_exmpl_modal').modal('show');"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="작성 안내 팝업을 호출합니다.">
            <i class="fas fa-info-circle"></i>작성 안내
        </button>
        <#include "/view/exptr/reqst/_exptr_reqst_exmpl_modal.ftlh">
        <button type="button" class="btn btn-sm btn-secondary btn-active-primary mx-1" onclick="$('#exptr_reqst_rule_modal').modal('show');"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="경조사비 기준 팝업을&#10;호출합니다.">
            <i class="fas fa-info-circle"></i>경조사비 기준
        </button>
        <#include "/view/exptr/reqst/_exptr_reqst_rule_modal.ftlh">
    </div>
</div>
<!-- class 속성이 덮어씌워지지 않아서 스타일 지정 -->
<div class="card post" style="margin-top:15px !important;">
    <!--begin::Form-->
    <form name="exptrReqstRegForm" id="exptrReqstRegForm" class="form" method="post" enctype="multipart/form-data">
        <input type="hidden" name="postNo" value="${post.postNo!}">
        <input type="hidden" name="atchFileId" value="${post.atchFileId!}">
        <input type="hidden" name="hitCnt" value="${post.hitCnt!0?string.number}">
        <input type="hidden" name="cfYn" value="${post.cfYn!"N"}">
        <!--begin::Row-->
        <div class="row d-flex mb-8">
            <div class="col-12">
                <label class="d-flex align-items-center mb-2" for="title">
                    <span class="text-gray-700 fs-6 fw-bolder">제목</span>
                    <span class="text-gray-500 fs-9">(최대 50자)</span>
                </label>
            </div>
            <div class="col-lg-2">
                <select name="ctgrCd" id="ctgrCd" class="form-select form-select-solid"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="글분류를 선택합니다.">
                    <option value="">--글분류--</option>
                    <#list EXPTR_REQST_CTGR_CD![] as ctgr>
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
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당 글은 페이지에 상관없이&#10;항상 목록의 상단에 노출됩니다.">
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
                <label for="mdfable" class="text-gray-700 fs-6 fw-bolder cursor-help"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당 글을 수정할 수 있는 권한의&#10;범위를 설정합니다.&#10;(기본 : 등록자)">
                    수정 권한
                    <i class="bi bi-question-circle"></i>
                </label>
            </div>
            <div class="col-lg-2 col-9 d-flex align-items-center">
                <select name="mdfable" id="mdfable" class="form-select form-select-solid">
                    <#list MDFABLE_CD![] as mdfable>
                        <option value="${mdfable.dtlCd!}" id="${mdfable.dtlCd!}"<#if (post.mdfable!"REGSTR") == (mdfable.dtlCd)> selected="selected"</#if>>[${mdfable.dtlCdNm!}]</option>
                    </#list>
                </select>
            </div>
        </div>
        <#if isMdf>
            <div class="row">
                <div class="col-lg-1 col-3 d-flex align-items-center">
                    <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                           data-bs-toggle="tooltip" data-bs-placement="top" title="미설정시 수정 일자가&#10;기존 일자로 유지됩니다.&#10;(기본 : ON)">
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
                <textarea class="form-control required" rows="10" placeholder="내용을 입력하세요" name="cn" id="tinymce_cn"><#if isReg>${(exptrReqstTmplat.cn)!}</#if>${post.cn!}</textarea>
            </div>
            <!--end::Form control-->
            <div id="tinymce_cnSpan"></div>
        </div>
        <!--begin::Menu separator-->
        <div class="separator my-2"></div>
        <!--end::Menu separator-->

        <!-- #include:: component:: 첨부파일 추가추가 영역 -->
        <#include "/view/_component/atch_file/_atch_file_reg_area.ftlh">

        <div class="separator my-8"></div>
        <div class="row h-50px">
            <div class="col-lg-1 col-3 d-flex align-items-center">
                <label class="text-gray-700 fs-6 fw-bolder cursor-help"
                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="해당 글이 작성되었음을&#10;사내 메신저(JANDI)를 통해&#10;알립니다.">
                    잔디 알림 발송
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
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="잔디 메세지를 발송할&#10;토픽(대화방)을 선택합니다.">
                        <#list JANDI_TOPIC_CD![] as topic>
                            <#if topic.dtlCd == "EXPTR_REQST">
                                <option value="${topic.dtlCd!}" id="${topic.dtlCd!}" <#if topic.dtlCd == "EXPTR_REQST">selected="selected"</#if>>[${topic.dtlCdNm!}]</option>
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

    <!-- 버튼 영역 -->
    <div class="d-flex justify-content-between">
        <!-- 버튼 :: 미리보기 -->
        <@component.btn_preview_pop "Page" />
        <div>
            <!-- 버튼 :: 저장하기 -->
            <@component.btn_post_save "Page" />
            <!-- 버튼 :: 목록으로 가기 -->
            <@component.btn_post_toList "Page" />
        </div>
    </div>

    <!--end::Card footer-->
</div>
<!--end::Card-->

<!-- #include:: component:: 첨부파일 추가 영역 template-->
<#include "/view/_component/atch_file/_atch_file_area.ftlh">
<!-- #include:: component:: tinymce 이미지 추가 영역 template-->
<#include "/view/_component/_tinymce_img.ftlh">

</@layout.layout>