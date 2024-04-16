/**
 * commons-validate.js
 * @namespace: commons.validate
 * @author: nichefish
 * @since: 2022-06-27
 * @last-modified: 2022-08-04
 * @last-modieied-by: nichefish
 * @dependency: jquery.validate.js
 * 공통 - 유효성 검사 관련 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.validate.noSpaces("#id") 이런식으로 사용)
 */
if (typeof commons === 'undefined') { var commons = {}; }
commons.validate = (function() {
    return {

        // global flag /g works inconsistent if called multiple times...

        /** 정규식: 숫자 */
        numRegex: /[0-9]/g,
        /** 정규식: 숫자 빼고 나머지 */
        nonNumRegex: /[^0-9]/g,

        /** 정규식: 숫자 및 점 */
        numAndDotRegex: /[0-9.]/g,
        /** 정규식: 숫자 및 점 빼고 나머지 */
        nonNumAndDotRegex: /[^0-9.]/g,

        /** 정규식: 숫자 및 쉼표(,) */
        numAndCommaRegex: /[^0-9,]/g,
        /** 정규식: 숫자 및 쉼표(,) 빼고 나머지 */
        nonNumAndCommaRegex: /[^0-9,]/g,

        /** 정규식: 숫자 및 점(.) 슬래시(/) */
        numDotAndSlashRegex: /[^0-9.\/]/g,
        /** 정규식: 숫자 및 점(.) 슬래시(/) 빼고 나머지 */
        nonNumDotAndSlashRegex: /[^0-9.\/]/g,

        /** 정규식: 숫자 및 대시(-) 슬래시(/) 콜론(:) 띄어쓰기 */
        dtRegex: /[0-9-:\/\s]/g,
        /** 정규식: 숫자 및 대시(-) 슬래시(/) 콜론(:) 띄어쓰기 빼고 나머지 (날짜 표시에 사용) */
        nonDtRegex: /[^0-9-:\/\s]/g,

        /** 정규식: 숫자 및 영문 대소문자 */
        engNumRegex: /[^A-Za-z0-9]/g,
        /** 정규식: 숫자 및 영문 대소문자 빼고 나머지 */
        nonEngNumRegex: /[^A-Za-z0-9]/g,

        /** 정규식: 숫자 및 영문 대문자 언더바 (코드 정보에 사용) */
        cdRegex: /[^A-Z0-9_]/g,
        /** 정규식: 숫자 및 영문 대문자 언더바 빼고 나머지 (코드 정보에 사용) */
        nonCdRegex: /[^A-Z0-9_]/g,

        /** 정규식: IP주소(v4) */
        ipv4regex: /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
        /** 정규식: IP주소(v4) (CIDR) */
        ipv4CidrRegex: /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)($|\/(0?[1-9]|[12][0-9]|3[01])$)/,

        /** 정규식: 아이디 정규식 */
        idRegex: /^(?=.*[a-z])[a-z\d]{5,16}$/,
        /** 정규식: 비밀번호 정규식 (요청으로 대문자 포함 조건 제거, 상황에 맞게 사용) */
        // pwRegex: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{9,20}$/g,
        pwRegex: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&_])[A-Za-z\d$@$!%*#?&_]{9,20}$/,
        /** 정규식: 2차 비밀번호 정규식 */
        scscPwRegex: /^(?=.*[a-zA-Z])(?=.*[$~@!%*#?&])[a-zA-Z$~@!%*#?&]{5}$/,

        /**
         * input에 공백시 자동 trim 처리 (onkeyup)
         * @param: selectorStr
         */
        noSpaces: function (selectorStr) {
            if ($(selectorStr) === undefined) return;
            $(selectorStr).on("keyup", function() {
                $(this).val($(this).val().trim());
            });
        },

        /**
         * 정규식에 해당시 공백처리 (onkeyup)
         * @param: selectorStr, regex
         */
        replaceBlankRegex: function (selectorStr, regex) {
            if ($(selectorStr) === undefined) return;
            commons.validate.noSpaces(selectorStr);
            $(selectorStr).on("keyup", function() {
                $(this).val($(this).val().replace(regex, ""));
            });
        },

        /**
         * 숫자 제외 공백처리 (onkeyup)
         * @param: selectorStr
         */
        onlyNum: function (selectorStr) {
            return commons.validate.replaceBlankRegex(selectorStr, commons.validate.nonNumRegex);
        },

        /**
         * 날짜 제외 공백처리 (onkeyup)
         * @param: selectorStr
         */
        onlyDt: function (selectorStr) {
            return commons.validate.replaceBlankRegex(selectorStr, commons.validate.nonDtRegex);
        },

        /**
         * 숫자와 점 제외 공백처리 (onkeyup)
         * @param: selectorStr
         */
        onlyNumAndDot: function (selectorStr) {
            return commons.validate.replaceBlankRegex(selectorStr, commons.validate.nonNumAndDotRegex);
        },

        /**
         * 숫자와 쉽표 제외 공백처리 (onkeyup)
         * @param: selectorStr
         */
        onlyNumAndComma: function (selectorStr) {
            return commons.validate.replaceBlankRegex(selectorStr, commons.validate.nonNumAndCommaRegex);
        },

        /**
         * 숫자와 영문 제외 공백처리 (onkeyup)
         * @param: selectorStr
         */
        onlyEngNum: function (selectorStr) {
            return commons.validate.replaceBlankRegex(selectorStr, commons.validate.nonEngNumRegex);
        },

        /**
         * ipv4 cidr regex (onkeyup)
         * @param: selectorStr
         */
        replaceIpv4Cidr: function (selectorStr) {
            commons.validate.replaceBlankRegex(selectorStr, commons.validate.nonNumDotAndSlashRegex);
            $(selectorStr).on("blur", function() {
                let $errorSpan = $("#" + $(this).attr("id") + "_valid_span");
                if (!commons.validate.ipv4regex.test($(this).val()) && !commons.validate.ipv4CidrRegex.test($(this).val())) {
                    $(this).val("");
                    if (commons.util.is$Present($errorSpan)) $errorSpan.empty().addClass("text-danger").text("유효하지 않은 IP 주소입니다.");
                } else {
                    if (commons.util.is$Present($errorSpan)) $errorSpan.empty().removeClass("text-danger");
                }
            });
        },

        /**
         * input 자동 lowercase 처리 (onkeyup)
         * @param: selectorStr
         */
        toLowerCase: function(selectorStr) {
            if ($(selectorStr) === undefined) return;
            $(selectorStr).on("keyup", function() {
                $(this).val($(this).val().toLowerCase());
            });
        },

        /**
         * input 자동 uppercase 처리 (onkeyup)
         * @param: selectorStr
         */
        toUpperCase: function(selectorStr) {
            if ($(selectorStr) === undefined) return;
            $(selectorStr).on("keyup", function() {
                $(this).val($(this).val().toUpperCase());
            });
        },

        /**
         * 핸드폰번호 형식 유효성검사 (정규식) (onkeyup)
         * @param: selectorStr
         */
        cttpc: function(selectorStr) {
            if ($(selectorStr) === undefined) return;
            $(selectorStr).on("keyup", function() {
                let value = $(this).val().trim();        // 공백 제거
                $(this).val(value);
                // 0으로 시작 안하면 무효처리
                let startWithZero = /(^0)([0-9]|-)*$/g;		    // 0으로 시작하는지
                if (!startWithZero.test(value)) {
                    $(this).val("");
                    return;
                }
                // 02는 12자/나머지는 13자보다 길어지면 끝자리 자름, 이후 xxx-xxx(x)-xxxx 형식에 맞게 변환
                let seoulLocal = /(^02)([0-9]|-)*$/g;           // 시작번호 02 vs. 나머지
                if (seoulLocal.test(value)) {
                    // 12자리 자르고 다시 포맷팅
                    $(this).val(value.substr(0,12).replace(/[^0-9]/g, "").replace(/(^02)([0-9]+)?([0-9]{4})$/,"$1-$2-$3").replace("--", "-"));
                } else {
                    // 13자리 자르고 다시 포맷팅
                    $(this).val(value.substr(0,13).replace(/[^0-9]/g, "").replace(/(^[0-9]{3})([0-9]+)?([0-9]{4})$/,"$1-$2-$3").replace("--", "-"));
                }
            });
        },

        /**
         * jquery-validation 함수 분리
         * @dependency: jquery-validation
         * @param: error, element
         */
        validateForm: function (formSelector, func) {
            $(function(){
                if (func === undefined) {
                    $(formSelector).validate({
                        errorPlacement : function(error, element) {
                            commons.validate.errorSpan(error, element);
                        },
                        ignore: [],         // hidden 필드도 검증하기 위함
                    });
                } else {
                    $(formSelector).validate({
                        submitHandler: function() {
                            if (func !== undefined) func();
                        },
                        errorPlacement : function(error, element) {
                            commons.validate.errorSpan(error, element);
                        },
                        ignore: [],         // hidden 필드도 검증하기 위함
                    });
                }
            });
        },

        /**
         * jquery-validation에서 공통으로 사용하는 에러 처리 함수 분리
         * @dependency: jQueryValidation
         * @param: error, element
         */
        errorSpan: function (error, element) {
            const $errorSpan = $("#" + $(element).attr("id") + "_valid_span");
            $errorSpan.append(error).addClass("text-danger").focus();
        },

        /**
         * jquery-validation에서 공통으로 사용하는 에러 처리 함수 분리
         * @dependency: jQueryValidation
         */
        clearErrorSpan: function() {
            $(".required").each(function(idx, elmt) {
                $("#" + $(elmt).attr("id") + "_valid_span").empty();
            });
        },

        /**
         * 파일 업로드시 확장자 체크
         */
        fileExtnChck: function(obj, validExtn) {
            if (!obj) return;
            let file = obj.files[0];
            if (typeof (file) == 'undefined') return;

            let filename = file.name;
            let extn = filename.split('\.').pop();
            if (extn === filename) extn = "";

            let basicfileFilter = "exe|class|jsp|asp|php|sh|bat|war|jar|java|xml|js|css|html|sql";
            if (basicfileFilter.indexOf(extn.toLowerCase()) > -1) {
                alert("파일첨부가 불가능한 파일 형식입니다.");
                $(obj).val("");
                return false;
            }

            // 'validExtn' 인자가 넘어온 경우:: 해당 확장자만 업로드 가능 (기본 필터는 그대로 막힘)
            if (validExtn !== undefined && validExtn !== "") {
                if (validExtn.indexOf(extn.toLowerCase()) < 0) {
                    let message = validExtn.replace(/\|/g, ", ");
                    alert(message + " 파일만 첨부 가능합니다.");
                    $(obj).val("");
                    return false;
                }
            }
            return true;
        },

        /**
         * 파일 업로드시 용량 체크 (현재 50MB)
         */
        fileSizeChck: function(obj) {
            if (!obj) return;
            let fileSizeLimitMb = 50;       // 50MB
            let fileSizeLimit = 1024 * 1024 * fileSizeLimitMb;
            let file = obj.files[0];
            if (typeof (file) == 'undefined') return;
            if (file.size > fileSizeLimit) {
                alert(fileSizeLimitMb + "MB 이하의 파일만 첨부가능합니다.");
                $(obj).val("");
                return false;
            }
            return true;
        }
    }
})();
