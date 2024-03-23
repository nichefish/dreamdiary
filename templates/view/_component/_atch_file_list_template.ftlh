<#--
  -- 게시판 > 태그 전체 목록 Template
  -- @included in : board_post_list
  -- @included in : board_post_dtl
  -->
<script type="text/x-handlebars-template" id="atch_file_list_template">
    <div class="card mt-0 py-4">
        <!--begin::Question-->
        <div class="mb-0">
            <table class="table align-middle table-row-dashed fs-small gy-3 table-fixed hoverTable mb-3">
                <tbody>
                    {{#each this}}
                        <tr class="bg-hover-secondary" onclick="commons.util.fileDownload('{{atchFileId}}', '{{atchFileDtlId}}');"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.file.download"/>">
                            <td class="col-lg-7 col-8 text-start wb-keepall ps-6">
                                <a class="badge badge-secondary p-2 btn-white blank blink-slow me-2">
                                    <i class="bi bi-file-earmark-arrow-down-fill text-primary blink-none"></i>
                                </a>
                                <span class="border-bottom text-primary opacity-hover">{{orgnFileNm}}</span>
                            </td>
                            <td class="col-lg-3 text-start hidden-table fs-small">
                                ({{numberFormat fileSize}} bytes)
                            </td>
                            <td class="col-lg-2 col-3 text-start">
                                <div class="badge badge-sm btn-primary badge-light-primary btn-outlined fw-normal">
                                    <i class="fas fa-download fs-7 pe-1"></i>다운로드
                                </div>
                            </td>
                        </tr>
                    {{/each}}
                </tbody>
            </table>
        </div>
    </div>
</script>