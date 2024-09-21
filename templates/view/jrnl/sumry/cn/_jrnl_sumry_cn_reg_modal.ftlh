<#--
 * 저널 결산 내용(jrnl_sumry_cn) > 등록/수정 Modal
 * @included_in : jrnl_day_page.ftlh
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="jrnl_sumry_cn_reg" size="xl">
    <#--begin::Modal Header-->
    <@component.modal_header "저널 결산 내용 저장" />
    <#--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <#--begin::Form-->
        <form name="jrnlSumryCnRegForm" id="jrnlSumryCnRegForm" class="form" enctype="multipart/form-data">
            <div id="jrnl_sumry_cn_reg_header_div">
                <#--Handlebars template goes in here-->
            </div>
            <#--begin::Row-->
            <div class="row mb-4">
                <div class="col-12">
                    <label class="d-flex align-items-center mb-2" for="tinymce_jrnlSumryCnCn">
                        <span class="text-gray-700 fs-6 fw-bolder">내용</span>
                    </label>
                </div>
                <div class="col-12">
                    <textarea name="cn" id="tinymce_jrnlSumryCnCn" class="form-control form-control-solid h-100px required"></textarea>
                    <div id="tinymce_jrnlSumryCnCn_valid_span"></div>
                </div>
            </div>
            <div id="jrnl_sumry_cn_reg_div">
                <#--Handlebars template goes in here-->
            </div>
        </form>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--모달:: 버튼: 저장하기-->
            <@component.modal_btn_save "JrnlSumryCn.submit();" />
            <#--모달:: 버튼: 모달 닫기-->
            <@component.modal_btn_close_safe />
        </div>
    </div>
</@modal.layout>

<#--include:: Handlebars template-->
<#include "/view/jrnl/sumry/cn/_jrnl_sumry_cn_reg_header_template.hbs">
<#include "/view/jrnl/sumry/cn/_jrnl_sumry_cn_reg_template.hbs">