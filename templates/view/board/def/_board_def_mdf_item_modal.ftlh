<#--
  -- 게시판 정의 > 항목 수정 Modal
  -- @included_in : board_def_list
  -->
<div class="modal fade" id="board_def_mdf_item_modal" tabindex="-1" role="dialog" aria-labelledby="boardDefModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="boardDefModalLongTitle">[게시판 정의 수정]</h5>
                <div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <form name="boardDefMdfItemForm" id="boardDefMdfItemForm">
                        <div class="row" id="board_def_mdf_item_div">
                            <!-- contents -->
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/board/def/_board_def_mdf_item_template.hbs">