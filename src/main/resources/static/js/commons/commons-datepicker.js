/**
 * commons-datepicker.js
 * @namespace: commons.datepicker
 * @author: nichefish
 * @since: 2022-06-27
 * @dependency: daterangepicker.js, commons-validate.js
 * 공통 - datepicker(라이브러리) 관련 함수 모듈
 * (노출식 모듈 패턴 적용 :: util.datepicker.singleDatePicker() 요런식으로 사용)
 */
if (typeof commons === 'undefined') { var commons = {}; }
commons.datepicker = (function() {
    return {
        ptnDate: "yyyy-MM-DD",
        ptnDatetime: "yyyy-MM-DD HH:mm:ss",

        baseOption: {
            singleDatePicker: true,
            startDate: moment().startOf("Day"),
            showDropdowns: true,
            autoUpdateInput: false,
            locale: {
                format: this.ptnDate,
                daysOfWeek: ["일", "월", "화", "수", "목", "금", "토"],
                monthNames: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"]
            }
        },

        timeOption: {
            singleDatePicker: true,
            timePicker: true,
            timePicker24Hour: true,
            timePickerSeconds: true,
            startDate: moment().startOf("Day"),
            showDropdowns: true,
            autoUpdateInput: false,
            locale: {
                format: this.ptnDatetime,
                "daysOfWeek": ["일", "월", "화", "수", "목", "금", "토"],
                "monthNames": ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"]
            }
        },

        /**
         * datepicker 공통 형식
         */
        datepicker: function(selectorStr, option, func) {
            const format = option.locale.format;
            const $elmt = $(selectorStr);
            const id = $elmt.attr("id");
            $elmt.val(option.startDate);
            $elmt.daterangepicker(option, function(start) {
                $elmt.val(start.format(format));
                let $errorSpan = $("#"+id+"Span");
                if (commons.util.isNotEmpty($errorSpan)) $errorSpan.empty();
                if (commons.util.isNotEmpty(func)) func(start);
            });
            commons.validate.onlyDt(selectorStr);
        },
        
        /**
         * dateRangePicker init
         * @depdendency: dateRangePicker (metronic)
         */
        singleDatePicker: function(selectorStr, format, initDt, func) {
            let option = commons.datepicker.baseOption;
            if (commons.util.isNotEmpty(format)) option.locale.format = format;
            if (initDt !== 'today') option.startDate = commons.util.isEmpty(initDt) ? undefined : initDt;
            return this.datepicker(selectorStr, option, func);
        },

        /**
         * dateRangePicker init
         * @depdendency: dateRangePicker (metronic)
         */
        singleDatetimePicker: function(selectorStr, format, initDt, func) {
            let option = commons.datepicker.timeOption;
            if (commons.util.isNotEmpty(format)) option.locale.format = format;
            if (initDt !== 'today') option.startDate = commons.util.isEmpty(initDt) ? undefined : initDt;
            return this.datepicker(selectorStr, option, func);
        },
    }
})();
