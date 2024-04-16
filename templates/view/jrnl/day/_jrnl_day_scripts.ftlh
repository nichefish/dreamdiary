<#--
  -- 저널 일자(jrnl_day) : 모듈 스크립트 분리
  -->

<script>
    const JrnlDay = (function() {
        return {
            /** 초기화 로직 */
            init: function () {
                JrnlDay.listAjax();
            },
            /** 등록 화면 호출 */
            regModal: function() {
                commons.util.handlebarsTemplate({}, "jrnl_day_reg", "show");
                // commons.util.chckboxLabel("useYn", "사용//미사용", "blue//gray");
                commons.datepicker.singleDatePicker("#jrnlDt", "yyyy-MM-DD", commons.date.getCurrDateStr("yyyy-MM-dd"));         // datepicker 날짜 검색 init
                commons.validate.replaceBlankRegex("#jrnlDayRegForm #clCd");
                commons.validate.toUpperCase("jrnlDayRegForm #clCd");
            },
            /** 목록 조회 */
            listAjax: function() {
                const url = "${Url.JRNL_DAY_LIST_AJAX!}";
                let ajaxData = {};
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltList, "jrnl_day_list");
                });
            },
            /** 등록 */
            regAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.reg"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_DAY_REG_AJAX!}";
                    let ajaxData = new FormData($("#jrnlDayRegForm")[0]);
                    commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();;
                            });
                    });
                });
            },
            /** 등록 화면 호출 */
            mdfModal: function(postNo) {
                const url = "${Url.JRNL_DAY_DTL_AJAX!}";
                let ajaxData = { "postNo" : postNo };
                commons.util.blockUIAjax(url, "get", ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (!res.rslt) {
                                if (commons.util.isNotEmpty(res.message)) Swal.fire({text: res.message});
                                return false;
                            }
                            commons.util.handlebarsTemplate(res.rsltObj, "jrnl_day_reg", "show");
                            commons.datepicker.singleDatePicker("#jrnlDt", "yyyy-MM-DD", commons.date.getCurrDateStr("yyyy-MM-dd"));         // datepicker 날짜 검색 init
                            commons.validate.replaceBlankRegex("#jrnlDayRegForm #clCd");
                            commons.validate.toUpperCase("jrnlDayRegForm #clCd");
                        });
                });
            },
            /** 삭제 */
            delAjax: function(postNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_DAY_DEL_AJAX!}";
                    let ajaxData = { "postNo": postNo };
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();;
                            });
                    });
                });
            },
        }
    })();
</script>