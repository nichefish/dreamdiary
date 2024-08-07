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

    /** 값 존재여부 체크 */
    let existsFunc = function(value) {
        return !commons.util.isEmpty(value);
    }
    let notExistsFunc = function(value) {
        return !existsFunc(value);
    }
    Handlebars.registerHelper("exists", existsFunc);
    Handlebars.registerHelper("notExists", notExistsFunc);
    /** 값 없을시 기본값 반환 */
    Handlebars.registerHelper("ifEmpty", function(value, alt) {
        if (commons.util.isEmpty(value)) return alt;
        return value;
    });

    /** select 선택 */
    Handlebars.registerHelper("selectedIf", function(value) {
        return value ? "selected" : "";
    });
    /** check 선택 */
    Handlebars.registerHelper("checkedYn", function(value) {
        return "Y" === value ? "checked" : "";
    })
    Handlebars.registerHelper("checkedIf", function(value) {
        return value ? "checked" : "";
    });
    Handlebars.registerHelper("numberFormat", function(value) {
        if (commons.util.isEmpty(value)) return;
        return value.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
    });

    Handlebars.registerHelper("ifYn", function(value, options) {
        return "Y" === value ? options.fn(this) : options.inverse(this);;
    })

    let truefalseFunc = function(value, ynValues, options) {
        // 기본값 null일 때 true로 간주하는 옵션
        let defaultTrue = options.hash["default"] || false;
        if (defaultTrue && commons.util.isEmpty(value)) return true;
        let separator = "//";
        let idx = ynValues.indexOf(separator);
        let yValue = ynValues.substring(0, idx);
        return (value == yValue);
    }
    Handlebars.registerHelper("trueFalse", truefalseFunc);

    let equalsFunc = function(value, compareValue, options) {
        // 기본값 null일 때 true로 간주하는 옵션
        let defaultTrue = options.hash["default"] || false;
        if (defaultTrue && commons.util.isEmpty(value)) return true;
        // 비교결과 반환 :: 일부러 느슨한 비교
        return (value == compareValue);
    }
    let notEqualsFunc = function(value, compareValue, options) {
        return !equalsFunc(value, compareValue, options);
    }
    Handlebars.registerHelper("equals", equalsFunc);
    Handlebars.registerHelper("notEquals", notEqualsFunc);

    let checkedLabelFunc = function(value, ynLabels, options) {
        let separator = "//";
        let idx = ynLabels.indexOf(separator);
        let yLabel = ynLabels.substring(0, idx);
        let nLabel = ynLabels.substring(idx + 2);
        return value ? yLabel : nLabel;
    }
    Handlebars.registerHelper("checkedLabel", checkedLabelFunc);

    let checkedStyleFunc = function(value, type, ynColors, options) {
        const separator = "//";
        let idx = ynColors.indexOf(separator);
        let yColor = ynColors.substring(0, idx);
        let nColor = ynColors.substring(idx + 2);
        return "style=" + type + ":" + (value ? yColor : nColor) + ";";
    }
    let checkedYnStyleFunc = function(value, type, ynColors, options) {
        return checkedStyleFunc(value === "Y", type, ynColors, options);
    }
    Handlebars.registerHelper("checkedStyle", checkedStyleFunc);

    Handlebars.registerHelper("checkedYnStyle", checkedYnStyleFunc);


    Handlebars.registerHelper("concat", function(value1, value2) {
        return value1 + "" + value2;
    });

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