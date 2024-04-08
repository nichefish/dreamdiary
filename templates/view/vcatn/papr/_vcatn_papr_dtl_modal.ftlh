<#--
  -- 휴가계획서 > 상세 Modal
  -- @included_in : vcatn_papr_list
  -->
<div class="modal fade" id="vcatn_papr_dtl_modal" tabindex="-1" role="dialog" aria-labelledby="vcatnPaprDtlModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="vcatnPaprDtlModalLongTitle">[휴가계획서]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div id="vcatn_papr_dtl_div">
                        <!-- contents -->
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal">닫기</button>
            </div>
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/vcatn/papr/_vcatn_papr_dtl_template.hbs">