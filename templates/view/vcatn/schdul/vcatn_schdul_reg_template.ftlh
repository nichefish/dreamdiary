<#--
  -- 휴가관리 > 휴가사용일자 > 휴가 관리자 직접 등록 Template
  -- @included_in : vcatn_schdul_list
-->
<script type="text/x-handlebars-template" id="vcatn_papr_dtl_template">
    <div class="card mt-4">
        <!--begin::title-->
        <div class="mb-0">
            <!--begin::Head-->
            <div class="d-flex align-items-center mb-4">
                <!--begin::Title-->
                <div class="col-12">
                    <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                        <span class="vertical-middle">
                            {{#schdulList}}
                                <span class="fw-bolder fs-2 ctgr-span ctgr-gray">
                                    {{bgnDt}} ~ {{endDt}}
                                </span>
                                <span href="javascript:void(0);" class="fw-bolder mx-3">
                                    {{vcatnNm}}
                                </span>
                            {{/schdulList}}
                        </span>
                    </h1>
                </div>
                <!--end::Title-->
            </div>
            <!--end::Head-->

            <!-- 게시물post 미리보기 헤더 메타 영역 -->
            <@component.post_meta post />

            <div class="separator separator-dashed border-gray-300 my-8"></div>

            <!--begin::cn-->
            <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
                <div class="row mb-4">
                    <div class="col-form-label">
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
                    </div>
                </div>
            </div>
            {{#atchFileInfo}}
                <div class="d-flex flex-stack flex-wrap mb-3 bg-light">
                    <div class="col-xl-2 col-form-label fs-6 fw-bold px-5">
                        첨부파일
                    </div>
                    <div class="col-xl-10 my-3" id="atchFileSpan">
                        {{#atchFileList}}
                            <div class="row my-1" id="atchFile{{atchFileDtlId}}">
                                <div class="col-xl-8" id="itemContainer{{atchFileDtlId}}">
                                    <a href="javascript:void(0);" onclick="commons.util.fileDownload('{{atchFileId}}', '{{atchFileDtlId}}');"
                                       data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.file.download"/>">
                                        <i class="fas fa-file-download fs-15 me-1"></i>
                                        {{orgnFileNm}}({{fileSize}}byte)
                                    </a>
                                    <input type="hidden" name="atchCtrl{{atchFileDtlId}}" id="atchCtrl{{atchFileDtlId}}" value="R">
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