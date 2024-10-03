<#--
 * 휴가계획서(vcatn_papr) : 스크립트 모듈 분리
 -->

<script>
    const VcatnPapr = (function() {
        return {
            /**
             * form validation init
             */
            initValidateForm: function() {
                /* jquery :: validation */
                $("#vcatnPaprRegForm").validate({
                    ...commons.validate.baseOptions,
                    submitHandler: function() {
                        Swal.fire({
                            text: <#if isMdf!false><@spring.message "view.cnfm.mdf"/><#else><@spring.message "view.cnfm.reg"/></#if>,
                            showCancelButton: true,
                        }).then(function(result) {
                            if (!result.value) return;
                            VcatnPapr.regAjax();
                        });
                    },
                });
            },

            /**
             * 목록 검색
             */
            search: function() {
                $("#listForm #pageNo").val(1);
                commons.util.blockUISubmit("#listForm", "${Url.VCATN_PAPR_LIST!}?actionTyCd=SEARCH");
            },

            /**
             * 내가 작성한 글 목록 보기
             */
            myPapr: function() {
                const url = "${Url.VCATN_PAPR_LIST!}";
                let param = "?searchType=nickNm&searchKeyword=${authInfo.nickNm!}&regstrId=${authInfo.userId!}&pageSize=50&actionTyCd=MY_PAPR";
                commons.util.blockUIReplace(url + param);
            },

            /**
             * 등록 화면으로 이동
             */
            regForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.VCATN_PAPR_REG_FORM!}");
            },

            /**
             * submit
             */
            submit: function() {
                $("#vcatnPaprRegForm").submit();
            },

            /**
             * 등록/수정 처리(Ajax)
             */
            regAjax: function() {
                const url = "<#if isReg!false>${Url.VCATN_PAPR_REG_AJAX!}</#if><#if isMdf!false>${Url.VCATN_PAPR_MDF_AJAX!}</#if>";
                const ajaxData = new FormData($("#vcatnPaprRegForm")[0]);
                commons.util.blockUIMultipartAjax(url, ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) VcatnPapr.list();
                        });
                }, "block");
            },

            /**
             * 상세 화면으로 이동
             */
            dtl: function (postNo) {
                $("#procForm #postNo").val(postNo);
                commons.util.blockUISubmit("#procForm", "${Url.VCATN_PAPR_DTL!}");
            },

            /**
             * 상세 모달 호출
             */
            dtlAjax: function (postNo) {
                event.stopPropagation();
                const url = "${Url.VCATN_PAPR_DTL_AJAX!}";
                const ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'GET', ajaxData, function(res) {
                    if (!res.rslt) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.rsltObj, "vcatn_papr_dtl", "show");
                });
            },

            /**
             * 수정 화면으로 이동
             */
            mdfForm: function() {
                commons.util.blockUISubmit("#procForm", "${Url.VCATN_PAPR_MDF_FORM!}");
            },

            /** 관리자 확인 여부 변경 (Ajax) */
            cfAjax: function() {
                const url = "${Url.VCATN_PAPR_CF_AJAX!}";
                const ajaxData = $("#procForm").serializeArray();
                commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.rslt) commons.util.blockUIReload();
                        });
                }, "block");
            },

            /**
             * 삭제 처리 (ajax)
             */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.VCATN_PAPR_DEL_AJAX!}";
                    const ajaxData = $("#procForm").serializeArray();
                    commons.util.blockUIAjax(url, 'POST', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.rslt) VcatnPapr.list();
                            });
                    }, "block");
                });
            },

            /**
             * 목록 화면으로 이동
             */
            list: function() {
                commons.util.blockUIReplace("${Url.VCATN_PAPR_LIST!}?isBackToList=Y");
            }
        }
    })();
</script>