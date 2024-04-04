<#--
  -- 경비 > 경비지출서 > 경비지출서 작성 규칙 Modal
  -- @included_in : exptr_prsnl_papr_list_header
  -- @included_in : exptr_prsnl_papr_reg_form
  -->
<div class="modal fade" id="exptr_prsnl_rule_modal" tabindex="-1" role="dialog" aria-labelledby="exptrPrsnlRuleModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exptrPrsnlRuleModalLongTitle">[경비지출서 작성 안내]</h5>
                <!--begin::Close-->
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <table class="table table-row-bordered table-row-gray-300 gy-2 align-middle fs-small">
                        <tr class="pt-10px">
                            <td>1. 경비지출서 작성은 각 경비지출내역이 생기는 시점에 업데이트한다.</td>
                        </tr>
                        <tr class="pt-10px">
                            <td>2. 경비지출서는 월 단위로 취합하며 취합일은 익월 3일까지로 한다.</td>
                        </tr>
                        <tr class="pt-10px">
                            <td>3. 취합일이 공휴일일 경우 공휴일은 포함하지 않고 익일까지로 한다.</td>
                        </tr>
                        <tr class="pt-10px">
                            <td>4. 경비지출서 작성시 영수증 사본(스캔 또는 촬영) 파일을 첨부한다.</td>
                        </tr>
                        <tr class="pt-10px">
                            <td>5. 해당 월의 경비지출서를 출력한다.</td>
                        </tr>
                        <tr class="pt-10px">
                            <td>
                                <div>6. 해당 월의 경비지출서와 함께 원본 영수증을 기획팀에 제출한다.</div>
                                <div class="ps-2">(※제출한 원본 영수증 항목에 대하여 제출여부를 작성자가 직접 업데이트한다.)</div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>