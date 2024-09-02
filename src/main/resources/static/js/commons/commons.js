/**
 * commons.js
 * @namespace: commons.util
 * @author: nichefish
 * @since: 2022-06-27~
 * @dependency: jquery.blockUI.js, jquery.forms.js
 * 공통 - 일반 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.util.enterKey("#userId") 이런식으로 사용)
 */
if (typeof commons === 'undefined') { var commons = {}; }
$(document).ajaxComplete(function(event, xhr) {
    const isHtmlReturned = xhr.getResponseHeader("Content-Type") && xhr.getResponseHeader("Content-Type").indexOf("text/html") !== -1;
    if (isHtmlReturned) {
        // ajax 응답으로 HTML이 반환되면 로그인 페이지로 이동
        window.location.href = "/auth/lgnForm.do";
    }
});
(function($) {
    // 인증만료로 ajax 실패시 로그인 페이지로 이동
    $.ajaxSetup({
        error: function(xhr) {
            if (xhr.status === 401) {   // ACCESS DENIED
                if (commons.util.hasSwal()) {
                    Swal.fire("접근이 거부되었습니다. (ACCESS DENIED)");
                } else {
                    alert("접근이 거부되었습니다. (ACCESS DENIED)");
                }
                window.location.href = "/auth/lgnForm.do";
            } else if (xhr.status === 403) {
                if (commons.util.hasSwal()) {
                    Swal.fire("접근이 거부되었습니다. (FORBIDDEN)");
                } else {
                    alert("접근이 거부되었습니다. (FORBIDDEN)");
                }
                window.location.href = "/auth/lgnForm.do";
            }
            // location.replace("/lgnForm.do");
        }
    });
})(jQuery);
commons.util = (function() {
    return {
        /** blockUI wrapped by try-catch */
        blockUI: function() {
            // let blockUI = new KTBlockUI();
            try {
                $.blockUI({
                    message: `<div class="flex-column py-2 bg-dark bg-opacity-25">
                        <span class="spinner-border text-primary" role="status"></span>
                        <span class="text-muted fs-6 fw-semibold mt-5">Loading...</span>
                    </div>`
                });
            } catch (error) {
                console.log("blockUI is not defined.");
            }
        },
        /** blockUI wrapped by try-catch */
        unblockUI: function() {
            try {
                setTimeout($.unblockUI(), 1500);    // 1.5초간 딜레이
            } catch(error) {
                console.log("blockUI is not defined.");
            }
        },
        hasSwal: function () {
            return (typeof Swal !== 'undefined');
        },
        /**
         * 빈 값, 공백 또는 undefined 체크
         * @param: data
         */
        isEmpty: function(data) {
            const type = typeof(data);
            if (type === 'object') {
                let isEmpty = false;
                if (data) {
                    try {
                        isEmpty = (JSON.stringify(data) === '{}' || JSON.stringify(data) === '[]');
                    } catch (error) {
                        // console.log(error);
                    }
                }
                return isEmpty || !data;
            } else if (type === 'string') {
                return !data.trim();
            } else return (type === 'undefined' || data === null);
        },

        /**
         * !공백 및 !undefined 체크
         * @param: data
         */
        isNotEmpty: function(data) {
            return !commons.util.isEmpty(data);
        },

        /**
         * jquery selector element 존재여부 체크
         * @param: selectorStr
         */
        is$Present: function(selectorStr) {
            if (typeof(selectorStr) === 'string') return $(selectorStr).length > 0;
            if (Array.isArray(selectorStr)) return selectorStr.length > 0;
            return commons.util.isNotEmpty(selectorStr);
        },

        /**
         * jquery selector element 존재여부 체크
         * @param: selectorStr
         */
        is$NotPresent: function(selectorStr) {
            return !commons.util.is$Present(selectorStr);
        },

        /**
         * TODO: element 값 존재여부 확인
         */

        /**
         * 새 팝업 open
         */
        popup: function(url, popupNm, option) {
            window.open(url, popupNm, option).open();
        },

        /**
         * input에 Enter키 처리 붙이기 (onkeyup)
         * @param: selectorStr
         * @param: function
         */
        enterKey: function(selectorStr, func) {
            if (commons.util.is$NotPresent(selectorStr)) return;
            const $inputs = $(selectorStr);
            $inputs.on("keyup", function (key) {
                if (key.keyCode === 13) {
                    key.preventDefault();
                    func();
                }
            });
        },

        /**
         * 행 추가 함수에서 reqstItemIdx 계산해서 반환
         */
        getReqstItemIdx: function(arrElmt, selectorStr, arrElmtId) {
            let reqstItemIdx = 0, elmtId, currentIdx;
            const reqstDataArr = $(arrElmt + "[" + selectorStr + "]");
            if (commons.util.is$NotPresent(reqstDataArr)) return;
            $(reqstDataArr).each(function (idx, elmt) {
                elmtId = elmt.id;
                if (elmtId.indexOf("__") < 0) {
                    currentIdx = elmtId.replace(arrElmtId, "");
                    if (reqstItemIdx <= Number(currentIdx)) reqstItemIdx = Number(currentIdx) + 1;
                }
            });
            return reqstItemIdx;
        },

        /**
         * 행 추가 함수에서 해당 input의 값(숫자) 총합 구해서 반환 (.excludeSum 제외)
         */
        getReqstItemTotSum: function(selectorStr) {
            const reqstDataArr = $("input[" + selectorStr + "]");
            if (commons.util.is$NotPresent(reqstDataArr)) return;
            let reqstItemTotSum = 0, elmtId;
            $(reqstDataArr).each(function (idx, elmt) {
                elmtId = elmt.id;
                if (elmtId.indexOf("{") < 0 && !$(elmt).hasClass("excludeSum")) {
                    const value = $(elmt).val();
                    const numValue = value.replace(/,/gi, "");
                    if (value && !isNaN(Number(numValue))) reqstItemTotSum += Number(numValue);
                }
            });
            return reqstItemTotSum;
        },

        /**
         * selectorStr 받아서 input 값을 숫자로 반환
         */
        toNumber: function(selectorStr) {
            if (commons.util.is$NotPresent(selectorStr)) return;
            const $input = $(selectorStr);
            if ($input.val() === undefined) return;
            const numValue = Number($(selectorStr).val().replace(/,/gi, ""));
            if (!isNaN(numValue)) return numValue;
        },

        /**
         * 파일 다운로드
         * (ajax로 파일 존재여부 체크 후 임시 form 만들어서 submit 후 지움)
         */
        fileDownload: function(atchFileNo, atchFileDtlNo) {
            let inputs = "<input type='hidden' name='atchFileNo' value='" + atchFileNo + "'>";
            inputs += "<input type='hidden' name='atchFileDtlNo' value='" + atchFileDtlNo + "'>";
            let form = "<form action='/file/fileDownload.do'>" + inputs + "</form>";
            $(form).appendTo('body').submit().remove();
        },

        /**
         * 쿠키 추가
         */
        setCookie: function(name, value, options) {
            let cookieStr = name + '=' + value + ';path=/';
            if (typeof options !== "undefined") {
                if (options.maxAge !== undefined) cookieStr = cookieStr + ";max-age=" + options.maxAge;
                if (options.expires !== undefined) cookieStr = cookieStr + ";expires=" + options.expires;
            }
            document.cookie = cookieStr;
        },

        /**
         * 쿠키 조회
         */
        getCookie: function(name) {
            if (document.cookie) {
                const array = document.cookie.split((escape(name) + '='));
                if (array.length >= 2) {
                    let arraySub = array[1].split(';');
                    return unescape(arraySub[0]);
                }
            }
        },

        /**
         * 쿠키 만료 처리
         */
        expireCookie: function(name) {
            document.cookie = encodeURIComponent(name) + "=deleted; expires=" + new Date(0).toUTCString();
        },

        /**
         * 파일 다운로드 blockUI
         * 서버단에서 응답 쿠키를 만들어 내려줄 때까지 blockUI를 유지한다.
         * @depdendency: blockUI (optional)
         */
        blockUIFileDownload: (function() {
            commons.util.blockUI();
            const downloadTimer = setInterval(function () {
                const token = commons.util.getCookie("FILE_CREATE_SUCCESS");
                if (token === "TRUE") {
                    commons.util.unblockUI();
                    clearInterval(downloadTimer);
                }
            }, 1000);
        }),

        /**
         * 함수 실행 blockUI
         * 서버단에서 응답 쿠키를 만들어 내려줄 때까지 blockUI를 유지한다.
         * @depdendency: blockUI (optional)
         */
        blockUIRequest: (function() {
            commons.util.blockUI();
            const requestTimer = setInterval(function () {
                const token = commons.util.getCookie("RESPONSE_SUCCESS");
                if (token === "TRUE") {
                    commons.util.unblockUI();
                    clearInterval(requestTimer);
                }
            }, 1000);
        }),

        /**
         * blockUI 적용한 reload
         * 서버단에서 응답 쿠키를 만들어 내려줄 때까지 blockUI를 유지한다.
         * @depdendency: blockUI (optional)
         */
        blockUIReload: (function() {
            commons.util.blockUI();
            commons.util.closeModal();
            location.reload();
        }),

        /**
         * blockUI 적용한 replace
         * 서버단에서 응답 쿠키를 만들어 내려줄 때까지 blockUI를 유지한다.
         * @depdendency: blockUI (optional)
         */
        blockUIReplace: (function(url) {
            commons.util.blockUI();
            commons.util.closeModal();
            location.replace(url);
        }),

        /**
         * blockUI 적용한 submit
         * 서버단에서 응답 쿠키를 만들어 내려줄 때까지 blockUI를 유지한다.
         * @depdendency: blockUI (optional)
         */
        blockUISubmit: (function(formSelector, actionUrl, prefunc) {
            commons.util.blockUIRequest();
            commons.util.closeModal();
            commons.util.submit(formSelector, actionUrl, prefunc);
        }),

        /**
         * ajax 공통 형식
         * @depdendency: blockUI (optional)
         */
        ajax: function(option, func, continueBlock) {
            commons.util.blockUI();
            $.ajax(
                option
            ).done(function(res) {
                if (commons.util.isNotEmpty(func)) {
                    let isSuccess = func(res);
                    if (!isSuccess) commons.util.unblockUI();
                }
            }).fail(function (data) {
                if (commons.util.hasSwal()) {
                    Swal.fire({
                        text: "처리에 실패했습니다."
                    }).then(function(){
                        Swal.fire({
                            text: JSON.stringify(data)
                        });
                    });
                } else {
                    alert("처리에 실패했습니다.");
                }
                commons.util.unblockUI();
            }).always(function () {
                if (continueBlock !== 'block') commons.util.unblockUI();
            });
        },

        /**
         * blockUI 적용한 ajax call
         * @depdendency: blockUI (optional)
         */
        blockUIAjax: function(url, method, ajaxData, func, continueBlock) {
            const option = {
                url: url,
                type: method,
                data: ajaxData,
                dataType: 'json',
                async: false
            };
            commons.util.ajax(option, func, continueBlock);
        },

        /**
         * blockUI 적용한 ajax call (file)
         * @depdendency: blockUI (optional)
         */
        blockUIFileAjax: function(url, ajaxData, func, continueBlock) {
            const option = {
                url: url,
                type: 'post',
                data: ajaxData,
                dataType: 'json',
                cache: false,
                async: false,
                processData: false,
                contentType: false
            };
            commons.util.ajax(option, func, continueBlock);
        },

        /**
         * blockUI 적용한 ajax call (async = true)
         * @depdendency: blockUI (optional)
         */
        blockUISyncAjax: function (url, method, ajaxData, func, continueBlock) {
            const option = {
                url: url,
                type: method,
                data: ajaxData,
                dataType: 'json',
                async: true
            };
            commons.util.ajax(option, func, continueBlock);
        },

        /**
         * blockUI 적용한 ajax call (json requestBody)
         * @depdendency: blockUI (optional)
         */
        blockUIJsonAjax: function(url, method, ajaxData, func, continueBlock) {
            const option = {
                url: url,
                type: method,
                data: ajaxData,
                dataType: 'json',
                async: false,
                contentType:'application/json',
                traditional: true
            };
            commons.util.ajax(option, func, continueBlock);
        },

        /**
         * form 초기화
         */
        resetForm: function(formSelector) {
            if ($(formSelector) === undefined) return;
            const $form = $(formSelector)[0];
            if ($form !== undefined) $form.reset();
        },

        /** form submit */
        submit: function(formSelector, actionUrl, prefunc) {
            const $form = $(formSelector);
            if ($form === undefined) {
                alert("form is not defined.");
                return false;
            }
            if (commons.util.isNotEmpty(prefunc)) prefunc();
            if (commons.util.isNotEmpty(actionUrl)) $form.attr("action", actionUrl);
            $form.submit();
        },

        /**
         * 문자열 맨 앞글자 대문자 변환
         */
        upperFirst: function(str) {
            if (commons.util.isEmpty(str)) return str;
            const firstChar = str.charAt(0);        // 'a'
            const firstCharUpper = firstChar.toUpperCase(); // 'A'
            const leftChar = str.slice(1, str.length); // 'pple'
            return firstCharUpper + leftChar; // 'Apple'
        },

        /**
         * 모든 table 헤더에 클릭 이벤트를 설정한다.
         */
        initSortTable: function() {
            if (typeof Page === 'undefined') { var Page = {}; }
            const tables = document.getElementsByTagName("table");
            for (let i = 0; i < tables.length; ++i) {
                const headers = tables[i].getElementsByTagName("th");
                for (let j = 0; j < headers.length; ++j) {
                    // 지역 유효범위에 생성할 중첩 함수
                    (function (table, n) {
                        headers[j].onclick = function () {
                            commons.util.sortTable(table, n, Page.tableSortMode);
                            Page.tableSortMode = (Page.tableSortMode === "REVERSE") ? "FORWARD" : "REVERSE";
                        };
                    }
                    (tables[i], j));
                }
            }
        },

        /**
         * 특정 테이블 헤더에 해당하는 열을 Sort한다.
         */
        sortTableByIdx: function(tableId, colIdx, sortMode) {
            const table = document.getElementById(tableId);
            commons.util.sortTable(table, colIdx, sortMode);
        },

        /**
         * 테이블(텍스트, 숫자) 정렬 함수
         */
        sortTable: function(table, n, sortMode) {
            if (table === undefined || table.tBodies === undefined) return;
            const tbody = table.tBodies[0];
            let rows = tbody.getElementsByTagName("tr");

            rows = Array.prototype.slice.call(rows, 0);
            rows.sort(function (row1, row2) {
                let cell1 = row1.getElementsByTagName("td")[n];
                let cell2 = row2.getElementsByTagName("td")[n];
                let value1 = cell1.textContent || cell1.innerText;
                value1 = !isNaN(Number(value1.replace(/,\./g, ""))) ? Number(value1.replace(/,\./g, "")) : value1;
                let value2 = cell2.textContent || cell2.innerText;
                value2 = !isNaN(Number(value2.replace(/,\./g, ""))) ? Number(value2.replace(/,\./g, "")) : value2;
                if (sortMode === "FORWARD") {
                    if (value1 < value2) return -1;
                    if (value1 > value2) return 1;
                } else if (sortMode === "REVERSE") {
                    if (value1 < value2) return 1;
                    if (value1 > value2) return -1;
                }
                return 0;
            });
            for (let i = 0; i < rows.length; ++i) {
                tbody.appendChild(rows[i]);
            }
        },

        /**
         * 테이블(추가추가된 input값 :: 텍스트, 숫자) 정렬 함수
         */
        sortReqstTable: function(table, n, sortMode) {
            if (table === undefined || table.tBodies === undefined) return;
            const tbody = table.tBodies[0];
            let rows = tbody.getElementsByTagName("tr");

            rows = Array.prototype.slice.call(rows, 0);
            if (rows.length < 2) return;
            rows.sort(function (row1, row2) {
                let cell1 = row1.getElementsByTagName("td")[n];
                let cell2 = row2.getElementsByTagName("td")[n];
                let value1 = cell1.getElementsByTagName("input")[0].value;
                value1 = !isNaN(Number(value1.replace(/,\./g, ""))) ? Number(value1.replace(/,\./g, "")) : value1;
                let value2 = cell2.getElementsByTagName("input")[0].value;
                value2 = !isNaN(Number(value2.replace(/,\./g, ""))) ? Number(value2.replace(/,\./g, "")) : value2;
                if (sortMode === "FORWARD") {
                    if (value1 < value2) return -1;
                    if (value1 > value2) return 1;
                } else if (sortMode === "REVERSE") {
                    if (value1 < value2) return 1;
                    if (value1 > value2) return -1;
                }
                return 0;
            });
            for (let i = 0; i < rows.length; ++i) {
                tbody.appendChild(rows[i]);
            }
        },

        /**
         * 체크박스 클릭시 라벨 변경 함수
         * "//"로 예/아니오 끊어서 문자열 넣어줌
         */
        chckboxLabel: function(attrId, ynCn, ynColor, yFunc, nFunc) {
            let $chckboxElmt = $("#" + attrId);
            if (commons.util.is$NotPresent($chckboxElmt)) {
                console.log("체크박스가 정의되지 않았습니다.");
                return false;
            }
            const separator = "//";
            const cnIdx = ynCn.indexOf(separator);
            const colorIdx = ynColor.indexOf(separator);
            const yesStr = ynCn.substring(0, cnIdx);
            const yesColor = ynColor.substring(0, colorIdx);
            const noStr = ynCn.substring(cnIdx + 2);
            const noColor = ynColor.substring(colorIdx + 2);
            $chckboxElmt.on("click", function () {
                if ($chckboxElmt.is(":checked")) {
                    $("#"+attrId+"Label").text(yesStr).css("color", yesColor);
                    if (commons.util.isNotEmpty(yFunc)) yFunc();
                } else {
                    $("#"+attrId+"Label").text(noStr).css("color", noColor);
                    if (commons.util.isNotEmpty(nFunc)) nFunc();
                }
            });
        },

        /**
         * 숫자(정수)에 콤마(,) 자동 붙이기
         * @param: value (숫자 또는 selectorStr)
         * @param: unit (나눔단위 ex.천단위)
         */
        addComma: function(value, unit) {
            if (value === "") return "";
            if (commons.util.isEmpty(unit)) unit = 1;       // 나눔 단위
            // 숫자값이 넘어오면 걍 콤마 붙인 결과값을(string) 넘겨버린다.
            const numValue = value.replace(/,/g, "");
            const isNumber = !isNaN(numValue);
            if (isNumber) {
                const divided = parseInt(numValue) / unit;
                return Number(divided).toLocaleString();
            }
            // 나머지 경우에는 selector로 간주, keyup시 천단위 콤마 처리한다.
            const selectorStr = value;
            if (commons.util.is$NotPresent(selectorStr)) return;
            const $inputs = $(selectorStr);
            $inputs.each(function(idx, elmt) {
                $(elmt).val(commons.util.addComma($(elmt).val(), unit));
                $(elmt).on("keyup", function() {
                    let localeStr = commons.util.addComma($(this).val(), unit);
                    // 콤마땜에 maxlength 넘어갈경우 처리
                    let maxlength = $(elmt).attr("maxlength");
                    if (maxlength !== undefined) localeStr = localeStr.substr(0, maxlength);
                    $(elmt).val(localeStr);
                });
            })
        },

        /**
         * 숫자에 콤마(,) 빼기
         * @param: value (숫자 또는 selectorStr)
         * @param: unit (나눔단위 ex.천단위)
         */
        removeComma: function(value, unit) {
            if (value === "") return "";
            if (commons.util.isEmpty(unit)) unit = 1;       // 나눔 단위
            // 숫자값이 넘어오면 걍 콤마 빼서 넘겨버린다.
            let numValue = value.replace(/,/g, "");
            let isNumber = !isNaN(numValue);
            if (isNumber) return Number(numValue / unit);
            // 나머지 경우에는 selector로 간주, 콤마 제거 처리 및 keyup시 콤마 제거 처리한다.
            let selectorStr = value;
            if (commons.util.is$NotPresent(selectorStr)) return;
            let $inputs = $(selectorStr);
            $inputs.each(function (idx, elmt) {
                $(elmt).val(commons.util.removeComma($(elmt).val(), unit));
                $(elmt).on("keyup", function() {
                    $(elmt).val(commons.util.removeComma($(elmt).val(), unit));
                });
            });
        },

        /**
         * 숫자에 소숫점 처리
         * @param: selectorStr (숫자 또는 selectorStr)
         * @param: fixed (자릿수)
         */
        addDot: function(value, fixed, unit) {
            if (value === "") return "";
            if (commons.util.isEmpty(fixed)) fixed = 0;
            if (commons.util.isEmpty(unit)) unit = 1;
            // 숫자값이 넘어오면 걍 콤마 빼서 넘겨버린다.
            let numValue = value.replace(/,/g, "");
            let isNumber = !isNaN(numValue);
            if (isNumber) {
                return Number(value).toLocaleString(undefined, {
                    minimumFractionDigits: fixed,
                    maximumFractionDigits: fixed
                });
            }
            // 나머지 경우에는 selector로 간주, 콤마 제거 처리 및 keyup시 콤마 제거 처리한다.
            let selectorStr = value;
            if (commons.util.is$NotPresent(selectorStr)) return;
            let $inputs = $(selectorStr);
            $inputs.each(function (idx, elmt) {
                $(elmt).val(commons.util.addDot($(elmt).val(), fixed, unit));
                $(elmt).on("keyup", function() {
                    $(this).val(commons.util.addDot($(this).val(), fixed, unit));
                });
            });
        },

        /**
         * 자간 처리 (글자가 영역 전체에 고르게 퍼치도록 처리)
         * 각 글자를 span 태그에 담아서 반환. 별도의 css 처리가 되어 있어야 한다.
         */
        letterSpacing: function(str) {
        	if (commons.util.isEmpty(str)) return;
        	let totalHtml = "", eachSpan = "";
        	let indvdLetters = Array.from(str);
            indvdLetters.forEach(spell => {
                eachSpan = "<span>" + spell + "</span>";
                totalHtml = totalHtml + eachSpan;
        	});
        	return totalHtml;
        },

        /**
         * form(list) serialize:: 갯수만큼 chunk를 넣어서 수동으로 잘라준다.
         */
        serializeJsonArray: function(arr, chunk) {
            let processedArr = [], i, j, w, form, obj, name, value;
            for (i = 0, j = arr.length; i < j; i+=chunk) {
                form = arr.slice(i,i+chunk);
                obj = {};
                for (w = 0; w < chunk; w++) {
                    if (commons.util.isEmpty(form[w])) continue;
                    name = form[w].name;
                    value = form[w].value;
                    obj[name] = value;
                }
                processedArr.push(obj);
            }
            return processedArr;
        },
        
        /**
         * Handlebars Template 공통함수 분리
         */
        handlebarsTemplate: function(data, templateStr, show) {
            if (data === undefined || data === null) data = {};
            const template = Handlebars.compile($("#"+templateStr+"_template").html().replaceAll("`", ""));
            const actual = template(data);
            $("#"+templateStr+"_div").empty().append(actual);
            if (show === "show") {
                $("#"+templateStr+"_modal").modal("show");
                // template에 있는 tooltip들 활성화
                $("[data-bs-toggle='tooltip']").tooltip();
            }
        },
        handlebarsAppend: function(data, templateStr) {
            if (data === undefined || data === null) data = {};
            const template = Handlebars.compile($("#"+templateStr+"_template").html().replaceAll("`", ""));
            const actual = template(data);
            $("#"+templateStr+"_div").append(actual);
            // template에 있는 tooltip들 활성화
            $("[data-bs-toggle='tooltip']").tooltip();
        },
        handlebarsAppendTo: function(data, templateId, trgetElmtId) {
            if (data === undefined || data === null) data = {};
            const template = Handlebars.compile($("#"+templateId+"_template").html().replaceAll("`", ""));
            const actual = template(data);
            $("#"+trgetElmtId).append(actual);
            // template에 있는 tooltip들 활성화
            $("[data-bs-toggle='tooltip']").tooltip();
        },

        /**
         * button 일시 잠김 (딜레이 3초)
         */
        delayBtn: function(elmt, sec) {
            if (elmt instanceof jQuery) elmt = elmt[0];
            if (elmt === undefined) return;
            if (sec === undefined) sec = 2;
            elmt.disabled = true;
            setTimeout(function(){ elmt.disabled=false; }, sec * 1000);
        },

        /**
         * tagify
         */
        tagify: function(selectorStr) {
            // 게시물 태그 tagify
            const tagInput = document.querySelector(selectorStr);
            return new Tagify(tagInput, {
                whitelist: [],
                maxTags: 21,
                keepInvalidTags: false,
                skipInvalid: true,
                // duplicate 허용하고 수동 로직으로 중복 처리
                duplicates: false,
                editTags: {
                    clicks: 2,
                    // if after editing, tag is invalid, auto-revert
                    keepInvalid: false
                },
                templates: {
                    tag: function (tagData) {
                        // 태그 메타데이터 (data)를 문자열로 변환하여 표시
                        const ctgr = commons.util.isNotEmpty(tagData.data) ? tagData.data.ctgr : "";
                        const ctgrSpan = ctgr !== "" ? `<span class="tagify__tag-category text-noti me-1">[${tagData.data.ctgr}]</span>` : "";
                        return `<tag title="${tagData.value}" contenteditable="false" spellcheck="false" tabindex="-1"
                                     class="tagify__tag" value="${tagData.value}" data-ctgr="${ctgr}">
                                    <x title="" class="tagify__tag__removeBtn" role="button" aria-label="remove tag"></x>
                                    <div>
                                        <!-- 메타데이터 시각화 -->
                                        ${ctgrSpan}
                                        <span class="tagify__tag-text">${tagData.value}</span>
                                    </div>
                                </tag>`;
                    }
                },
            });
        },

        /**
         * tagify :: 카테고리 기능 추가
         */
        tagifyWithCtgr: function(selectorStr, tagCtgrMap) {
            const tagify = commons.util.tagify(selectorStr);
            // 태그 카테고리 입력 UI 존재시에만 활성화됨
            const parts = selectorStr.split(' ');
            const tagScope = (parts.length > 1) ? document.querySelector(parts[0]) : document;

            const categoryInputContainer = tagScope.querySelector('#tag_ctgr_div');
            const tagCtgrInput = tagScope.querySelector('#tag_ctgr');
            if (!categoryInputContainer || !tagCtgrInput) return tagify;

            // 수동 중복 체크 위해 중복 제한 제거
            tagify.settings.duplicates = true;

            const metaInfoContainer = tagScope.querySelector('#tag_ctgr_select_div');
            const metaInfoSelect = tagScope.querySelector('#tag_ctgr_select');
            // 태그 자동완성
            tagify.on("input", function(e) {
                if (!tagCtgrMap) return;
                const value = e.detail.value;
                tagify.settings.whitelist = Object.keys(tagCtgrMap).filter(tag => tag.startsWith(value));
                tagify.dropdown.show(value); // Show the suggestions dropdown
            });
            // 태그 추가시 카테고리 입력 칸 prompt
            tagify.on("add", function(e) {
                // 기본 태그 (카테고리 붙이기 전) 처리시에만 동작
                const newTag = e.detail.data;
                const baseTagProc = newTag.data === undefined;
                if (!baseTagProc) return;
                // setTimeout을 사용하여 포커스 호출 지연
                setTimeout(() => {
                    // 1. 새로 추가된 임시 태그 마킹
                    const addedTagElmt = tagify.getTagElms()[tagify.getTagElms().length - 1];
                    addedTagElmt.setAttribute('data-marked', 'true');  // 마킹

                    // 2. 카테고리 입력 칸 prompt
                    const newValue = newTag.value;
                    categoryInputContainer.style.display = 'block';
                    tagCtgrInput.value = '';
                    tagCtgrInput.dataset.tagValue = newValue;
                    tagCtgrInput.focus();

                    // 3. 카테고리 맵 정의시: selectbox 세팅
                    metaInfoContainer.style.display = 'none';
                    if (!tagCtgrMap) return;
                    const predefinedCtgr = tagCtgrMap[newTag.value];
                    if (!predefinedCtgr) return;
                    const filteredCtgr = predefinedCtgr.filter(item => item !== "");
                    if (filteredCtgr.length === 0) return;
                    // 초기화 및 직접입력 추가
                    metaInfoSelect.innerHTML = '<option value="custom">직접입력</option>';
                    // 사전 정의된 카테고리 옵션 추가
                    filteredCtgr.forEach(function(item) {
                        const option = document.createElement('option');
                        option.value = item;
                        option.textContent = item;
                        metaInfoSelect.appendChild(option);
                    });
                    // 메타정보 컨테이너 표시
                    metaInfoSelect.size = filteredCtgr.length + 1;
                    metaInfoContainer.style.display = 'block';
                    // 자동완성 선택 이벤트 핸들러
                    metaInfoSelect.onchange = function() {
                        const selectedCtgr = metaInfoSelect.value;
                        if (selectedCtgr !== "custom") {
                            tagify.removeTags(addedTagElmt);
                            tagify.addTags([{ "value": newValue, "data": { "ctgr": selectedCtgr } }]);
                            metaInfoContainer.style.display = 'none';  // 선택 후 컨테이너 숨김
                        } else {
                            // 직접입력 선택 시 입력 필드로 포커스 이동
                            categoryInputContainer.style.display = 'block';
                            tagCtgrInput.value = '';
                            tagCtgrInput.dataset.tagValue = newValue;
                            tagCtgrInput.focus();
                        }
                    };
                }, 0);
            });
            // 추가된 태그가 제거될 때 ctgr 관련 숨기기
            tagify.on("remove", function() {
                metaInfoContainer.style.display = 'none';
                categoryInputContainer.style.display = 'none';
                const markedTags = tagScope.querySelectorAll('[data-marked="true"]');
                markedTags.forEach(tagElmt => {
                    tagElmt.removeAttribute('data-marked');
                });
            });
            // 카테고리 입력칸에 이벤트리스너 추가 (ESC 또는 탭)
            tagCtgrInput.addEventListener('keydown', function(event) {
                metaInfoContainer.style.display = 'none';
                const markedTags = tagScope.querySelectorAll('[data-marked="true"]');
                if (event.key === 'Escape') {
                    // ESC = 태그 추가 없이 빠져나감
                    event.preventDefault();
                    categoryInputContainer.style.display = 'none';
                    setTimeout(() => {
                        if (tagify.DOM.input) tagify.DOM.input.focus();
                    }, 0);
                    markedTags.forEach(tagElmt => {
                        tagElmt.removeAttribute('data-marked');
                    });
                } else if (event.key === 'Tab') {
                    // TAB = 빈칸 아닐시 카테고리 추가
                    event.preventDefault();
                    const newCtgr = tagCtgrInput.value;
                    const newValue = tagCtgrInput.dataset.tagValue;

                    // 중복 체크
                    // (카테고리 미추가헐 경우 마킹한 임시태그는 이 과정에서 자연스레 없어짐.)
                    const tags = tagify.getTagElms(); // 모든 태그 DOM 요소 가져오기
                    Array.from(tags).forEach(existingTagElmt => {
                        const existingTagData = tagify.getSetTagData(existingTagElmt);
                        if (existingTagData.value !== newValue) return;
                        const existingCtgr = existingTagData.data ? existingTagData.data.ctgr : "";
                        if (existingCtgr === newCtgr) tagify.removeTags(existingTagElmt);  // 마킹된 태그 제거
                    });
                    // 새 태그 추가
                    tagify.addTags([{ "value": newValue, "data": { "ctgr": newCtgr } }]);
                    // 카테고리 붙인 걍우 임시 태그를 찾아 제거
                    markedTags.forEach(tagElmt => {
                        tagElmt.removeAttribute('data-marked');
                        if (newCtgr) tagify.removeTags(tagElmt);  // 마킹된 태그 제거
                    });
                    categoryInputContainer.style.display = 'none';  // 카테고리 입력 필드 숨김
                    setTimeout(() => {
                        if (tagify.DOM.input) tagify.DOM.input.focus();
                    }, 0);
                }
            });
            return tagify;
        },

        /**
         * 전체 모달 닫기
         */
        closeModal: function() {
            $(".modal").modal("hide");
        },

        /**
         * 페이지 상단으로 이동
         */
        toPageTop: function() {
            $('html, body').animate({
                scrollTop : 0
            }, 300);
        }

    }
})();
