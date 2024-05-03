<#--
 * 저널 결산(jrnl_sumry) : 스크립트 모듈 분리
 -->

<script>
    const JrnlSumry = (function() {
        return {
            /** 상세 화면 이동 */
            dtl: function (postNo) {
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${Url.JRNL_SUMRY_DTL!}");
            },
            /** 특정 년도 결산 생성 */
            makeYySumryAjax: function(yy) {
                if (yy === undefined) yy = $("#listForm #yy").val();
                if (commons.util.isEmpty(yy)) {
                    alert("yy는 필수 항목입니다.");
                    return false;
                }
                const url = "${Url.JRNL_SUMRY_MAKE_AJAX!}";
                const ajaxData = { "yy": yy };
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                });
            },
            /** 전체 년도 결산 갱신 */
            makeTotalSumryAjax: function() {
                const url = "${Url.JRNL_SUMRY_MAKE_TOTAL_AJAX!}";
                commons.util.blockUIAjax(url, 'post', null, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                });
            },
        }
    })();
</script>