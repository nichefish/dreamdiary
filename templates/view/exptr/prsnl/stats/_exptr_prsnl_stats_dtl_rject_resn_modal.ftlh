<#--
 * 경비 관리 > 경비지출누적집계 > 반려사유 Modal
 * @included_in : exptr_prsnl_stats_dtl
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="rject_resn" size="md">
    <!--begin::Modal Header-->
    <div class="modal-header" style="background-color:#41416e;">
        <h2 class="modal-title text-light">반려하기</h2>
        <#--begin::Close-->
        <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
            <span class="svg-icon svg-icon-2x">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                    <rect opacity="0.5" x="6" y="17.3137" width="16" height="2" rx="1" transform="rotate(-45 6 17.3137)" fill="black"></rect>
                    <rect x="7.41422" y="6" width="16" height="2" rx="1" transform="rotate(45 7.41422 6)" fill="#ffffff"></rect>
                </svg>
            </span>
        </div>
    </div>
    <!--begin::Modal Body-->
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
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <button type="button" id="pwCfMdfBtn" class="btn btn-sm btn-primary" onclick="Page.rjectAjax($('#modal_idx').val(), 'Y');">
                수정
            </button>
            <button type="button" id="pwCfClsBtn" class="btn btn-sm btn-light" data-bs-dismiss="modal">
                닫기
            </button>
        </div>
    </div>
</@modal.layout>
