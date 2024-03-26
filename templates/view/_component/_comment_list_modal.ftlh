<#--
  -- 댓글 목록 Modal
 -- @included in : 댓글 미리보기 기능이 있는 페이지들
  -->
<script>
    let CommentList = function() {
        return {
            init: function() {
                //
            },
            /**
             * 첨부파일 추가추가
             * (reqstItemIdx는 어차피 한 페이지 내에서 고유하므로 따로 처리해줄 필요 없다.)
             */
            modal: function(postNo) {
                event.stopPropagation();
                $("#commentProcForm #refPostNo").val(postNo);
                const url = "${SiteUrl.COMMENT_LIST_AJAX!}";
                let ajaxData = $("#commentProcForm").serializeArray();
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let resultList = res.resultList;
                    if (commons.util.isEmpty(res.resultList)) resultList = [];
                    commons.util.handlebarsTemplate(resultList, "comment_list", "show");
                });
            },
        }
    }();

    $(document).ready(function () {
        CommentList.init();
    });
</script>

<form name="procForm" id="commentProcForm">
    <input type="hidden" name="refPostNo" id="refPostNo" value="" />
    <input type="hidden" name="refBoardCd" value="${boardCd!}" />
    <input type="hidden" name="actvtyCtgrCd" value="${actvtyCtgrCd!}" />
</form>
<div class="modal fade" id="comment_list_modal" tabindex="-1" role="dialog" aria-labelledby="tagModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="tagModalLongTitle">[댓글 목록 조회]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div class="row" id="comment_list_div">
                        <!-- handlebars template goes in here -->
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal">닫기</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>