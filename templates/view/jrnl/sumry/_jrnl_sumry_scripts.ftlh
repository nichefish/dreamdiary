<#--
 * 저널 결산(jrnl_sumry) : 스크립트 모듈 분리
 -->

<script>
    const JrnlSumry = (function() {
        return {
            /** 등록 화면 호출 */
            makeSumryAjax: function(yy) {
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
        }
    })();
</script>