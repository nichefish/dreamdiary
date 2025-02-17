/**
 * pickr.ts
 * 공통 - pickr 관련 함수 모듈
 *
 * @namespace: cF.pickr (노출식 모듈 패턴)
 * @author: nichefish
 * @see "https://github.com/simonwep/pickr"
 */
// @ts-ignore
if (typeof cF === 'undefined') { var cF = {} as any; }
cF.pickr = (function(): Module {
    /** 기본 색상 배열 */
    const defaultColorArr = [
        "#d3d3d3",
        "#bf4141",
        "#ff9750",
        "#fdffb6",
        // ,,,
    ];

    /** 기본 색상 배열 */
    const i18n = {
        // Strings visible in the UI
        'ui:dialog': 'color picker dialog',
        'btn:toggle': 'toggle color picker dialog',
        'btn:swatch': 'color swatch',
        'btn:last-color': 'use previous color',
        'btn:save': 'Save',
        'btn:cancel': 'Cancel',
        'btn:clear': 'Clear',

        // Strings used for aria-labels
        'aria:btn:save': 'save and close',
        'aria:btn:cancel': 'cancel and close',
        'aria:btn:clear': 'clear and close',
        'aria:input': 'color input field',
        'aria:palette': 'color selection area',
        'aria:hue': 'hue selection slider',
        'aria:opacity': 'selection slider'
    };

    return {
        /**
         * 색상 선택기를 초기화하고 변경 이벤트를 처리합니다.
         * @param {string} selectorStr - 색상 선택기를 초기화할 DOM 요소의 선택자 문자열.
         * @param {string} initColor - 초기 색상 값 (선택적).
         * @param {Array} [initColorArr=defaultColorArr] - 초기 색상 배열 (선택적).
         * @returns {Pickr} - Pickr 인스턴스.
         */
        init: function(selectorStr: string, initColor: string, initColorArr: string[] = defaultColorArr): Pickr {
            // 색상 배열 들어올시 기본배열에 추가
            if (initColor) initColorArr = [initColor, ...initColorArr];

            const pickr = Pickr.create({
                el: selectorStr,
                theme: 'nano',
                swatches: initColorArr,
                components: {
                    /** main components */
                    preview: true,
                    opacity: true,
                    hue: true,
                    default: initColor,
                    /** input/output option */
                    interaction: {
                        hex: true,
                        rgba: true,
                        input: true,
                        clear: true,
                        /** save: true */
                    },
                    // i18n
                    i18n: i18n
                }
            });

            // 초기화 시 초기 색상 설정
            pickr.on("init", function(): void {
                if (initColor) pickr.setColor(initColor);
            });

            // 색상 변경 시 처리 로직
            pickr.on("change", function(color: any): void {
                const colorCd = color.toRGBA().toString();
                const idx = pickr.options.el.id.replace("color-picker", "");
                const trimmedColorCd = colorCd.replace(/\.(.*?\d*),/g, ",").replace(/ /g, "");
                (document.querySelector("#colorCd" + idx) as HTMLInputElement).value = trimmedColorCd;
                (document.querySelector("#colorCdHidden" + idx) as HTMLInputElement).value = trimmedColorCd;
                pickr.applyColor();
            })

            return pickr;
        },
    }
})();
