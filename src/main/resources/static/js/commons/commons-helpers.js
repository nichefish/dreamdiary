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
    const ifCondFunc = function (v1, operator, v2, options) {
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
    const existsFunc = function(value) {
        return !commons.util.isEmpty(value);
    }
    const notExistsFunc = function(value) {
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
        return "Y" === (value && value.trim()) ? options.fn(this) : options.inverse(this);;
    })

    const truefalseFunc = function(value, ynValues, options) {
        // 기본값 null일 때 true로 간주하는 옵션
        const defaultTrue = options.hash["default"] || false;
        if (defaultTrue && commons.util.isEmpty(value)) return true;
        const separator = "//";
        const idx = ynValues.indexOf(separator);
        const yValue = ynValues.substring(0, idx);
        return (value == yValue);
    }
    Handlebars.registerHelper("trueFalse", truefalseFunc);

    const equalsFunc = function(value, compareValue, options) {
        // 기본값 null일 때 true로 간주하는 옵션
        const defaultTrue = options.hash["default"] || false;
        if (defaultTrue && commons.util.isEmpty(value)) return true;
        // 비교결과 반환 :: 일부러 느슨한 비교
        return (value == compareValue);
    }
    const notEqualsFunc = function(value, compareValue, options) {
        return !equalsFunc(value, compareValue, options);
    }
    Handlebars.registerHelper("equals", equalsFunc);
    Handlebars.registerHelper("notEquals", notEqualsFunc);

    const checkedLabelFunc = function(value, ynLabels, options) {
        const separator = "//";
        const idx = ynLabels.indexOf(separator);
        const yLabel = ynLabels.substring(0, idx);
        const nLabel = ynLabels.substring(idx + 2);
        return value ? yLabel : nLabel;
    }
    Handlebars.registerHelper("checkedLabel", checkedLabelFunc);

    const checkedStyleFunc = function(value, type, ynColors, options) {
        const separator = "//";
        const idx = ynColors.indexOf(separator);
        const yColor = ynColors.substring(0, idx);
        const nColor = ynColors.substring(idx + 2);
        return "style=" + type + ":" + (value ? yColor : nColor) + ";";
    }
    const checkedYnStyleFunc = function(value, type, ynColors, options) {
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

    Handlebars.registerHelper('subtract', function(a, b) {
        return a - b;
    });

    /** 비교 연산자 */
    Handlebars.registerHelper('lt', function(a, b) {
        return a < b;
    });
    Handlebars.registerHelper('le', function(a, b) {
        return a <= b;
    });
    Handlebars.registerHelper('gt', function(a, b) {
        return a > b;
    });
    Handlebars.registerHelper('ge', function(a, b) {
        return a >= b;
    });

    /*    const placeholderFunc = function(value, options) {
            const placeholder = options.hash["default"] || 0;
            return commons.util.isEmpty(value) ? placeholder : value;
        }
        Handlebars.registerHelper("placeholder", placeholderFunc());*/

})(Handlebars);