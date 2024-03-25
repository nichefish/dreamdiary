<#--
  -- 댓글 목록 Template
 -- @included in : 댓글 미리보기 기능이 있는 페이지들
  -->
<script type="text/x-handlebars-template" id="notice_popup_template">
    <div class="table-responsive-sm">
        <!-- contents -->
        <div class="card mt-4">
            <!--begin::Question-->
            <div class="mb-0">
                <!--begin::Head-->
                <div class="d-flex align-items-center mb-4">
                    <input type="hidden" name="postNo" id="noticePopupPostNo" value="{{postNo}}">
                    <input type="hidden" name="boardCd" id="noticePopupBoardCd" value="{{boardCd}}">
                    <!--begin::Title-->
                    <div class="col-12">
                        <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                            <span class="vertical-middle">
                                {{title}}
                            </span>
                        </h1>
                    </div>
                    <!--end::Title-->
                </div>
                <!--end::Head-->
                <!--begin::Foot-->
                <div class="d-flex flex-stack flex-wrap mb-3">
                    <!--begin::Author-->
                    <div class="d-flex align-items-center">
                        <!--begin::Name-->
                        <div class="d-flex flex-column align-items-start justify-content-center">
                            <span class="text-gray-800 fs-6 fw-bold lh-1 mb-2">{{regstrNm}}</span>
                            <span class="text-muted fs-7 lh-1">{{regDt}}</span>
                        </div>
                        <!--end::Name-->
                    </div>
                    <!--end::Author-->
                    <!--begin::Info-->
                    <div class="d-flex align-items-center py-1">
                        <a href="#answers" data-kt-scroll-toggle="true"
                           class="btn btn-sm btn-outline btn-outline-dashed btn-outline-default px-4 me-2">댓글 {{commentCnt}}</a>
                        <button class="btn btn-sm btn-light px-4 me-2 disabled">조회수 {{hitCnt}}</button>
                    </div>
                    <!--end::Info-->
                </div>
                <!--end::Foot-->
                <div class="separator separator-dashed border-gray-300 my-8"></div>
                <!--begin::Question-->
                <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
                    {{{postCn}}}
                </div>
                {{#atchFileInfo}}
                    <div class="d-flex flex-stack flex-wrap mb-3 bg-light">
                        <div class="col-xl-2 col-form-label fs-6 fw-bold px-5">
                            첨부파일
                        </div>
                        <div class="col-xl-10 my-3">
                            {{#atchFileList}}
                                <div class="row my-1">
                                    <div class="col-xl-8">
                                        <a href="#" onclick="commons.util.fileDownload('{{atchFileNo}}', '{{atchFileDtlNo}}');"
                                           data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.file.download"/>">
                                            <i class="fas fa-file-download fs-15 me-1"></i>
                                            {{orgnFileNm}}({{fileSize}}byte)
                                        </a>
                                    </div>
                                </div>
                            {{/atchFileList}}
                            {{^atchFileList}}
                                <span>첨부된 파일이 없습니다.</span>
                            {{/atchFileList}}
                        </div>
                    </div>
                {{/atchFileInfo}}
            </div>
            <div class="separator separator-dashed border-gray-300 my-8"></div>
            <div class="modal-footer">
                <div class="d-flex align-items-center me-10 text-muted"
                     data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="선택한 기간 동안&#10;이 팝업을 열지 않습니다.">
                    <label for="noticePopupCookieChk" class="cursor-help me-2">이 팝업을 브라우저에서</label>
                    <select name="noticePopupCookieTime" id="noticePopupCookieTime" class="form-select form-select-solid w-auto py-2 me-2 fs-7 cursor-help text-muted">
                        <option value="600">10분간</option>
                        <option value="midnight">오늘 하루</option>
                        <option value="forever">영원히...</option>
                    </select>
                    <label for="noticePopupCookieChk" class="me-2 text-muted cursor-help">보지 않기</label>
                    <input type="checkbox" name="noticePopupCookieChk" id="noticePopupCookieChk" class="form-check-input">
                </div>
                <button type="button" class="btn btn-sm btn-light" onclick="NoticePopup.closeBtnFunc();">닫기</button>
            </div>
        </div>
    </div>
</script>