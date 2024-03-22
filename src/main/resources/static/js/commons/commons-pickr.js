/**
 * commons-pickr.js
 * @namespace: commons.pickr
 * @author: nichefish
 * @since: 2022-06-27~
 * @last-modified: 2022-08-04
 * @last-modieied-by: nichefish
 * 공통 - Pickr (색상선택 툴) 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.util.enterKey("#userId") 이런식으로 사용)
 * https://github.com/simonwep/pickr
 */
if (typeof commons === 'undefined') { var commons = {}; }
commons.pickr = (function() {
    return {
        defaultColorArr: [
            "#d3d3d3",
            "#bf4141",
            "#ff9750",
            "#fdffb6",
            // ,,,
        ],
        init: function(selectorStr, initColor, initColorArr) {
            if (initColorArr === undefined) initColorArr = commons.pickr.defaultColorArr;
            if (initColor !== undefined) initColorArr.unshift(initColor);
            let pickr = Pickr.create({
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
                    i18n: {
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
                    }
                }
            });
            pickr.on("init", function(pickrInstance) {
                if (initColor !== undefined) pickr.setColor(initColor);
            });
            pickr.on("change", function(color, e, pickrInstance) {
                let colorCd = color.toRGBA().toString();
                let idx = $(pickr.options.el).attr("id").replace("color-picker", "");
                let trimmedColorCd = colorCd.replace(/\.(.*?\d*),/g, ",").replace(/ /g, "");
                $("#colorCd"+idx).val(trimmedColorCd);
                $("#colorCdHidden"+idx).val(trimmedColorCd);
                pickr.applyColor();
            })
        },

    }
})();
