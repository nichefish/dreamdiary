<#--
  -- 저널 일기(jrnl_diary) > 등록/수정 Modal
  -- @included_in : jrnl_day_page.ftlh
  -->

<div class="modal fade" id="jrnl_diary_reg_modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "저널 일기 저장" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <form name="jrnlDiaryRegForm" id="jrnlDiaryRegForm" class="form" enctype="multipart/form-data">
                        <div id="jrnl_diary_reg_header_div">
                            <!-- contents -->
                        </div>
                        <!--begin::Row-->
                        <div class="row mb-4">
                            <div class="col-2">
                                <div class="fs-6 col-form-label text-center fw-bold">
                                    <label for="tinymce_infrEventInvsCn">조사내용<em class="text-danger">*</em></label>
                                </div>
                            </div>
                            <div class="col-12">
                                <textarea name="cn" id="tinymce_jrnlDiaryCn" class="form-control form-control-solid h-100px required"></textarea>
                            </div>
                        </div>
                        <div class="row mb-4 error-msg-span">
                            <div class="col-2"></div>
                            <div class="col-8">
                                <div id="tinymce_jrnlDiaryCn_valid_span"></div>
                            </div>
                        </div>
                        <div id="jrnl_diary_reg_div">
                            <!-- contents -->
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <!-- 버튼 영역 -->
                <div class="d-flex justify-content-end">
                    <button type="button" class="btn btn-sm btn-primary me-2"
                            onclick="JrnlDiary.submit();"
                            data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.save"/>">
                        <i class="bi bi-pencil-square"></i>저장
                    </button>
                    <!-- 버튼:: 모달 닫기 -->
                    <@component.btn_modal_close />
                </div>
            </div>
        </div>
    </div>
</div>

<!-- include:: Handlebars template -->
<#include "/view/jrnl/diary/_jrnl_diary_reg_header_template.hbs">
<#include "/view/jrnl/diary/_jrnl_diary_reg_template.hbs">