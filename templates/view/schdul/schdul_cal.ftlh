<#--
  -- 일정 > 전체 일정 화면 (달력)
  -->
<#import "/layout/layout.ftlh" as layout >

<#-- 툴바 버튼 추가 -->
<#assign toolbar>
    <#include "/view/schdul/_schdul_cal_header.ftlh">
</#assign>

<!--begin::Page Vendors Javascript(used by this page)-->
<link href="/metronic/assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<script src="/metronic/assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="/js/commons/commons-fullcalendar.js?20230918"></script>

<#assign isReg = (isReg!false)>
<#assign isMdf = (isMdf!false)>

<@layout.layout toolbar=toolbar!"">

<style>
    #full_calendar_app table {
        table-layout: fixed;
    }
    @media (max-width: 768px) {
        .card-body {
            padding-left: 0 !important;
            padding-right: 0 !important;
        }
    }
</style>
<script>
    const Page = function() {
        return {
            eventList: [],
            calendar: null,
            calInitDt: null,
            calDt: null,
            init: function () {
                commons.validate.noSpaces(".no-space");        // 공백 자동 제거

                let chkArr = $("input:checkbox[id^='schdul_chk']");
                chkArr.each(function(_index, item) {
                    let id = $(item).attr("id");
                    if (Page.getChkedCookie(id) === "Y") $(item).prop("checked", true);
                });
                /** TODO : 선택된 년도,달 기준으로 변경해야함 */
                Page.calInitDt = new Date(new Date().getFullYear(), new Date().getMonth(), 1);
                Page.calDt = Page.calInitDt;
                Page.getEventList();
                Page.calendar = commons.fullcalendar.init("full_calendar_app", Page.eventList, function(info) {
                    // 일정 onclick 이벤트
                    let event = info.event;
                    let schdulCd = event.groupId;
                    let isVcatn = schdulCd === "${Constant.SCHDUL_TY_VCATN!}";
                    let isBrthdy = schdulCd === "${Constant.SCHDUL_TY_BRTHDY!}"
                    if (isVcatn || isBrthdy) return false;     // 휴가/생일은 팝업 띄우지 않음 - 휴가관리/사용자 정보 쪽에서 관리
                    Schdul.dtlAjax(event.id);
                });
                Page.calendar.render();
                // 좌우탐색버튼 이벤트핸들러 추가
                $(".fc-today-button").on("click", function() {
                    let calDt = new Date(Page.calInitDt.getFullYear(), Page.calInitDt.getMonth(), 1);
                    Page.refreshEventList(calDt);
                });
                $(".fc-prev-button").on("click", function() {
                    let calDt = new Date(Page.calDt.getFullYear(), Page.calDt.getMonth() - 1, 1);
                    Page.refreshEventList(calDt);
                });
                $(".fc-next-button").on("click", function() {
                    let calDt = new Date(Page.calDt.getFullYear(), Page.calDt.getMonth() + 1, 1);
                    Page.refreshEventList(calDt);
                });

                // datepicker 날짜 검색 init
                commons.datepicker.singleDatePicker("#calDt", "yyyy-MM-DD", commons.date.asStr(Page.calInitDt), function(start) {
                    let selectedDt = new Date(start);
                    Page.calendar.gotoDate(selectedDt);
                    Page.refreshEventList(selectedDt);
                });

                commons.util.enterKey("#searchKeyword", Page.search);       // 엔터키 처리

                // https://github.com/fullcalendar/fullcalendar/issues/6393
                let harnessArr = $(".fc-daygrid-event-harness");
                harnessArr.each(function() {
                    const marginTop = $(this).css("margin-top");
                    if (parseInt(marginTop) < 0) $(this).css("margin-top", "");
                });
            },
            /** 일정 등록 모달 호출 */
            refreshEventList: function(calDt) {
                Page.calDt = calDt;
                Page.getEventList();
                Page.calendar.removeAllEvents();
                Page.eventList.forEach(event => Page.calendar.addEvent(event));
            },
            /** 일정 등록 모달 호출 */
            search: function() {
                Page.getEventList();
                Page.calendar.removeAllEvents();
                Page.eventList.forEach(event => Page.calendar.addEvent(event));
            },
            /** 일정 등록 모달 호출 */
            getEventList: function() {
                const url = '${SiteUrl.SCHDUL_CAL_LIST_AJAX!}';
                // 날짜 세팅
                let calDt = Page.calDt;
                let yy  = calDt.getFullYear();
                let bgnDt = commons.date.getDateAddDayStr(calDt, -35, "yyyy-MM-dd");
                let endDt = commons.date.getDateAddDayStr(calDt, 45, "yyyy-MM-dd");
                // 체크박스 값을 읽어와서 필터 요소를 만듬
                let ajaxData = { 'yy': yy, 'bgnDt': bgnDt, 'endDt': endDt };
                let chkArr = $("input:checkbox[id^='schdul_chk_']");
                chkArr.each(function(_index, item) {
                    const itemName = $(item).attr("name").replaceAll("schdul_chk_", "") + "ed";
                    ajaxData[itemName] = $(item).prop("checked") ? "Y" : "N";
                });
                // 검색키워드 설정
                ajaxData.searchKeyword = $("#searchKeyword").val();
                commons.util.blockUIAjax(url, 'get', ajaxData, function(res) {
                    if (res.result) Page.eventList = res.resultList;
                });
            },
            /** 팝업 쿠키 존재여부 체크 */
            getChkedCookie: function(key) {
                let cookie = $.cookie(key);
                if (key === "schdul_chk_myPaprChk") return (cookie === undefined) ? "N" : cookie;
                return (cookie === undefined) ? "Y" : cookie;
            },
            /** 체크박스 쿠키 설정 */
            chkbxProp: function(obj) {
                let checkedYn = $(obj).prop("checked") ? "Y" : "N";
                let cookieOptions = { "expires": commons.date.getCurrDateAddDay(36135) };
                $.cookie($(obj).attr("id"), checkedYn, cookieOptions);
                Page.refreshEventList(Page.calDt);
            }
        }
    }();
    const Schdul = function() {
        return {
            prtcpntCnt: 0,
            init: function() {
                /** jquery :: validation */
                $(function () {
                    $("#schdulRegForm").validate({
                        submitHandler: function () {
                            let isReg = ($("#schdulRegForm #postNo").val() === "");
                            Swal.fire({
                                text: isReg ? <@spring.message "view.cnfm.reg"/> : <@spring.message "view.cnfm.mdf"/>,
                                showCancelButton: true,
                            }).then(function(result) {
                                if (!result.value) return;
                                Schdul.regAjax();
                            });
                        },
                        errorPlacement: function (error, element) {
                            commons.validate.errorSpan(error, element);        // 공통 함수로 분리
                        }
                    });
                });
            },
            /** 일정 등록 모달 호출 */
            regForm: function() {
                commons.util.handlebarsTemplate({}, "schdul_reg", "show");
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
                commons.util.chckboxLabel("jandiYn", "발송//미발송", "blue//gray");
                commons.datepicker.singleDatePicker("#bgnDt", "yyyy-MM-DD");
                commons.datepicker.singleDatePicker("#endDt", "yyyy-MM-DD");

                // 잔디발송여부 클릭시 글씨 변경
                commons.util.chckboxLabel("jandiYn", "발송//미발송", "blue//gray", function () {
                    $("#trgetTopicSpan").show();
                }, function () {
                    $("#trgetTopicSpan").hide();
                });
                $("#jandiYn").click();
                Schdul.addPrtcpnt();
            },
            /** 일정 등록 모달 호출 */
            prvtRegForm: function() {
                commons.util.handlebarsTemplate({ "isPrvt": true }, "schdul_reg", "show");
                $('[data-bs-toggle="tooltip"]').tooltip();      // template에 있는 tooltip들 활성화
                commons.datepicker.singleDatePicker("#bgnDt", "yyyy-MM-DD");
                commons.datepicker.singleDatePicker("#endDt", "yyyy-MM-DD");
            },
            /** 참여자 추가 */
            addPrtcpnt: function() {
                commons.util.handlebarsAppend({ "idx": Schdul.prtcpntCnt++ }, "schdul_reg_prtcpnt");
            },
            /** 참여자 삭제 */
            removePrtcpnt: function(idx) {
                $("#schdul_reg_prtcpnt_div #prtcpntRow"+idx).remove();
            },
            /** 종료일자 토글 처리 */
            schdulCd: function(obj) {
                if ($(obj).val() === "${Constant.SCHDUL_TY_HLDY!}") {
                    $("#endDt").val($("#startDt").val());
                    $("#endDtDiv").hide();
                    $("#endDtSpan").hide();
                } else {
                    $("#endDtDiv").show();
                    $("#endDtSpan").show();
                }
            },
            /** 등록/수정 처리(Ajax) */
            regAjax: function() {
                const url = "${SiteUrl.SCHDUL_REG_AJAX!}";
                let ajaxData = $("#schdulRegForm").serializeArray();
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    Swal.fire({ text: res.message })
                        .then(function() {
                            if (res.result) commons.util.blockUIReload();
                        });
                });
            },
            /** 상세 모달 호출 (Ajax) */
            dtlAjax: function(postNo) {
                const url = "${SiteUrl.SCHDUL_DTL_AJAX!}";
                let ajaxData = { "postNo": postNo };
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    let obj = res.resultObj;
                    commons.util.handlebarsTemplate(obj, "schdul_dtl", "show");
                    Schdul.postNo = obj.postNo;
                });
            },
            /** 수정 폼 오픈 */
            mdfForm: function() {
                const url = "${SiteUrl.SCHDUL_DTL_AJAX!}";
                let ajaxData = { "postNo": Schdul.postNo };
                commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                    if (!res.result) {
                        if (commons.util.isNotEmpty(res.message)) Swal.fire({ text: res.message });
                        return false;
                    }
                    $("#schdul_dtl_modal").modal("hide");
                    commons.util.handlebarsTemplate(res.resultObj, "schdul_reg", "show");
                    let resultObj = res.resultObj;
                    let prtcpnt = resultObj.prtcpntList;
                    Schdul.prtcpntCnt = prtcpnt !== undefined ? prtcpnt.length : 0;

                    commons.datepicker.singleDatePicker("#bgnDt", "yyyy-MM-DD", resultObj.bgnDt);
                    commons.datepicker.singleDatePicker("#endDt", "yyyy-MM-DD", resultObj.endDt);
                    // 잔디발송여부 클릭시 글씨 변경
                    commons.util.chckboxLabel("jandiYn", "발송//미발송", "blue//gray", function () {
                        $("#trgetTopicSpan").show();
                    }, function () {
                        $("#trgetTopicSpan").hide();
                    });
                });
            },
            /** 삭제 처리 (ajax) */
            delAjax: function() {
                Swal.fire({
                    text: <@spring.message "view.cnfm.del"/>,
                    showCancelButton: true,
                }).then(function(result) {
                    if (!result.value) return;
                    const url = "${SiteUrl.SCHDUL_DEL_AJAX!}";
                    let ajaxData = { "postNo" : Schdul.postNo }
                    commons.util.blockUIAjax(url, 'post', ajaxData, function(res) {
                        Swal.fire({ text: res.message })
                            .then(function() {
                                if (res.result) commons.util.blockUIReload();
                            });
                    });
                });
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
        Schdul.init();
    });
</script>

<!--begin::Advance form-->
<div class="collapse px-10 py-4 bg-light w-100" id="schdul_advanced_search_div">
    <div class="d-flex flex-lg-row flex-column align-items-lg-center align-items-start justify-content-end text-muted">
        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="내 일정 정보만 조회합니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="myPaprChk" class="me-2 text-muted cursor-help">내 일정만 보기</label>
            <input type="checkbox" name="myPaprChk" id="schdul_chk_myPaprChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>

        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="휴가 정보 표시여부를&#10;켜고 끕니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="outdtChk" class="me-2 text-muted cursor-help">휴가</label>
            <input type="checkbox" name="vcatnChk" id="schdul_chk_vcatnChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>
        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="내부일정 정보 표시여부를&#10;켜고 끕니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="outdtChk" class="me-2 text-muted cursor-help">내부일정</label>
            <input type="checkbox" name="indtChk" id="schdul_chk_indtChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>
        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="외근 정보 표시여부를&#10;켜고 끕니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="outdtChk" class="me-2 text-muted cursor-help">외근</label>
            <input type="checkbox" name="outdtChk" id="schdul_chk_outdtChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>
        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="재택근무 정보 표시여부를&#10;켜고 끕니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="tlcmmtChk" class="me-2 text-muted cursor-help">재택근무</label>
            <input type="checkbox" name="tlcmmtChk" id="schdul_chk_tlcmmtChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>
        <div class="mx-3"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="개인용 일정 표시여부를&#10;켜고 끕니다.&#10;(해당 정보는 브라우저 캐시에&#10;저장됩니다.)">
            <label for="tlcmmtChk" class="me-2 text-muted cursor-help">개인용 일정</label>
            <input type="checkbox" name="prvtChk" id="schdul_chk_prvtChk" class="form-check-input" value="Y" onclick="Page.chkbxProp(this);">
        </div>
    </div>
</div>
<!--end::Advance form-->

<div class="card post">
    <!--begin::Tab -->
    <ul class="nav nav-tabs nav-tabs-line ps-5 mt-5">
        <li class="nav-item">
            <a class="nav-link px-6 active" data-bs-toggle="tab" href="#kt_tab_pane_1">
                <span class="nav-icon">
                    <i class="bi bi-sort-down-alt"></i>
                </span>
                <span class="nav-text">달력 VIEW</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link px-6" onclick="Swal.fire('준비중입니다.'); return false;">
                <span class="nav-icon">
                    <i class="bi bi-people"></i>
                </span>
                <span class="nav-text">목록 VIEW</span>
            </a>
        </li>
    </ul>
    <!--begin::Card body-->
    <div class="card-body">
        <!--begin::Calendar::FullCalendar goes in here!!!-->
        <div id="full_calendar_app"></div>
        <!--end::Calendar-->
    </div>
    <!--end::Card body-->
</div>
<!--end::Card-->

<#include "/view/schdul/_schdul_reg_modal.ftlh">
<#include "/view/schdul/_schdul_reg_template.hbs">
<#include "/view/schdul/_schdul_reg_prtcpnt_template.ftlh">
<#include "/view/schdul/_schdul_dtl_modal.ftlh">
<#include "/view/schdul/_schdul_dtl_template.hbs">

</@layout.layout>