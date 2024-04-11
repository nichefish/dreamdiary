<#--
  -- 게시판 > 상세 Modal
  -- @included_in : board_post_list
  -->
<div class="modal fade" id="board_post_dtl_modal" tabindex="-1" role="dialog" aria-labelledby="postDtlModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="postDtlModalLongTitle">[게시판]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div id="board_post_dtl_div">
                        <!-- contents -->
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <!-- 버튼 영역 -->
                <div class="d-flex justify-content-end">
                    <!-- 버튼:: 모달 닫기 -->
                    <@component.btn_modal_close />
                </div>
            </div>
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/board/post/_board_post_dtl_template.hbs">