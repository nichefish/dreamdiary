<#--
  -- 게시판 목록 > 댓글 조회 !Handlebars! Template
  -- @included_in : 댓글 미리보기 기능이 있는 페이지들
  -->
<script type="text/x-handlebars-template" id="comment_list_template">
    <div class="card mt-0 py-4">
        <div class="mb-0">
            <table class="table align-middle table-row-dashed fs-small gy-3 table-fixed hoverTable mb-3">
                <tbody>
                    {{#each this}}
                        <tr>
                            <td class="col-lg-2 col-3 text-start wb-keepall ps-6">
                                <div class="text-gray-800 fs-6 fw-bold lh-1 mb-2">
                                    {{#if (exists regstrInfo.proflImgUrl)}}
                                        <div class="btn btn-icon btn-active-light-primary position-relative w-15px h-15px w-md-20px h-md-20px me-1">
                                            <img src="{{regstrInfo.proflImgUrl}}" class="img-thumbnail p-0 w-100" />
                                        </div>
                                    {{/if}}
                                    {{regstrNm}}
                                </div>
                                <div class="text-muted fs-7 lh-1">
                                    {{regDt}}
                                </div>
                            </td>
                            <td class="col-lg-10 col-9 text-start fs-small">
                                <div class="div-textarea-smp">{{cn}}</div>
                            </td>
                        </tr>
                    {{/each}}
                </tbody>
            </table>
        </div>
    </div>
</script>