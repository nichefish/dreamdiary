<#--
 * 경비지출서(exptr_prsnl_papr) : 스크립트 모듈 분리
 -->

<script>
    const ExptrPrsnlPapr = (function() {
        return {
            /** form validation init */
            initValidateForm: function () {
                /* jquery :: validation */
                $("#exptrPrsnlRegForm").validate({
                    submitHandler: function () {
                        Swal.fire({
                            text: <#if isMdf!false><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                            showCancelButton: true,
                        }).then(function(result) {
                            if (!result.value) return;
                            // disabled된 selectbox 값을 풀어준다.
                            $("input").removeAttr("disabled");
                            $("select").removeAttr("disabled");
                            ExptrPrsnlPapr.regAjax();
                        });
                    },
                    errorPlacement: function (error, element) {
                        commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                    },
                    ignore: []         // hidden 필드도 검증하기 위함
                });
            },
            /** 목록 검색 */
            search: function () {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.EXPTR_PRSNL_PAPR_LIST!}?actionTyCd=SEARCH");
            },
            /** 내가 작성한 글 보기 */
            myPapr: function () {
                const url = "${Url.EXPTR_PRSNL_PAPR_LIST!}";
                let param = "?searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },
            /** 개별 영수증 PDF 취합 다운로드 */
            pdfDownload: function(postNo) {
                event.stopPropagation();
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#postNo").val(postNo);
                    $("#procForm").attr("action", "${Url.EXPTR_PRSNL_PAPR_RCIPT_PDF_DOWNLOAD!}").submit();
                });
            },
            /** 등록 전 점검 + 등록 화면 이동 (Ajax) */
            regForm: function() {
                $("#prevYn").val("N");
                const url = "${Url.EXPTR_PRSNL_PAPR_EXISTING_CHCK_AJAX!}";
                commons.util.blockUIAjax(url, 'post', "", function(res) {
                    if (!res.rsltMap) return;
                    // 중복 코드 감소를 위한 함수 정의
                    const checkAndRedirect = (data) => {
                        if (monthData && monthData.cfYn !== "Y") {
                            const message = "이미 등록중인 글(" + monthData.yy + "년 " + monthData.mnth + " +월)이 있습니다. 해당 글로 이동하시겠습니까?";
                            if (confirm(message)) {
                                $("#postNo").val(monthData.postNo);
                                commons.util.blockUISubmit("#procForm", "${Url.EXPTR_PRSNL_PAPR_MDF_FORM!}");
                                return true;
                            }
                        }
                        return false;
                    };

                    let {prevMnth, currMnth} = res.rsltMap;
                    // 확인된 결과에 따라 처리
                    if (prevMnth?.cfYn === "Y" && currMnth?.cfYn === "Y") {
                        alert("<@spring.message 'view.post.prsnl.alreadyCompt'/>");
                        return;
                    }
                    if (checkAndRedirect(prevMnth, 'prev') || checkAndRedirect(currMnth, 'curr')) return;

                    // 여기까지 실행됐다는 것은 둘 다 진행중인 글이 없다는 것을 의미
                    if (!prevMnth || !currMnth) {
                        $("#prevYn").val(prevMnth ? "N" : "Y");
                        commons.util.blockUISubmit("#procForm", "${Url.EXPTR_PRSNL_PAPR_REG_FORM!}");
                    } else {
                        alert("경비지출서는 각 월당 한 개만 작성할 수 있습니다.");
                    }
                });
            },
            /** submit */
            submit: function() {
                $("#exptrPrsnlRegForm").submit();
            },
            /** 등록/수정 처리(Ajax) */
            regAjax: function() {
                // DTO에서 integer로 받으므로 콤마 처리되어 있던 값 수동으로 처리
                $("input[name$='.exptrAmtStr']").each(function(idx, item) {
                    let index = $(item).attr("id").replace("exptrAmt", "");
                    $("input[name='itemList["+index+"].exptrAmt']").val($(item).val().replace(/,/gi, ""));
                });
                let ajaxData = new FormData($("#exptrPrsnlRegForm")[0]);
                const url = "<#if isReg!false>${Url.EXPTR_PRSNL_PAPR_REG_AJAX!}</#if><#if isMdf!false>${Url.EXPTR_PRSNL_PAPR_MDF_AJAX!}</#if>";
                commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (!res.rslt) return;
                            if ($("#mngrYn").val() === "Y") {
                                commons.util.blockUIReplace("${Url.EXPTR_PRSNL_STATS_PAGE!}")
                            } else
                                ExptrPrsnlPapr.list();
                        });
                });
            },
            /** 상세 화면 이동 */
            dtl: function (postNo) {
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${Url.EXPTR_PRSNL_PAPR_DTL!}");
            },
            /** 수정 화면 이동 */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.EXPTR_PRSNL_PAPR_MDF_FORM!}");
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.EXPTR_PRSNL_PAPR_DEL_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) ExptrPrsnlPapr.list();
                            });
                    });
                });
            },
            /** 목록 화면 이동 */
            list: function() {
                const listUrl = "${Url.EXPTR_PRSNL_PAPR_LIST!}?isBackToList=Y";
                commons.util.blockUIReplace(listUrl);
            },
            /** 이전 화면 이동 */
            stats: function() {
                commons.util.blockUIReplace("${Url.EXPTR_PRSNL_STATS_PAGE!}");
            }
        }
    })();
</script>