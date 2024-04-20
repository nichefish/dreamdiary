<#--
 * 저널 꿈(jrnl_dream) > 등록/수정 Modal
 * @included_in : jrnl_day_page.ftlh
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="jrnl_dream_reg" size="xl">
    <!--begin::Modal Header-->
    <@component.modal_header "저널 꿈 저장" />
    <!--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <form name="jrnlDreamRegForm" id="jrnlDreamRegForm" class="form" enctype="multipart/form-data">
            <div id="jrnl_dream_reg_header_div">
                <!-- contents -->
            </div>
            <#--begin::Row-->
            <div class="row mb-4">
                <div class="col-12">
                    <label class="d-flex align-items-center mb-2" for="title">
                        <span class="text-gray-700 fs-6 fw-bolder">내용</span>
                    </label>
                </div>
                <div class="col-12">
                    <textarea name="cn" id="tinymce_jrnlDreamCn" class="form-control form-control-solid h-100px required"></textarea>
                    <div id="tinymce_jrnlDreamCn_valid_span"></div>
                </div>
            </div>
            <div id="jrnl_dream_reg_div">
                <!-- contents -->
            </div>
        </form>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-sm btn-primary me-2"
                    onclick="JrnlDream.submit();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                <i class="bi bi-pencil-square"></i>저장
            </button>
            <!-- 버튼:: 모달 닫기 -->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/jrnl/dream/_jrnl_dream_reg_header_template.hbs">
<#include "/view/jrnl/dream/_jrnl_dream_reg_template.hbs">