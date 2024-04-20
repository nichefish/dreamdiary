<#--
  -- 파일시스템 메타(flsys_meta) > 등록 Modal
  -- @included_in : log_actvty_list
  -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="flsys_meta_reg" size="xl">
    <!--Begin::Modal Header-->
    <@component.modal_header "파일시스템 메타 정보 저장" />
    <!--Begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div class="table-responsive-sm">
            <form class="form" name="flsysMetaRegForm" id="flsysMetaRegForm">
                <div id="flsys_meta_reg_div">
                    <!-- contents -->
                </div>
            </form>
        </div>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-sm btn-primary"
                    onclick="commons.util.submit('#flsysMetaRegForm');"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                <i class="bi bi-pencil-square"></i>
                <span class="indicator-label">저장</span>
                <span class="indicator-progress">
                            Please wait...
                            <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                        </span>
            </button>
            <!-- 버튼:: 모달 닫기 -->
            <@component.btn_modal_close />
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/cmm/flsys/_flsys_meta_reg_template.hbs">