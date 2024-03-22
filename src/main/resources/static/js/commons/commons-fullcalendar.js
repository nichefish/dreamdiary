/**
 * commons-fullcalendar.js
 * @namespace: commons.fullcalendar
 * @author: nichefish
 * @since: 2022-06-27
 * @dependency: fullCalendar.js
 * 공통 - 일반 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.enterKey("#userId") 이런식으로 사용)
 */
if (typeof commons === 'undefined') { var commons = {}; }
commons.fullcalendar = (function() {
    return {
        /** fullCalendar 객체 생성 */
        init: function(selectorId, eventList, clickFunc) {
            let target = document.getElementById(selectorId);
            let todayDate = moment().startOf("day");
            let TODAY = todayDate.format("YYYY-MM-DD");

            return new FullCalendar.Calendar(target, {
                headerToolbar: {
                    left: "prev,next today",
                    center: "title",
                    right: ""
                },
                height: 1040,
                contentHeight: 980,
                aspectRatio: 3,  // see: https://fullcalendar.io/docs/aspectRatio
                expandRows: true,
                nowIndicator: true,
                selectOverlap: false,
                now: TODAY,

                views: {
                    dayGridMonth: { buttonText: "월별 조회" }
                },

                initialView: "dayGridMonth",
                initialDate: TODAY,
                locale: "ko",

                editable: false,
                businessHours: true,
                displayEventTime: false,
                // dayMaxEvents: true, // allow "more" link when too many events
                navLinks: false,
                nextDayThreshold: '00:00:00',
                events: eventList,

                eventClick: function(info) {
                    if (clickFunc !== undefined) clickFunc(info);
                }
            });
        }
    }
})();