<#--
  -- 경비 > 물품구매/경조사비 신청 > 상세 Modal
  -- @included_in : exptr_reqst_list
  -->
<div class="modal fade" id="exptr_reqst_dtl_modal" tabindex="-1" role="dialog" aria-labelledby="exptrReqstDtlModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exptrReqstDtlModalLongTitle">[물품구매/경조사비 신청]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div id="exptr_reqst_dtl_div">
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
<#include "/view/exptr/reqst/_exptr_reqst_dtl_template.hbs">