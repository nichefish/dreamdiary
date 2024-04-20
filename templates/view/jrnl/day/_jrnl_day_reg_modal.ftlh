<#--
 * 저널 일자(jrnl_day) > 등록/수정 Modal
 * @included_in : jrnl_day_page.ftlh
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="jrnl_day_reg" size="xl">
    <!--begin::Modal Header-->
    <@component.modal_header "저널 일자 저장" />
    <!--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <form name="jrnlDayRegForm" id="jrnlDayRegForm" class="form" enctype="multipart/form-data">
            <div id="jrnl_day_reg_div">
                <!-- contents -->
            </div>
        </form>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-sm btn-primary me-2"
                    onclick="JrnlDay.submit();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                <i class="bi bi-pencil-square"></i>저장
            </button>
            <!-- 버튼:: 모달 닫기 -->
            <@component.btn_modal_close />
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/jrnl/day/_jrnl_day_reg_template.hbs">