<#--
  -- 활동 로그 > 상세 Modal
  -- @included_in : log_actvty_list
  -->
<div class="modal fade" id="flsys_file_vod_modal" tabindex="-1" role="dialog" aria-labelledby="flsysMetaDtlModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="flsysMetaDtlModalLongTitle">[메타 정보 조회]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <div id="flsys_file_vod_div">
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
<#include "/view/cmm/flsys/_flsys_file_vod_template.hbs">