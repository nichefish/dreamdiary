<#--
 * 경비지출서(exptr_prsnl_papr) > 영수증 Template
 * @included_in : exptr_prsnl_papr_reg_form
 * @included_in : exptr_prsnl_papr_dtl
 -->

<script>
    const Rcipt = (function() {
        return {
            /**
             * 영수증 이미지 첨부 (등록/수정 페이지) (ajax)
             */
            uploadRegAjax: function(idx) {
                let fileInput = $("#atchFile0");
                fileInput.click();
                fileInput.on("change", function() {
                    if (this.value !== "") {
                        if (!commons.validate.fileSizeChck(this)) return false;      // fileSizeChck
                        if (!commons.validate.fileImgExtnChck(this)) return false;      // fileExtnChck
                        const url = "${Url.FILE_UPLOAD_AJAX!}";
                        const ajaxData = new FormData(document.getElementById("exptrPrsnlRciptForm"));
                        commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                            Swal.fire({ text: res.message })
                                .then(function() {
                                    if (res.rslt) {
                                        let fileInfo = res.rsltObj;
                                        let atchFileDtlNo = fileInfo.atchFileDtlNo;
                                        $("#atchFileDtlNo"+idx).val(atchFileDtlNo);
                                        let rciptFileIconStr = "<a href=\"#\" class=\"opacity-75-hover\" onclick=\"commons.util.fileDownload('', '" + atchFileDtlNo+ "');\"> <span class=\"svg-icon svg-icon-2hx svg-icon-primary\" ><svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" fill=\"none\"> <path opacity=\"0.3\" d=\"M19 22H5C4.4 22 4 21.6 4 21V3C4 2.4 4.4 2 5 2H14L20 8V21C20 21.6 19.6 22 19 22ZM13 15.4V10C13 9.4 12.6 9 12 9C11.4 9 11 9.4 11 10V15.4H8L11.3 18.7C11.7 19.1 12.3 19.1 12.7 18.7L16 15.4H13Z\" fill=\"black\"/> <path d=\"M15 8H20L14 2V7C14 7.6 14.4 8 15 8Z\" fill=\"black\"/> </svg></span>";
                                        $("#rciptRegBtn"+idx).hide();
                                        $("#rciptFile"+idx).empty().append(rciptFileIconStr);
                                        $("#rciptDel"+idx).show();
                                    }
                                    fileInput.unbind();
                                });
                        });
                    }
                });
            },

            /**
             * 영수증 이미지 첨부 (상세 페이지) (ajax)
             */
            uploadDtlAjax: function(exptrPrsnlItemNo) {
                let fileInput = $("#atchFile0");
                fileInput.click();
                fileInput.on("change", function() {
                    if (this.value !== "") {
                        if (!commons.validate.fileSizeChck(this)) return false;      // fileSizeChck
                        if (!commons.validate.fileImgExtnChck(this)) return false;      // fileExtnChck
                        $("#exptrPrsnlItemNo_upload").val(exptrPrsnlItemNo);
                        const url = "${Url.EXPTR_PRSNL_ITEM_RCIPT_UPLOAD_AJAX!}";
                        const ajaxData = new FormData(document.getElementById("exptrPrsnlRciptForm"));
                        commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                            Swal.fire({ text: res.message })
                                .then(function() {
                                    if (res.rslt) commons.util.blockUIReload();
                                });
                        });
                    }
                });
            }
        }
    })();
</script>

<#--begin::Card body-->
<div id="exptrPrsnlRciptTemplate" hidden>
    <#--begin::Form-->
    <form name="exptrPrsnlRciptForm" id="exptrPrsnlRciptForm" class="form" enctype="multipart/form-data">
        <input type="file" name="atchFile0" id="atchFile0" class="file"
               accept=".png, .jpg, .jpeg" />
        <input type="hidden" name="exptrPrsnlItemNo" id="exptrPrsnlItemNo_upload">
    </form>
</div>