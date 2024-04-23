<#--
 * 경비 관리 > 경비지출누적집계 > 반려사유 Modal
 * @included_in : exptr_prsnl_stats_dtl
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="rject_resn" size="md">
    <#--begin::Modal Header-->
    <@component.modal_header_dark "반려하기" />
    <#--begin::Modal Body-->
    <div class="modal-body">
        <form name="myPwCfForm" id="myPwCfForm" class="form">
            <input type="hidden" id="modal_idx" value="">
            <#--begin::Row-->
            <div class="row mb-5">
                <div class="col-xl-3">
                    <div class="col-form-label text-center fs-6 fw-bold">
                        <label for="rjectResnShow">반려사유</label>
                    </div>
                </div>
                <div class="col-xl-9 text-start">
                    <input type="text" name="rjectResnShow" id="rjectResnShow" class="form-control" value="">
                </div>
            </div>
        </form>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <button type="button" id="pwCfMdfBtn" class="btn btn-sm btn-primary" onclick="Page.rjectAjax($('#modal_idx').val(), 'Y');">
                수정
            </button>
            <#--모달:: 버튼: 모달 닫기-->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>
