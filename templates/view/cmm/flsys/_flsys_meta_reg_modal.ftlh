<#--
  -- 활동 로그 > 상세 Modal
  -- @included_in : log_actvty_list
  -->
<script>
    const Meta = function() {
        return {
            init: function() {
                /** jquery :: validation */
                $(function () {
                    $("#flsysMetaRegForm").validate({
                        submitHandler: function () {
                            Meta.regAjax();
                        },
                        errorPlacement: function (error, element) {
                            commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                        }
                    });
                });
            },
            /** 등록 모달 조회 */
            regForm: function(filePath) {
                let obj = { "filePath": filePath, "upperFilePath": Flsys.currPath };
                commons.util.handlebarsTemplate(obj, "flsys_meta_reg", "show");

                /** tagify */
                let tagInput = document.querySelector("#tagListStr");
                new Tagify(tagInput, {
                    maxTags: 5,
                    // pattern: commons.validate.ipv4regex,
                    // 특수문자 제외?몇몇개만 허용?
                    keepInvalidTags: false,
                    skipInvalid: true,
                    duplicates: false,
                    editTags: {
                        clicks: 2,
                        keepInvalid: false      // if after editing, tag is invalid, auto-revert
                    },
                });
            },
            /** 메타정보 등록 (Ajax) */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.FLSYS_META_REG_AJAX!}";
                    let ajaxData = $("#flsysMetaRegForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) Flsys.flsysRefreshAjax();
                            });
                    });
                });
            },
            /** 메타정보 상세 조회 (Ajax) */
            dtlAjax: function(postNo) {
                const url = "${SiteUrl.FLSYS_META_DTL_AJAX!}";
                let ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "flsys_meta_dtl_btn");
                    commons.util.handlebarsTemplate(res.rsltObj, "flsys_meta_dtl", "show");
                });
            },
            /** 등록 모달 조회 */
            mdfForm: function(postNo) {
                const url = "${SiteUrl.FLSYS_META_DTL_AJAX!}";
                let ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let rsltObj = res.rsltObj;
                    $("#flsys_meta_dtl_modal").modal("hide");
                    commons.util.handlebarsTemplate(rsltObj, "flsys_meta_reg", "show");

                    /** tagify */
                    let tagInput = document.querySelector("#tagListStr");
                    let tagStrList = rsltObj.tagStrList;
                    if (rsltObj.tagStrList !== undefined && rsltObj.tagStrList.length > 0) {
                        let tagifyStr = "";
                        tagStrList.forEach(function(item) {
                            tagifyStr += item + ",";
                        });
                        tagifyStr.replace(/,$/, '');     // 마지막에 쉼표가 있을 경우 제거
                        tagInput.value = tagifyStr;
                    }
                    new Tagify(tagInput, {
                        maxTags: 5,
                        // pattern: commons.validate.ipv4regex,
                        // 특수문자 제외?몇몇개만 허용?
                        keepInvalidTags: false,
                        skipInvalid: true,
                        duplicates: false,
                        editTags: {
                            clicks: 2,
                            keepInvalid: false      // if after editing, tag is invalid, auto-revert
                        },
                    });
                    $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
                });
            },
            /** 메타정보 삭제 (Ajax) */
            delAjax: function(postNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.FLSYS_META_DEL_AJAX!}";
                    let ajaxData = { "postNo": postNo };
                    commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        if (res.rslt) Flsys.flsysListAjax(Flsys.currPath);
                    });
                });
            },
        }
    }();
    $(document).ready(function() {
        Meta.init();
    });
</script>
<div class="modal fade" id="flsys_meta_reg_modal" tabindex="-1" role="dialog" aria-labelledby="flsysMetaRegModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="flsysMetaRegModalLongTitle">[메타 정보 저장]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <form class="form" name="flsysMetaRegForm" id="flsysMetaRegForm">
                        <div id="flsys_meta_reg_div">
                            <!-- contents -->
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
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
                <button type="button" class="btn btn-sm btn-light" data-bs-dismiss="modal">닫기</button>
            </div>
        </div>
    </div>
</div>
<!-- include:: Handlebars template -->
<#include "/view/cmm/flsys/_flsys_meta_reg_template.hbs">