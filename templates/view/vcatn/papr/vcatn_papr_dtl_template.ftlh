<#--
  -- 휴가계획서 > 상세 Template
  -- @included in : vcatn_papr_list
  -->
<script type="text/x-handlebars-template" id="vcatn_papr_dtl_template">
    <div class="card mt-4">
        <!--begin::Question-->
        <div class="mb-0">
            <!--begin::Head-->
            <div class="d-flex align-items-center mb-4">
                <!--begin::Title-->
                <div class="col-12">
                    <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                        <span class="vertical-middle">
                            {{#schdulList}}
                                <span class="fw-bolder fs-2 ctgr-span ctgr-gray">
                                    {{vcatnNm}}
                                </span>
                                <span href="javascript:void(0);" class="fw-bolder mx-3">
                                    {{bgnDt}} ~ {{endDt}}
                                </span>
                            {{/schdulList}}
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

            <!--begin::cn-->
            <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
                <div class="row mb-4">
                    <div class="col-form-label" id="vcatnListDiv">
                        {{#schdulList}}
                            <div class="row d-flex flex-column mx-5 my-4">
                                <div class="d-flex align-items-center position-relative mb-1">
                                    <div class="position-absolute top-0 start-0 rounded h-100 bg-secondary w-4px"></div>
                                    <div class="fw-bold ms-5">
                                        <span class="fs-5 fw-bolder text-dark">
                                            {{vcatnNm}}
                                        </span>
                                        <span href="javascript:void(0);" class="fs-5 fw-bolder text-gray-700 mx-3">
                                            {{bgnDt}} ~ {{endDt}}
                                        </span>
                                        <div class="fs-7 text-muted">{{vcatnResn}}</div>
                                    </div>
                                </div>
                            </div>
                        {{/schdulList}}
                        <span class="text-noti fs-7 ps-5">※주말 및 공휴일은 자동으로 휴가일자 산정에서 제외됩니다. (포함해서 올려도 상관없음)</span>
                    </div>
                </div>
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
                                    <a href="javascript:void(0);" onclick="commons.util.fileDownload('{{atchFileId}}', '{{atchFileDtlId}}');"
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

    </div>
</script>