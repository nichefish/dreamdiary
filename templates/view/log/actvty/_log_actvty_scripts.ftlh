<#--
 * 활동 로그(log_actvty) : 스크립트 모듈 분리
 -->

<script>
    const LogActvty = (function() {
        return {
            /** 목록 검색 */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.LOG_ACTVTY_LIST!}?actionTyCd=SEARCH");
            },
            /** 엑셀 다운로드 */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${Url.LOG_ACTVTY_LIST_XLSX_DOWNLOAD!}").submit();
                });
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function (logActvtyNo) {
                event.stopPropagation();
                const url = "${Url.LOG_ACTVTY_DTL_AJAX!}";
                let ajaxData = { "logActvtyNo": logActvtyNo };
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "log_actvty_dtl", "show");
                });
            },
        }
    })();
</script>