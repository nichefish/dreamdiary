/**
 * commons-pdf.js
 * @namespace: commons.pdf
 * @author: nichefish
 * @since: 2022-06-27
 * @depdendency: html2pdf
 * 공통 - pdf 다운로드 관련 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.pdf.fnPdf("#id") 이런식으로 사용)
 */
if (typeof commons === 'undefined') { var commons = {}; }
commons.pdf = (function() {
    return {
        /**
         * pdf 관련 설정
         *
         */
        fnPdf: function(elmtId) {
            const elmt = document.getElementById(elmtId);
            const options = {
                margin: 0,
                filename: 'test.pdf',
                image: { type: 'jpeg', quality: 1 },
                html2canvas: { dpi: 300, letterRendering: true },
                jsPDF: {orientation: 'portrait', unit: 'mm', format: 'a4', compressPDF: true},
                pagebreak: {
                    mode: ['avoid-all', 'css'],
                    before: [ '#pdfContent' ]
                }
            };
            html2pdf().from(elmt).set(options).save();
        }
    }
})();

