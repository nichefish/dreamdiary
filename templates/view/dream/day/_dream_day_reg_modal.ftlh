<#--
  -- 분류 코드(CL_CD) > 상세 코드(DTL_CD) 등록/수정 Modal
  -- @included_in : cl_cd_dtl
  -->
<script>
    const DreamDay = (function() {
        return {
            /** 초기화 로직 */
            init: function () {
                DreamDay.listAjax();
            },
            /** 등록 화면 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "dream_day_reg", "show");
                // commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.datepicker.singleDatePicker("#dreamtDt", "yyyy-MM-DD", commons.date.getCurrDateStr("yyyy-MM-dd"));         // datepicker 날짜 검색 init
                commons.validate.replaceBlankRegex("#dreamDayRegForm #clCd");
                commons.validate.toUpperCase("dreamDayRegForm #clCd");
            },
            /** 목록 조회 */
            listAjax: function() {
                const url = "${ApiUrl.API_DREAM_DAY_LIST_AJAX!}";
                let ajaxData = {};
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.resultList, "dream_day_list");
                });
            },
            /** 등록 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.DREAM_DAY_REG_AJAX!}";
                    let ajaxData = new FormData($("#dreamDayRegForm")[0]);
                    commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();;
                            });
                    });
                });
            },
            /** 등록 화면 호출 */
            mdfModal: function(postNo) {
                const url = "${SiteUrl.DREAM_DAY_DTL_AJAX!}";
                let ajaxData = { "postNo" : postNo };
                commons.util.blockUIFileAjax(url, "get", ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (!res.result) {
                                if (commons.util.isNotEmpty(res.message)) Swal.fire({text: res.message});
                                return false;
                            }
                            commons.util.handlebarsTemplate(res.resultObj, "dream_day_reg", "show");
                            commons.datepicker.singleDatePicker("#dreamtDt", "yyyy-MM-DD", commons.date.getCurrDateStr("yyyy-MM-dd"));         // datepicker 날짜 검색 init
                            commons.validate.replaceBlankRegex("#dreamDayRegForm #clCd");
                            commons.validate.toUpperCase("dreamDayRegForm #clCd");
                        });
                });
            },
        }
    })();
    /** 페이지 초기화시 처리 */
    $(document).ready(function() {
        DreamDay.init();
    });
</script>

<div class="modal fade" id="dream_day_reg_modal" tabindex="-1" role="dialog" aria-labelledby="dreamDayRegModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="dreamDayRegModalLongTitle">[꿈 일자 등록]</h5>
                <div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <form name="dreamDayRegForm" id="dreamDayRegForm" class="form" enctype="multipart/form-data">
                        <div id="dream_day_reg_div">
                            <!-- contents -->
                        </div>
                    </form>
                    <div class="d-flex justify-content-end">
                        <button type="button" class="btn btn-sm btn-primary me-2"
                                onclick="DreamDay.regAjax();"
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
<#include "/view/dream/day/_dream_day_reg_template.hbs">