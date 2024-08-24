/**
 * commons-date.js
 * @namespace: commons.date
 * @author: nichefish
 * @since: 2022-06-27
 * @dependency: commons.js
 * 공통 - 날짜 관련 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.date.getCurrDateStr(commons.date.ptnDate) 이런식으로 사용)
 */
if (typeof commons === 'undefined') { var commons = {}; }
commons.date = (function() {
    return {
        ptnDate: "yyyy-MM-dd",
        ptnDatetime: "yyyy-MM-dd HH:mm:ss",
        ptnPdate: "yyyyMMdd",
        ptnSdate: "yyyy/MM/dd",

        /**
         * 문자열을 받아서 날짜Date 반환
         * @param: dateStr
         */
        strToDate: function(dateStr) {
            let delim = "-";
            if (dateStr.length === 8) dateStr = dateStr.substring(0, 3) + delim + dateStr.substring(4, 5) + delim + dateStr.substring(6, 7);
            let newDate;
            try {
                newDate = new Date(dateStr);
            } catch (error) {
                console.log(error);
            }
            return newDate;
        },

        /**
         * 날짜Date를 받아서 문자열 반환 (pattern)
         * @param: date, pattern(optional)
         */
        dateToStr: function(date, pattern) {
            if (!(date instanceof Date)) return;
            if (pattern === undefined) pattern = commons.date.ptnDate;
            // 가변적인 delim에는 일단 안먹음. 연월일 구분이 -- 또는 // 식으로 같은 구분자라고 일단 가정
            let delim = pattern.replaceAll(/[a-zA-Z]/g,"").charAt(0);
            return date.getFullYear() + delim + ('0' + (date.getMonth() + 1)).slice(-2)  + delim + ('0' + date.getDate()).slice(-2);
        },

        /**
         * 날짜 또는 문자열을 받아서 날짜Date로 일괄 반환
         * Date, 문자열
         */
        asDate: function(date) {
            if (date === "today") return new Date();
            if (commons.util.isEmpty(date)) return;
            if (date instanceof Date) return date;
            if (typeof date === "string") return commons.date.strToDate(date);
        },

        /**
         * 날짜 또는 문자열을 받아서 문자열로 일괄 반환
         * Date, 문자열, LocalDateTime
         */
        asStr: function(date, pattern) {
            if (date === "today") return commons.date.dateToStr(new Date, pattern);
            if (commons.util.isEmpty(date)) return;
            return commons.date.dateToStr(commons.date.asDate(date), pattern);
        },

        /**
         * 현재 날짜(시간 제외) 문자열 반환 (pattern)
         * @param: pattern(optional)
         */
        getCurrDateStr: function(pattern) {
            return commons.date.dateToStr(new Date(), pattern);
        },

        /**
         * 어제 날짜(시간 제외) 문자열 반환 (pattern)
         * @param: pattern(optional)
         */
        getPrevDateStr: function(pattern) {
            return commons.date.getCurrDateAddDayStr(-1, pattern);
        },

        /**
         * 내일 날짜(시간 제외) 문자열 반환 (pattern)
         * @param: pattern(optional)
         */
        getNextDateStr: function(pattern) {
            return commons.date.getCurrDateAddDayStr(1, pattern);
        },

        /**
         * 헌재 년도 반환
         */
        getCurrYyStr: function() {
            return new Date().getFullYear();
        },

        /**
         * 헌재 월 반환
         */
        getCurrMnthStr: function() {
            return new Date().getMonth() + 1;
        },

        /**
         * 헌재 날짜 반환
         */
        getCurrDayStr: function(digits = 1) {
            return new Date().getDate().toString();
            // 숫자를 지정된 자릿수로 맞추기
            if (day.length < digits) day = day.padStart(digits, '0');
            return day;
        },

        /**
         * 현재 날짜(시간 제외)에 일수 더해서 날짜 객체로 반환
         * @param: day
         */
        getCurrDateAddDay: function(day) {
            let currDate = new Date();
            return new Date(currDate.setDate(currDate.getDate() + day));
        },

        /**
         * 현재 날짜(시간 제외)에 일수 더해서 문자열로 반환 (pattern)
         * @param: day, pattern(optional)
         */
        getCurrDateAddDayStr: function(day, pattern) {
            return commons.date.dateToStr(commons.date.getCurrDateAddDay(day), pattern);
        },

        /**
         * 날짜를 받아서 일수 더해서 날짜 객체로 반환
         */
        getDateAddDay: function(paramDate, day) {
            let date = new Date(paramDate);
            return new Date(date.setDate(date.getDate() + day));
        },

        getEndOfDay: function(paramDate) {
            let date = new Date(paramDate);
            date.setHours(23, 59, 59);
            return date;
        },

        /**
         * 날짜를 받아서 일수 더해서 문자열로 반환
         */
        getDateAddDayStr: function(date, day, pattern) {
            return commons.date.dateToStr(commons.date.getDateAddDay(date, day), pattern);
        },

        /**
         * 날짜 문자열을 받아서 일수 더해서 날짜 객체로 반환
         */
        getDateStrAddDay: function(datestr, day) {
            let date = commons.date.asDate(datestr);
            return new Date(date.setDate(date.getDate() + day));
        },

        /** 날짜 문자열을 받아서 일수 더해서 문자열로 반환 */
        getDateStrAddDayStr: function(datestr, day, pattern) {
            return commons.date.dateToStr(commons.date.getDateStrAddDay(datestr, day), pattern);
        },

        /**
         * 현재 시간 문자열 반환 (HH:mm:ss)
         */
        getCurrTimeStr: function() {
            let currDate = new Date();
            return ('0' + currDate.getHours()).slice(-2) + ":" + ('0' + currDate.getMinutes()).slice(-2) + ":" + ('0' + currDate.getSeconds()).slice(-2);
        },

        /**
         * 현재 날짜시간 문자열 반환 (yyyy-MM-dd HH:mm:ss)
         * @param: pattern(optional)
         */
        getCurrDatetimeStr: function(pattern) {
            return commons.date.getDatetimeStr(new Date(), pattern);
        },

        /**
         * 날짜 문자열을 받아서 문자열 반환 (pattern 변환)
         */
        getDateStrStr: function(datestr, pattern) {
            if (pattern === undefined) pattern = commons.date.ptnDate;
            if (datestr.length === 8) datestr = datestr.substr(0,4) + "-" + datestr.substr(4, 2) + "-" + datestr.substr(6, 2);
            let date = commons.date.asDate(datestr);
            return commons.date.dateToStr(date, pattern);
        },

        /**
         * 날짜Date를 받아서 시간 문자열로 반환 (HH:mm:ss)
         */
        getTimeStr: function(date) {
            if (!(date instanceof Date)) return;
            return ('0' + date.getHours()).slice(-2) + ":" + ('0' + date.getMinutes()).slice(-2) + ":" + ('0' + date.getSeconds()).slice(-2);
        },

        /**
         * 날짜를 받아서 날짜시간 문자열 반환 (yyyy-MM-dd HH:mm:ss)
         * @param: pattern(optional)
         */
        getDatetimeStr: function(date, pattern) {
            if (!(date instanceof Date)) return;
            return commons.date.dateToStr(date, pattern) + " " + commons.date.getTimeStr(date);
        },

        /**
         * 시작-끝 문자열을 받아서 현재 시간이 포함되어 있는지 여부 판단하여 반환
         * @param: startDt, endDt;
         */
        getCurrDateWithinChk: function(startDt, endDt) {
            return commons.date.getDateWithinChk(this.getCurrDateStr(), startDt, endDt);
        },

        /**
         * 시작-끝 문자열을 받아서 특정 시간이 포함되어 있는지 여부 판단하여 반환
         * @param: dt, startDt, endDt (문자열이면 문자열, date면 date 타입이 셋 다 일치해야 함.)
         */
        getDateWithinChk: function(dt, startDt, endDt) {
            if (startDt === "") return (dt <= endDt);
            if (endDt === "") return (dt >= startDt);
            return (dt >= startDt && dt <= endDt);
        },

        /**
         * Date를 받아서 요일(한글 또는 영문) 반환
         * @param: date, locale
         */
        getDayweekStr: function(date, locale) {
            date = commons.date.asDate(date);
            let dw = date.getDay();     // 요일 인덱스
            let dwStrArr = [ ["일","SUN"], ["월","MON"], ["화","TUE"], ["수","WED"], ["목","THU"], ["금","FRI"], ["토","SAT"] ];
            let localeIdx;

            switch(locale) {
                case "KO": localeIdx = 0; break;
                case "EN": localeIdx = 1; break;
                default: localeIdx = 0;
            }
            return dwStrArr[dw][localeIdx];
        },

        /**
         * 특정 날짜가 포함되는 주의 특정 요일 날짜 반환
         * 일0 월1 화2 수3 목4 금5 토6
         */
        getWeekdayDate: function(date, paramWeekday) {
            date = commons.date.asDate(date);
            // 요일 인덱스를 받아서 빼고 해당 요일로 추가
            let weekday = date.getDay();
            let dayDiff = date.getDate() - weekday + ((weekday === 0) ? -7 : 0) + paramWeekday;
            return new Date(date.setDate(dayDiff));
        },

        /**
         * 오늘 날짜가 포함되는 주의 특정 요일 날짜 문자열 반환
         * 일0 월1 화2 수3 목4 금5 토6
         */
        getWeekdayDateStr: function(date, paramWeekday, pattern) {
            if (pattern === undefined) pattern = commons.date.ptnDate;
            return commons.date.dateToStr(commons.date.getWeekdayDate(date, paramWeekday), pattern);
        },

        /**
         * 시작일 종료일 계산 
         * 앞선 날이 뒷날보다 더 앞쪽인지, 순서가 제대로 되었는지 반환
         */
        isBefore: function(beginSelectorStr, endSelectorStr) {
            const begin = $(beginSelectorStr).val();
            const end = $(endSelectorStr).val();
            const beginCompareDate = commons.date.asDate(begin);
            const endCompareDate = commons.date.asDate(end);
            if (commons.util.isEmpty(beginCompareDate) || commons.util.isEmpty(endCompareDate)) return null;
            return beginCompareDate.getTime() <= endCompareDate.getTime();
        },

        /**
         * 좌우로 이동시 날짜 구하는 함수 (메소드 분리)
         * @param searchType: "day"/"week"/"month"/"year"
         * @param stdrdDate: 기준일("yyyy-MM-dd")
         * @param type: "prev"/"next"
         */
        navigateDate: function(searchType, stdrdDate, type) {
            switch(searchType) {
                case "day":  return commons.date.getDateStrAddDayStr(stdrdDate, (type === "prev") ? -1 : 1, this.ptnDate);
                case "week":  return commons.date.getDateStrAddDayStr(stdrdDate, (type === "prev") ? -7 : 7, this.ptnDate);
                case "month":
                    let date = commons.date.asDate(stdrdDate);
                    if (type === "prev") {
                        date.setDate(0);
                        date.setDate(1);
                        return commons.date.dateToStr(date, 'yyyy-MM-dd');
                   } else {
                        return commons.date.dateToStr(new Date(date.getFullYear(), date.getMonth() + 1, 1), this.ptnDate);
                   }
                case "year": return new Date(stdrdDate).getFullYear() + ((type === "prev") ? -1 : 1) + "-01-01";
                default: return commons.date.getDateStrAddDayStr(stdrdDate, (type === "prev") ? -1 : 1, this.ptnDate);
            }
        }
    }
})();
