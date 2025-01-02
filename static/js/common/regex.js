/**
 * commons.ts
 * @namespace: cF.util
 * @author: nichefish
 * @dependency: jquery.blockUI.js, jquery.forms.js
 * 공통 - 일반 함수 모듈
 * (노출식 모듈 패턴 적용 :: cF.util.enterKey("#userId") 이런식으로 사용)
 */
if (typeof cF === 'undefined') {
    let cF = {};
}
cF.validate = (function () {
    return Object.freeze({
        /** 정규식: 숫자 */
        num: /[0-9]/g,
        /** 정규식: 숫자 빼고 나머지 */
        nonNum: /[^0-9]/g,
        /** 정규식: 숫자 및 점 */
        numAndDot: /[0-9.]/g,
        /** 정규식: 숫자 및 점 빼고 나머지 */
        nonNumAndDot: /[^0-9.]/g,
        /** 정규식: 숫자 및 쉼표(,) */
        numAndComma: /[^0-9,]/g,
        /** 정규식: 숫자 및 쉼표(,) 빼고 나머지 */
        nonNumAndComma: /[^0-9,]/g,
        /** 정규식: 숫자 및 점(.) 슬래시(/) */
        numDotAndSlash: /[^0-9.\/]/g,
        /** 정규식: 숫자 및 점(.) 슬래시(/) 빼고 나머지 */
        nonNumDotAndSlash: /[^0-9.\/]/g,
        /** 정규식: 숫자 및 대시(-) 슬래시(/) 콜론(:) 띄어쓰기 */
        dt: /[0-9-:\/\s]/g,
        /** 정규식: 숫자 및 대시(-) 슬래시(/) 콜론(:) 띄어쓰기 빼고 나머지 (날짜 표시에 사용) */
        nonDt: /[^0-9-:\/\s]/g,
        /** 정규식: 숫자 및 영문 대소문자 */
        engNum: /[^A-Za-z0-9]/g,
        /** 정규식: 숫자 및 영문 대소문자 빼고 나머지 */
        nonEngNum: /[^A-Za-z0-9]/g,
        /** 정규식: 숫자 및 영문 대소문자 언더바 (코드 정보에 사용) */
        cd: /[^A-Za-z0-9_]/g,
        /** 정규식: 숫자 및 영문 대소문자 언더바 빼고 나머지 (코드 정보에 사용) */
        nonCd: /[^A-Za-z0-9_]/g,
        /** 정규식: IP주소(v4) */
        ipv4: /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
        /** 정규식: IP주소(v4) (CIDR) */
        ipv4Cidr: /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)($|\/(0?[1-9]|[12][0-9]|3[01])$)/,
        /** 정규식: 아이디 정규식 */
        id: /^(?=.*[a-z])[a-z\d]{5,16}$/,
        pw: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&_])[A-Za-z\d$@$!%*#?&_]{9,20}$/,
        /** 정규식: 천 단위 콤마 */
        thousandSeparator: /\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g,
    });
})();