<#--
 * 물품구매/경조사비 신청(exptr_reqst) : 스크립트 모듈 분리
 -->

<script>
    const ExptrReqst = (function() {
        return {
            /**
             * form validation init
             */
            initValidateForm: function() {
                /* jquery :: validation */
                $("#exptrReqstRegForm").validate({
                    submitHandler: function() {
                        if (ExptrReqst.submitMode === "preview") {
                            const popupNm = "preview";
                            const options = 'width=1280,height=1440,top=0,left=270';
                            const popup = commons.util.openPopup("", popupNm, options);
                            if (popup) popup.focus();
                            const popupUrl = "${Url.EXPTR_REQST_REG_PREVIEW_POP!}";
                            $("#exptrReqstRegForm").attr("action", popupUrl).attr("target", popupNm);
                            return true;
                        } else if (ExptrReqst.submitMode === "submit") {
                            $("#exptrReqstRegForm").removeAttr("action");
                            Swal.fire({
                                text: <#if isMdf!false><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                                showCancelButton: true,
                            }).then(function(result) {
                                if (!result.value) return;
                                ExptrReqst.regAjax();
                            });
                        }
                    },
                    errorPlacement : function(error, element) {
                        commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                    },
                    ignore: [],         // hidden 필드도 검증하기 위함
                });
            },
            /** 목록 검색 */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.EXPTR_REQST_LIST!}?actionTyCd=SEARCH");
            },
            /** 내가 작성한 글 목록 보기 */
            myPapr: function() {
                const url = "${Url.EXPTR_REQST_LIST!}";
                let param = "?searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },
            /** 등록 화면 이동 */
            regForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.EXPTR_REQST_REG_FORM!}");
            },
            /**
             * submit
             */
            submit: function() {
                tinymce.activeEditor.save();
                Page.submitMode = "submit";
                $("#exptrReqstRegForm").submit();
            },
            /** 미리보기 팝업 호출 */
            preview: function() {
                tinymce.activeEditor.save();
                Page.submitMode = "preview";
                $("#exptrReqstRegForm").submit();
            },
            /** 등록/수정 처리(Ajax) */
            regAjax: function() {
                const url = "<#if isReg!false>${Url.EXPTR_REQST_REG_AJAX!}</#if><#if isMdf!false>${Url.EXPTR_REQST_MDF_AJAX!}</#if>";
                const ajaxData = new FormData($("#exptrReqstRegForm")[0]);
                commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) ExptrReqst.list();
                        });
                }, "block");
            },
            /** 상세 화면 이동 */
            dtl: function (postNo) {
                commons.util.blockUIRequest();
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${Url.EXPTR_REQST_DTL!}");
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function (postNo) {
                event.stopPropagation();
                const url = "${Url.EXPTR_REQST_DTL_AJAX!}";
                const ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "exptr_reqst_dtl", "show");
                });
            },
            /** 관리자 확인 여부 변경 (Ajax) */
            cfAjax: function() {
                Swal.fire({
                    text: "완료 처리하시겠습니까?",
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${Url.EXPTR_REQST_CF_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 항목 미처리 (Ajax) */
            dismissAjax: function() {
                Swal.fire({
                    text: "취소(기각) 처리하시겠습니까?",
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${Url.EXPTR_REQST_DISMISS_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) commons.util.blockUIReload();
                            });
                    }, "block");
                });
            },
            /** 수정 화면 이동 */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.EXPTR_REQST_MDF_FORM!}");
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.EXPTR_REQST_DEL_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) ExptrReqst.list();
                            });
                    }, "block");
                });
            },
            /** 목록 화면으로 이동 */
            list: function() {
                const listUrl = "${Url.EXPTR_REQST_LIST!}<#if isMdf!false>?isBackToList=Y</#if>";
                commons.util.blockUIReplace(listUrl);
            }
        }
    })();
</script>