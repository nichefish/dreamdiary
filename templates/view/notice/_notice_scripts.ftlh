<#--
 * 공지사항(notice) : 스크립트 모듈 분리
 -->

<script>
    const Notice = (function() {
        return {
            /** form validation init */
            initValidateForm: function () {
                /* jquery :: validation */
                $("#noticeRegForm").validate({
                    submitHandler: function () {
                        if (Notice.submitMode === "preview") {
                            const popupNm = "preview";
                            const options = 'width=1280,height=1440,top=0,left=270';
                            commons.util.popup("", popupNm, options);
                            let popupUrl = "${Url.NOTICE_REG_PREVIEW_POP!}";
                            $("#noticeRegForm").attr("action", popupUrl).attr("target", popupNm);
                            return true;
                        } else if (Notice.submitMode === "submit") {
                            $("#noticeRegForm").removeAttr("action");
                            Swal.fire({
                                text: <#if isMdf!false><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                                showCancelButton: true,
                            }).then(function (result) {
                                if (!result.value) return;
                                Notice.regAjax();
                            });
                        }
                    },
                    errorPlacement: function (error, element) {
                        commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                    },
                    ignore: [],         // hidden 필드도 검증하기 위함
                });
            },
            /** 목록 검색 */
            search: function () {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.NOTICE_LIST!}?actionTyCd=SEARCH");
            },
            /** 내가 작성한 글 보기 */
            myPapr: function () {
                const url = "${Url.NOTICE_LIST!}";
                let param = "?searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },
            /** 엑셀 다운로드 */
            xlsxDownload: function () {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${Url.NOTICE_LIST_XLSX_DOWNLOAD!}").submit();
                });
            },
            /** 등록 화면 이동 */
            regForm: function () {
                commons.util.blockUISubmit("#procForm", "${Url.NOTICE_REG_FORM!}");
            },
            /** submit */
            submit: function () {
                if (tinymce !== undefined) tinymce.activeEditor.save();
                Notice.submitMode = "submit";
                $("#noticeRegForm").submit();
            },
            /** 미리보기 팝업 호출 */
            preview: function () {
                if (tinymce !== undefined) tinymce.activeEditor.save();
                Notice.submitMode = "preview";
                $("#noticeRegForm").submit();
            },
            /** 등록/수정 처리(Ajax) */
            regAjax: function () {
                const url = " <#if isMdf!false>${Url.NOTICE_MDF_AJAX!}<#else>${Url.NOTICE_REG_AJAX!}</#if>";
                const ajaxData = new FormData($("#noticeRegForm")[0]);
                commons.util.blockUIFileAjax(url, ajaxData, function (res) {
                    Swal.fire({text: res.message})
                        .then(function () {
                            if (res.rslt) Notice.list();
                        });
                }, "block");
            },
            /** 상세 화면 이동 */
            dtl: function (postNo) {
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${Url.NOTICE_DTL!}");
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function (postNo) {
                event.stopPropagation();
                const url = "${Url.NOTICE_DTL_AJAX!}";
                const ajaxData = {"postNo": postNo};
                commons.util.blockUIAjax(url, 'GET', ajaxData, function (res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({text: res.message});
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "notice_dtl", "show");
                });
            },
            /** 수정 화면 이동 */
            mdfForm: function () {
                commons.util.blockUISubmit("#procForm", "${Url.NOTICE_MDF_FORM!}");
            },
            /** 삭제 처리 (ajax) */
            delAjax: function () {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${Url.NOTICE_DEL_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function (res) {
                        Swal.fire({text: res.message})
                            .then(function () {
                                if (res.rslt) Notice.list();
                            });
                    }, "block");
                });
            },
            /** 목록 화면 이동 */
            list: function () {
                const listUrl = "${Url.NOTICE_LIST!}<#if isMdf!false>?isBackToList=Y</#if>";
                commons.util.blockUIReplace(listUrl);
            }
        }
    })();
</script>