<#--
 * 게시판(board_post) : 스크립트 모듈 분리
 -->

<script>
    const BoardPost = (function() {
        return {
            /** form validation init */
            initValidateForm: function() {
                /* jquery :: validation */
                $("#postRegForm").validate({
                    submitHandler: function () {
                        if (Page.submitMode === "preview") {
                            const popupNm = "preview";
                            const options = 'width=1280,height=1440,top=0,left=270';
                            const popup = commons.util.openPopup("", popupNm, options);
                            if (popup) popup.focus();
                            const popupUrl = "${Url.BOARD_POST_REG_PREVIEW_POP!}";
                            $("#postRegForm").attr("action", popupUrl).attr("target", popupNm);
                            return true;
                        } else if (Page.submitMode === "submit") {
                            $("#postRegForm").removeAttr("action");
                            Swal.fire({
                                text: <#if isMdf!false><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                                showCancelButton: true,
                            }).then(function(result) {
                                if (!result.value) return;
                                BoardPost.regAjax();
                            });
                        }
                    },
                    errorPlacement: function(error, element) {
                        commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                    },
                    ignore: [],         // hidden 필드도 검증하기 위함
                });
            },
            /** 목록 검색 */
            search: function () {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.BOARD_POST_LIST!}?actionTyCd=SEARCH");
            },
            /** 내가 작성한 글 보기 */
            myPapr: function () {
                let boardCd = $("#boardCd").val();
                const url = "${Url.BOARD_POST_LIST!}";
                let param = "?boardCd="+boardCd+"&searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },
            /** 등록 화면 이동 */
            regForm: function () {
                commons.util.blockUISubmit("#procForm", "${Url.BOARD_POST_REG_FORM!}");
            },
            /** submit */
            submit: function() {
                tinymce.activeEditor.save();
                Page.submitMode = "submit";
                $("#postRegForm").submit();
            },
            /** 미리보기 팝업 호출 */
            preview: function() {
                tinymce.activeEditor.save();
                Page.submitMode = "preview";
                $("#postRegForm").submit();
            },
            /** 등록/수정 처리(Ajax) */
            regAjax: function() {
                const url = "<#if isMdf!false>${Url.BOARD_POST_MDF_AJAX!}<#else>${Url.BOARD_POST_REG_AJAX!}</#if>";
                const ajaxData = new FormData($("#postRegForm")[0]);
                commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) BoardPost.list();
                        });
                }, "block");
            },
            /** 상세 화면 이동 */
            dtl: function (postNo) {
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${Url.BOARD_POST_DTL!}");
            },
            /** 상세 모달 팝업 호출 */
            dtlAjax: function (postNo) {
                event.stopPropagation();
                const url = "${Url.BOARD_POST_DTL_AJAX!}";
                const ajaxData = { "postNo": postNo, "boardCd": $("#boardCd").val() };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "board_post_dtl", "show");
                });
            },
            /** 수정 화면 이동 */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.BOARD_POST_MDF_FORM!}");
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.BOARD_POST_DEL_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) BoardPost.list();
                            });
                    }, "block");
                });
            },
            /** 목록 화면 이동 */
            list: function() {
                const listUrl = "${Url.BOARD_POST_LIST!}?boardCd=${boardCd!}<#if isMdf!false>&isBackToList=Y</#if>";
                commons.util.blockUIReplace(listUrl);
            }
        }
    })();
</script>