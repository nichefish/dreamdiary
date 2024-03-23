<!--
 -- 레이아웃 > 첨부파일 모듈
 -- @included in : 파일 첨부 기능이 있는 페이지들
 -->
<!--begin::Row-->
<div class="row">
    <div>
        <label class="mb-2">
            <span class="text-gray-700 fs-6 fw-bolder">첨부파일</span>
            <span class="text-gray-500 fs-9 mx-2">※ 첨부파일은 개별 50MB, 최대 100MB까지 업로드 가능합니다.</span>
        </label>
        <input class="form-control" type="file" id="formFile" onclick="AtchFile.addFileItem();">
    </div>
    <div class="col-xl-10 mb-8 text-sm-start" id="atchFileSpan">
        <#if post.atchFileInfo??>
            <#list post.atchFileInfo.atchFileList![] as file>
                <div class="row mt-2" id="atchFile${file.atchFileDtlId!}">
                    <div class="col-xl-8 text-sm-start" id="itemContainer${file.atchFileDtlId!}">
                        <i class="fas fa-file-download fs-15 me-3"></i>
                        <a href="#" onclick="commons.util.fileDownload('${file.atchFileId!}', '${file.atchFileDtlId!}');"
                           data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.file.download"/>">
                            ${file.orgnFileNm!} (${file.fileSize!}byte)
                        </a>
                        <div class="badge badge-light btn-primary badge-outlined mx-2 cursor-pointer"
                             onclick="AtchFile.fnDelExistingFile(${file.atchFileDtlId!});"
                             data-bs-toggle="tooltip" data-bs-placement="top" title="<@spring.message "bs.tooltip.file.del"/>">
                            삭제
                        </div>
                        <input type="hidden" name="atchCtrl${file.atchFileDtlId!}" id="atchCtrl${file.atchFileDtlId!}" value="R">
                    </div>
                </div>
            </#list>
        </#if>
    </div>
</div>