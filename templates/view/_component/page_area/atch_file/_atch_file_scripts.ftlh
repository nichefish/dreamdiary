<#--
 * 레이아웃 > 첨부파일 모듈
 * @included_in : 파일 첨부 기능이 있는 페이지들
 -->

<script>
    const AtchFile = (function() {
        return {
            init: function() {
                AtchFile.atchFileListToggle();     // 첨부파일 영역 0개인지 체크
            },
            /**
             * 첨부파일 추가추가
             * (reqstItemIdx는 어차피 한 페이지 내에서 고유하므로 따로 처리해줄 필요 없다.)
             */
            addFileItem: function() {
                let reqstItemIdx = commons.util.getReqstItemIdx("input", "id^=atchFile", "atchFile");		// elmt, selector, elmtId
                // 감췄다 숨겼다 할것이므로 style로 설정한다.
                let tableTmpl = "<div class='row' id='itemContainer"+reqstItemIdx+"' style='display:none;'>" + $("#atchFileTemplate").html() + "</div>";
                let str = tableTmpl.replace(/__INDEX__/g, reqstItemIdx);
                // 공격 탐지 내역 목록 관련 처리 로직
                $("#atchFileSpan").append(str);
                $("#atchFile" + reqstItemIdx).click();
                $("#atchFile" + reqstItemIdx).on("change", function() {
                    let newFileSpan = $("div#itemContainer"+reqstItemIdx);
                    if (this.value !== "") {
                        if (!commons.validate.fileSizeChck(this) || !commons.validate.fileExtnChck(this)) newFileSpan.remove();      // fileSizeChck
                        let filename = $("input#atchFile"+reqstItemIdx).val().split('\/').pop().split('\\').pop();
                        $("span#fileNm"+reqstItemIdx).text(filename);
                        newFileSpan.show();
                    } else {
                        newFileSpan.remove();
                    }
                    AtchFile.atchFileListToggle();
                });
            },
            /** 추가추가 개수 0개인지 체크 */
            atchFileListToggle: function() {
                if ($("#atchFileSpan div[id^=itemContainer]").length === 0) {
                    $("#emptyFileListDiv").show();
                } else {
                    $("#emptyFileListDiv").hide();
                }
            },
            /** 새로 추가된 첨부파일 삭제 */
            delNewFileSpan: function(idx) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("div#itemContainer"+idx).remove();
                });
            },
            /** 기존 첨부파일 삭제 플래그 세팅 */
            delExistingFile: function(atchFileDtlNo) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("#atchCtrl" + atchFileDtlNo).val("D");
                    $("div#itemContainer"+atchFileDtlNo).hide();
                });
            }
        }
    })();
    $(document).ready(function () {
        AtchFile.init();
    });
</script>