/**
 * commons-helpers.js
 * @namespace: commons.date
 * @author: nichefish
 * @since: 2022-06-27
 * @dependency: commons.js
 * 공통 - 날짜 관련 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.date.getCurrDateStr(commons.date.ptnDate) 이런식으로 사용)
 */

(function(Handlebars) {
	if(!Handlebars) { return; }

    /**
   	 * 비교연산
   	 */
   	let ifCondFunc = function (v1, operator, v2, options) {
   	    switch (operator) {
   	        case '==':
   	            return (v1 == v2) ? options.fn(this) : options.inverse(this);
   	        case '===':
   	            return (v1 === v2) ? options.fn(this) : options.inverse(this);
   	        case '!=':
   	            return (v1 != v2) ? options.fn(this) : options.inverse(this);
   	        case '!==':
   	            return (v1 !== v2) ? options.fn(this) : options.inverse(this);
   	        case '<':
   	            return (v1 < v2) ? options.fn(this) : options.inverse(this);
   	        case '<=':
   	            return (v1 <= v2) ? options.fn(this) : options.inverse(this);
   	        case '>':
   	            return (v1 > v2) ? options.fn(this) : options.inverse(this);
   	        case '>=':
   	            return (v1 >= v2) ? options.fn(this) : options.inverse(this);
   	        case '&&':
   	            return (v1 && v2) ? options.fn(this) : options.inverse(this);
   	        case '||':
   	            return (v1 || v2) ? options.fn(this) : options.inverse(this);
   	        default:
   	            return options.inverse(this);
   	    }
   	}
    Handlebars.registerHelper('ifCond', ifCondFunc);

    /** 값 존재여부 체크*/
    let existsFunc = function(value, options) {
        return !commons.util.isEmpty(value);
    }
    Handlebars.registerHelper("exists", existsFunc);
    Handlebars.registerHelper("notExists", !existsFunc);
    /** 값 없을시 기본값 반환 */
    Handlebars.registerHelper("ifEmpty", function(value, alt) {
        if (commons.util.isEmpty(value)) return alt;
        return value;
    });

    Handlebars.registerHelper("selectedIf", function(value) {
        return value ? "selected" : "";
    });
    Handlebars.registerHelper("checkedIf", function(value) {
        return value ? "checked" : "";
    })
    Handlebars.registerHelper("numberFormat", function(value) {
        if (commons.util.isEmpty(value)) return;
        return value.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
    });

    let truefalseFunc = function(value, ynValues, options) {
        // 기본값 null일 때 true로 간주하는 옵션
        let defaultTrue = options.hash["default"] || false;
        if (defaultTrue && commons.util.isEmpty(value)) return true;
        let separator = "//";
        let idx = ynValues.indexOf(separator);
        let yValue = ynValues.substring(0, idx);
        let nValue = ynValues.substring(idx + 2);
        return (value == yValue);
    }
    Handlebars.registerHelper("trueFalse", truefalseFunc);

    let equalsFunc = function(value, compareValue, options) {
        // 기본값 null일 때 true로 간주하는 옵션
        let defaultTrue = options.hash["default"] || false;
        if (defaultTrue && commons.util.isEmpty(value)) return true;
        // 비교결과 반환
        return (value == compareValue);
    }
    Handlebars.registerHelper("equals", equalsFunc);
    Handlebars.registerHelper("notEquals", !equalsFunc);

    let checkedLabelFunc = function(value, ynLabels, options) {
        let separator = "//";
        let idx = ynLabels.indexOf(separator);
        let yLabel = ynLabels.substring(0, idx);
        let nLabel = ynLabels.substring(idx + 2);
        return value ? yLabel : nLabel;
    }
    Handlebars.registerHelper("checkedLabel", checkedLabelFunc);

    let checkedStyleFunc = function(value, type, ynColors, options) {
        let separator = "//";
        let idx = ynColors.indexOf(separator);
        let yColor = ynColors.substring(0, idx);
        let nColor = ynColors.substring(idx + 2);
        return "style=" + type + ":" + (value ? yColor : nColor) + ";";
    }
    Handlebars.registerHelper("checkedStyle", checkedStyleFunc);

    /** 날짜 포맷팅 */
    Handlebars.registerHelper("dateformat", function(value, pattern) {
        if (value === undefined) return "";
        return commons.date.asStr(value, pattern);
    });

    /*    let placeholderFunc = function(value, options) {
            let placeholder = options.hash["default"] || 0;
            return commons.util.isEmpty(value) ? placeholder : value;
        }
        Handlebars.registerHelper("placeholder", placeholderFunc());*/

})(Handlebars);