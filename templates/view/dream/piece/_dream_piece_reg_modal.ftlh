<#--
  -- 분류 코드(CL_CD) > 상세 코드(DTL_CD) 등록/수정 Modal
  -- @included_in : cl_cd_dtl
  -->
<script>
    const DreamPiece = (function() {
        return {
            init: function () {
                /** jquery :: validation */
                commons.validate.validateForm("#dreamPieceRegForm", DreamPiece.regAjax);
            },
            /** 꿈 조각 등록 모달 팝업 호출 */
            regModal: function(dreamDayNo, dreamtDt) {
                let obj = { "dreamDayNo": dreamDayNo, "dreamtDt": dreamtDt };
                commons.util.handlebarsTemplate(obj, "dream_piece_reg_header");
                commons.util.handlebarsTemplate(obj, "dream_piece_reg", "show");

                // tinymce editor reset
                tinymce.EditorManager.editors.splice(2, 2);     //removing second element in array.
                delete tinymce.EditorManager.editors['tinymce_dreamPieceCn'];     //deleting respective textarea id from array
                if (commons.util.isNotEmpty(tinyMCE.activeEditor)) tinyMCE.activeEditor.setContent("");
                commons.tinymce.init('#tinymce_dreamPieceCn', commons.tinymce.imgRegFunc);
            },
            /** 이관 submit */
            submit: function() {
                tinymce.get("tinymce_dreamPieceCn").save();
                $("#dreamPieceRegForm").submit();
            },
            /** 등록 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.DREAM_PIECE_REG_AJAX!}";
                    let ajaxData = new FormData($("#dreamPieceRegForm")[0]);
                    commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();;
                            });
                    });
                });
            },
            /** 꿈 조각 수정 모달 팝업 호출 */
            mdfModal: function(dreamDayNo, dreamtDt) {
                let obj = { "dreamDayNo": dreamDayNo, "dreamtDt": dreamtDt };
                commons.util.handlebarsTemplate(obj, "dream_piece_reg_header");
                commons.util.handlebarsTemplate(obj, "dream_piece_reg", "show");

                // tinymce editor reset
                tinymce.EditorManager.editors.splice(2, 2);     //removing second element in array.
                delete tinymce.EditorManager.editors['tinymce_dreamPieceCn'];     //deleting respective textarea id from array
                if (commons.util.isNotEmpty(tinyMCE.activeEditor)) tinyMCE.activeEditor.setContent("");
                commons.tinymce.init('#tinymce_dreamPieceCn', commons.tinymce.imgRegFunc);
            },
        }
    })();
    /** 페이지 초기화시 처리 */
    $(document).ready(function() {
        DreamPiece.init();
    });
</script>

<div class="modal fade" id="dream_piece_reg_modal" tabindex="-1" role="dialog" aria-labelledby="dreamPieceRegModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="dreamPieceRegModalLongTitle">[꿈 조각 등록]</h5>
                <div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <form name="dreamPieceRegForm" id="dreamPieceRegForm" class="form" enctype="multipart/form-data">
                        <div id="dream_piece_reg_header_div">
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
                                <textarea name="cn" id="tinymce_dreamPieceCn" class="form-control form-control-solid h-100px required"></textarea>
                            </div>
                        </div>
                        <div class="row mb-4 error-msg-span">
                            <div class="col-2"></div>
                            <div class="col-8">
                                <div id="tinymce_infrEventInvsCnSpan"></div>
                            </div>
                        </div>
                        <div id="dream_piece_reg_div">
                            <!-- contents -->
                        </div>
                    </form>
                    <div class="d-flex justify-content-end">
                        <button type="button" class="btn btn-sm btn-primary me-2"
                                onclick="DreamPiece.submit();"
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
<!-- include:: Handlebars template -->
<#include "/view/dream/piece/_dream_piece_reg_header_template.hbs">
<#include "/view/dream/piece/_dream_piece_reg_template.hbs">