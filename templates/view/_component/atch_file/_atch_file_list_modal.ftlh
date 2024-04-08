<#--
  -- 게시판 > 태그 전체 목록 Modal
  -- @included_in : board_post_list
  -- @included_in : board_post_dtl
  -->

<script>
    const AtchFileList = function() {
        return {
            init: function() {
                //
            },
            /**
             * 첨부파일 추가추가
             * (reqstItemIdx는 어차피 한 페이지 내에서 고유하므로 따로 처리해줄 필요 없다.)
             */
            modal: function(atchFileNo) {
                event.stopPropagation();
                const url = "${SiteUrl.FILE_INFO_LIST_AJAX!}";
                let ajaxData = { "atchFileNo": atchFileNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let resultList = res.resultList;
                    if (commons.util.isEmpty(res.resultList)) resultList = [];
                    commons.util.handlebarsTemplate(resultList, "atch_file_list", "show");
                });
            },
        }
    }();

    $(document).ready(function () {
        AtchFileList.init();
    });
</script>

<div class="modal fade" id="atch_file_list_modal" tabindex="-1" role="dialog" aria-labelledby="tagModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="tagModalLongTitle">[첨부파일 목록 조회]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div class="row" id="atch_file_list_div">
                        <!-- contents -->
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal">닫기</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/_component/atch_file/_atch_file_list_template.hbs">