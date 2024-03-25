<#--
  -- 분류 코드(CL_CD) > 상세 코드(DTL_CD) 등록/수정 Modal
  -- @included in : cl_cd_dtl
  -->
<div class="modal fade" id="dream_day_reg_modal" tabindex="-1" role="dialog" aria-labelledby="dreamDayRegModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="dreamDayRegModalLongTitle">[상세코드 등록]</h5>
                <div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <form name="dreamDayRegForm" id="dreamDayRegForm" class="form" enctype="multipart/form-data">
                        <div id="dream_day_reg_div">
                            <!-- contents -->
                        </div>
                    </form>
                    <div class="d-flex justify-content-end">
                        <button type="button" class="btn btn-sm btn-primary me-2"
                                onclick="DreamDay.regAjax();"
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