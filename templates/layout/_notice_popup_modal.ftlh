<#--
  -- 로그인 화면 > 공지사항 Modal
  -- TODO:
  -->
<script>
    /** 공지사항 팝업 관련 */
    let NoticePopup = function() {
        return {
            init: function() {
                /** 로그인 공지 목록 조회 */
                let isNotAuthenticated = ${isNotAuthenticated!false?string};
                if (!isNotAuthenticated) NoticePopup.showNoticePopupAjax();
            },
            /** 팝업 목록 조회 및 처리 */
            showNoticePopupAjax() {
                // const url = "${SiteUrl.NOTICE_POPUP_LIST_AJAX!}";
                // commons.util.blockUISyncAjax(url, 'post', null, function(res) {
                //     if (commons.util.isNotEmpty(res.resultList)) {
                //         let resultList = res.resultList;
                //         NoticePopup.popupList = resultList;
                //         NoticePopup.popupSize = resultList.length;
                //         NoticePopup.popupMaxIdx = resultList.length - 1;
                //         NoticePopup.popupIdx = 0;
                //         NoticePopup.showNoticePopup();
                //     }
                // });
            },
            /** 팝업 열기 (쿠키 있으면 열지 않음, 순회하면서 체크) */
            showNoticePopup: function() {
                if (commons.util.isEmpty(NoticePopup.popupList)) return;
                if (NoticePopup.popupIdx > NoticePopup.popupMaxIdx) return;
                let hasNoticeCookie = NoticePopup.hasNoticeCookie();
                if (hasNoticeCookie) {
                    NoticePopup.popupIdx++;
                    return NoticePopup.showNoticePopup();
                }
                const url = "${SiteUrl.NOTICE_DTL_AJAX!}";
                let obj = NoticePopup.popupList[NoticePopup.popupIdx];
                let ajaxData = { "postNo": obj.postNo, "boardCd": obj.boardCd };
                commons.util.blockUISyncAjax(url, 'get', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    commons.util.handlebarsTemplate(res.resultObj, "notice_popup", "show");
                    NoticePopup.popupIdx++;
                });
            },
            /** 팝업 쿠키 존재여부 체크 */
            hasNoticeCookie: function() {
                let obj = NoticePopup.popupList[NoticePopup.popupIdx];
                if (obj === undefined) return false;
                let popupCookie = $.cookie("notice_popup__postno_" + obj.postNo + "_boardcd_" + obj.boardCd);
                return (popupCookie !== undefined);
            },
            /** 팝업 닫기 버튼 */
            closeBtnFunc: function() {
                NoticePopup.setNoticePopupCookie();          // 쿠키 설정
                if (NoticePopup.popupIdx < (NoticePopup.popupSize)) {
                    return NoticePopup.showNoticePopup();
                } else {
                    NoticePopup.closeNoticePopup();
                }
            },
            /** 공지사항 팝업 쿠키 세팅 */
            setNoticePopupCookie: function() {
                // 팝업안보기 옵션 선택 상태일 경우 브라우저 쿠키 추가
                let isChecked = $("#noticePopupCookieChk").prop("checked");
                if (isChecked) {
                    let postNo = $("#noticePopupPostNo").val();
                    let boardCd = $("#noticePopupBoardCd").val();
                    let cookieNm = "notice_popup__postno_" + postNo + "_boardcd_" + boardCd;
                    // 선택지에 따라 시간 별도 세팅
                    let selectedTime = $("#noticePopupCookieTime").val();
                    let cookieOptions = {};
                    if (selectedTime === "midnight") cookieOptions.expires = commons.date.getEndOfDay(new Date());
                    else if (selectedTime === "forever") cookieOptions.expires = commons.date.getCurrDateAddDay(36135);
                    else cookieOptions.maxAge = selectedTime;       // Do note that maxAge is in milliseconds!!! (어처구니...)
                    cookieOptions.path = "/";
                    $.cookie(cookieNm, true, cookieOptions);
                }
            },
            /** 팝업 닫기 */
            closeNoticePopup: function() {
                $("#notice_popup_modal").modal("hide");
            },
        }
    }();

    $(document).ready(function() {
        NoticePopup.init();
    });
</script>
<div class="modal fade" id="notice_popup_modal" data-bs-keyboard="false" data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="lgnNotifyModalLongTitle">[공지사항 팝업]</h5>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2"
                     onclick="NoticePopup.closeBtnFunc();" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <div class="modal-body modal-mbl-body mb-5">
                <div id="notice_popup_div">
                    <!-- handlebars template goes in here -->
                </div>
            </div>
        </div>
    </div>
</div>