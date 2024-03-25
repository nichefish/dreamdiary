<#--
  -- 공지사항 > 상세 Template
  -- @included in : notice_list
  -->
<script type="text/x-handlebars-template" id="notice_dtl_template">
    <div class="card mt-4">
        <!--begin::Question-->
        <div class="mb-0">
            <!--begin::Head-->
            <div class="d-flex align-items-center mb-4">
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
                        <span class="text-gray-800 fs-6 fw-bold lh-1 mb-2">
                        {{#if (exists regstrInfo.proflImgUrl)}}
                            <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                <img src="{{regstrInfo.proflImgUrl}}" class="img-thumbnail p-0 w-100" />
                            </div>
                        {{/if}}
                        {{regstrNm}}
                        </span>
                        <span class="text-muted fs-7 lh-1">
                            {{regDt}}
                        </span>
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
                    <div class="col-xl-2">
                        <div class="fs-6 fw-bold col-form-label px-5">
                            첨부파일
                        </div>
                    </div>
                    <div class="col-xl-10 my-3">
                        {{#atchFileList}}
                            <div class="row my-1">
                                <div class="col-xl-8">
                                    <a href="javascript:void(0);" onclick="commons.util.fileDownload('{{atchFileId}}', '{{atchFileDtlId}}');"
                                       data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.file.download"/>">
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

    </div>
</script>