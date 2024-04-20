<#--
 -- 상세 코드(dtl_cd) 등록/수정 Modal
 -- @included_in : cl_cd_dtl
 -->

<#import "/layout/modal_layout.ftlh" as modal >

<@modal.layout id="dtl_cd_reg" size="lg">
    <!--Begin::Modal Header-->
    <@component.modal_header "상세코드 저장" />
    <!--Begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <form name="dtlCdRegForm" id="dtlCdRegForm">
            <div id="dtl_cd_reg_div">
                <!-- contents -->
            </div>
        </form>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-sm btn-primary me-2"
                    onclick="DtlCd.regAjax();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                <i class="bi bi-pencil-square"></i>저장
            </button>
            <!-- 버튼:: 모달 닫기 -->
            <@component.btn_modal_close />
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/admin/cd/_dtl_cd_reg_template.hbs">