<#--
 * 공지사항(notice) : 스크립트 모듈 분리
 -->

<script>
    const Notice = (function() {
        return {
            /**
             * form init
             * @param {Object} obj - 폼에 바인딩할 데이터
             */
            initForm: function(obj = {}) {
                /* jquery validation */
                commons.validate.validateForm("#noticeRegForm", Notice.submitHandler);
                /* tinymce init */
                commons.tinymce.init("#tinymce_cn");
                /* tagify */
                commons.tagify.initWithCtgr("#noticeRegForm #tagListStr");
                // 잔디발송여부 클릭시 글씨 변경
                commons.util.chckboxLabel("jandiYn", "발송//미발송", "blue//gray", function() {
                    $("#trgetTopicSpan").show();
                }, function() {
                    $("#trgetTopicSpan").hide();
                });
            },

            /**
             * Custom SubmitHandler
             */
            submitHandler: function() {
                if (Notice.submitMode === "preview") {
                    const popupNm = "preview";
                    const options = 'width=1280,height=1440,top=0,left=270';
                    const popup = commons.util.openPopup("", popupNm, options);
                    if (popup) popup.focus();
                    const popupUrl = "${Url.NOTICE_REG_PREVIEW_POP!}";
                    $("#noticeRegForm").attr("action", popupUrl).attr("target", popupNm);
                    return true;
                } else if (Notice.submitMode === "submit") {
                    $("#noticeRegForm").removeAttr("action");
                    Swal.fire({
                        text: <#if isMdf!false><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                        showCancelButton: true,
                    }).then(function(result) {
                        if (!result.value) return;
                        Notice.regAjax();
                    });
                }
            },

            /**
             * 목록 검색
             */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.NOTICE_LIST!}?actionTyCd=SEARCH");
            },

            /**
             * 내가 작성한 글 목록 보기
             */
            myPaprList: function() {
                const url = "${Url.NOTICE_LIST!}";
                let param = "?searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },

            /**
             * 엑셀 다운로드
             */
            xlsxDownload: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.download"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    commons.util.blockUIFileDownload();
                    $("#listForm").attr("action", "${Url.NOTICE_LIST_XLSX_DOWNLOAD!}").submit();
                });
            },

            /**
             * 등록 화면 이동
             */
            regForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.NOTICE_REG_FORM!}");
            },

            /**
             * form submit
             */
            submit: function() {
                if (tinymce !== undefined) tinymce.activeEditor.save();
                Notice.submitMode = "submit";
                $("#noticeRegForm").submit();
            },

            /**
             * 미리보기 팝업 호출
             */
            preview: function() {
                if (tinymce !== undefined) tinymce.activeEditor.save();
                Notice.submitMode = "preview";
                $("#noticeRegForm").submit();
            },

            /**
             * 등록/수정 처리(Ajax)
             */
            regAjax: function() {
                const url = "<#if isMdf!false>${Url.NOTICE_MDF_AJAX!}<#else>${Url.NOTICE_REG_AJAX!}</#if>";
                const ajaxData = new FormData(document.getElementById("noticeRegForm"));
                commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                    Swal.fire({text: res.message})
                        .then(function() {
                            if (res.rslt) Notice.list();
                        });
                }, "block");
            },

            /**
             * 상세 화면으로 이동
             * @param {string|number} postNo - 조회할 글 번호.
             */
            dtl: function(postNo) {
                if (isNaN(postNo)) return;

                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${Url.NOTICE_DTL!}");
            },

            /**
             * 상세 모달 호출
             * @param {string|number} postNo - 조회할 글 번호.
             */
            dtlModal: function(postNo) {
                event.stopPropagation();
                if (isNaN(postNo)) return;

                const url = "${Url.NOTICE_DTL_AJAX!}";
                const ajaxData = {"postNo": postNo};
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({text: res.message});
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "notice_dtl", "show");
                });
            },

            /**
             * 수정 화면 이동
             */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.NOTICE_MDF_FORM!}");
            },

            /**
             * 삭제 (Ajax)
             * @param {string|number} postNo - 글 번호.
             */
            delAjax: function(postNo) {
                if (isNaN(postNo)) return;

                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.NOTICE_DEL_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({text: res.message})
                            .then(function() {
                                if (res.rslt) Notice.list();
                            });
                    }, "block");
                });
            },

            /**
             * 목록 화면으로 이동
             */
            list: function() {
                const listUrl = "${Url.NOTICE_LIST!}<#if isMdf!false>?isBackToList=Y</#if>";
                commons.util.blockUIReplace(listUrl);
            }
        }
    })();
</script>