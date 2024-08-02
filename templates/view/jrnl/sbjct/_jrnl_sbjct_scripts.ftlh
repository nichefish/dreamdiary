<#--
 * 저널 주제(jrnl_sbjct) : 스크립트 모듈 분리
 -->

<script>
    const JrnlSbjct = (function() {
        return {
            /** form validation init */
            initValidateForm: function () {
                /* jquery :: validation */
                $("#jrnlSbjctRegForm").validate({
                    submitHandler: function () {
                        if (JrnlSbjct.submitMode === "preview") {
                            const popupNm = "preview";
                            const options = 'width=1280,height=1440,top=0,left=270';
                            commons.util.popup("", popupNm, options);
                            let popupUrl = "${Url.JRNL_SBJCT_REG_PREVIEW_POP!}";
                            $("#jrnlSbjctRegForm").attr("action", popupUrl).attr("target", popupNm);
                            return true;
                        } else if (JrnlSbjct.submitMode === "submit") {
                            $("#jrnlSbjctRegForm").removeAttr("action");
                            Swal.fire({
                                text: <#if isMdf!false><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                                showCancelButton: true,
                            }).then(function (result) {
                                if (!result.value) return;
                                JrnlSbjct.regAjax();
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
                commons.util.blockUISubmit("#listForm", "${Url.JRNL_SBJCT_LIST!}?actionTyCd=SEARCH");
            },
            /** 내가 작성한 글 보기 */
            myPapr: function () {
                const url = "${Url.JRNL_SBJCT_LIST!}";
                let param = "?searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },
            /** 등록 화면 이동 */
            regForm: function () {
                commons.util.blockUISubmit("#procForm", "${Url.JRNL_SBJCT_REG_FORM!}");
            },
            /** submit */
            submit: function () {
                if (tinymce !== undefined) tinymce.activeEditor.save();
                JrnlSbjct.submitMode = "submit";
                $("#jrnlSbjctRegForm").submit();
            },
            /** 미리보기 팝업 호출 */
            preview: function () {
                if (tinymce !== undefined) tinymce.activeEditor.save();
                JrnlSbjct.submitMode = "preview";
                $("#jrnlSbjctRegForm").submit();
            },
            /** 등록/수정 처리(Ajax) */
            regAjax: function () {
                let ajaxData = new FormData($("#jrnlSbjctRegForm")[0]);
                const url = " <#if isMdf!false>${Url.JRNL_SBJCT_MDF_AJAX!}<#else>${Url.JRNL_SBJCT_REG_AJAX!}</#if>";
                commons.util.blockUIFileAjax(url, ajaxData, function (res) {
                    Swal.fire({text: res.message})
                        .then(function () {
                            if (res.rslt) JrnlSbjct.list();
                        });
                }, "block");
            },
            /** 상세 화면 이동 */
            dtl: function (postNo) {
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${Url.JRNL_SBJCT_DTL!}");
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function (postNo) {
                event.stopPropagation();
                const url = "${Url.JRNL_SBJCT_DTL_AJAX!}";
                let ajaxData = {"postNo": postNo};
                commons.util.blockUIAjax(url, 'get', ajaxData, function (res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({text: res.message});
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "jrnlSbjct_dtl", "show");
                });
            },
            /** 수정 화면 이동 */
            mdfForm: function () {
                commons.util.blockUISubmit("#procForm", "${Url.JRNL_SBJCT_MDF_FORM!}");
            },
            /** 삭제 처리 (ajax) */
            delAjax: function () {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function (result) {
                    if (!result.value) return;
                    const url = "${Url.JRNL_SBJCT_DEL_AJAX!}";
                    let ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'post', ajaxData, function (res) {
                        Swal.fire({text: res.message})
                            .then(function () {
                                if (res.rslt) JrnlSbjct.list();
                            });
                    }, "block");
                });
            },
            /** 목록 화면 이동 */
            list: function () {
                const listUrl = "${Url.JRNL_SBJCT_LIST!}<#if isMdf!false>?isBackToList=Y</#if>";
                commons.util.blockUIReplace(listUrl);
            }
        }
    })();
</script>