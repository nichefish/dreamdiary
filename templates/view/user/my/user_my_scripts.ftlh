<#--
 * 내 정보(user_my) > 스크립트 모듈 분리
 -->

<script>
    const UserMy = (function() {
        return {
            /**
             * 프로필 이미지 첨부
             */
            uploadProflImg: function() {
                let $fileInput = $("#atchFile0");
                $fileInput.click();
                $fileInput.on("change", function() {
                    if (this.value !== "") {
                        if (!commons.validate.fileSizeChck(this)) return false;      // fileSizeChck
                        if (!commons.validate.fileExtnChck(this, "jpg|jpeg|png")) return false;      // fileExtnChck
                        const url = "${Url.USER_MY_UPLOAD_PROFL_IMG_AJAX!}";
                        let ajaxData = new FormData($("#profllImgForm")[0]);
                        commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                            if (commons.util.isNotEmpty(res.message)) {
                                Swal.fire({ "text": res.message })
                                    .then(function() {
                                        Swal.fire({ "text": "변경된 프로필은 재접속 이후에 적용됩니다." })
                                            .then(function() {
                                                if (res.rslt) commons.util.blockUIReload();
                                            });
                                    });
                            }
                        }, "block");
                    }
                });
            },
            /**
             * 프로필 이미지 삭제
             */
            removeProflImg: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${Url.USER_MY_REMOVE_PROFL_IMG_AJAX!}";
                    commons.util.blockUIFileAjax(url, {}, function(res) {
                        if (commons.util.isNotEmpty(res.message)) {
                            Swal.fire({ "text": res.message })
                                .then(function() {
                                    Swal.fire({ "text": "변경된 프로필은 재접속 이후에 적용됩니다." })
                                        .then(function() {
                                            if (res.rslt) commons.util.blockUIReload();
                                        });
                                });
                        }
                    }, "block");
                });
            },
            /**
             * TODO: 원본 프로필 이미지 조회
             */
            viewOrgnlProflImg: function() {
                //
            },
            /** 내 휴가 사용현황 조회 */
            myVcatnList: function() {
                $("#user_my_dtl_vcatn_list").modal("show");
                // template에 있는 tooltip들 활성화
                $("[data-bs-toggle='tooltip']").tooltip();
            }
        }
    })();
</script>
