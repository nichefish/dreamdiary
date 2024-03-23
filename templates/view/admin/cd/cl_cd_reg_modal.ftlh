<#--
  -- 분류 코드(CL_CD) > 등록 Modal
  -- @included in : cl_cd_list
  -->
<div class="modal fade" id="cl_cd_reg_modal" tabindex="-1" role="dialog" aria-labelledby="clCdRegModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="clCdRegModalLongTitle">[분류코드 등록]</h5>
                <div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <form name="clCdRegForm" id="clCdRegForm">
                        <div id="cl_cd_reg_div">
                            <!-- contents -->
                        </div>
                    </form>
                    <div class="d-flex justify-content-end">
                        <button type="button" class="btn btn-sm btn-primary me-2"
                                onclick="Page.fnRegAjax();"
                                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                            <i class="bi bi-pencil-square"></i>저장
                        </button>
                        <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal">닫기</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>