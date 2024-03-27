<!--
 -- 레이아웃 > 첨부파일 모듈
 -- @included in : 파일 첨부 기능이 있는 페이지들
 -->
<script>
    let AtchFile = function() {
        return {
            init: function() {
                AtchFile.fnAtchFileListToggle();     // 첨부파일 영역 0개인지 체크
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
                    AtchFile.fnAtchFileListToggle();
                });
            },
            /** 추가추가 개수 0개인지 체크 */
            fnAtchFileListToggle: function() {
                if ($("#atchFileSpan div[id^=itemContainer]").length === 0) {
                    $("#emptyFileListDiv").show();
                } else {
                    $("#emptyFileListDiv").hide();
                }
            },
            /** 새로 추가된 첨부파일 삭제 */
            fnDelNewFileSpan: function(idx) {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    $("div#itemContainer"+idx).remove();
                });
            },
            /** 기존 첨부파일 삭제 플래그 세팅 */
            fnDelExistingFile: function(atchFileDtlNo) {
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
    }();

    $(document).ready(function () {
        AtchFile.init();
    });
</script>

<!-- 가리고 숨기고를 반복하는 곳은 style로 선언해야 한다. -->
<div id="atchFileTemplate" style="display:none;">
    <!--atchCtrl :: CRUD -->
    <input type="hidden" name="atchCtrl__INDEX__" id="atchCtrl__INDEX__" value="C" size="5"/>
    <!--fileSn :: nullable-->
    <input type="hidden" name="fileSn__INDEX__" value="fileSn__INDEX__" size="5"/>
    <div hidden>
        <input type="file" class="file" name="atchFile__INDEX__" id="atchFile__INDEX__" onchange="return false;"/>
    </div>
    <div class="col-xl-10 text-sm-start">
        <!--fileNm :: to display-->
        <i class="fas fa-file-download fs-15 me-3"></i> <span id="fileNm__INDEX__" ></span>
        <button type="button" class="badge badge-light btn-primary badge-outlined mx-2"
                onclick="AtchFile.fnDelNewFileSpan('__INDEX__');"
                data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="<@spring.message "bs.tooltip.file.del"/>">
            삭제
        </button>
    </div>
</div>