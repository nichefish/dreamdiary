/**
 * commons-jstree.js
 * @namespace: commons.jstree
 * @author: nichefish
 * @since: 2022-06-27
 * @dependency: jstree.js
 * 공통 - jstree(라이브러리) 관련 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.jstree.init("#userId") 이런식으로 사용)
 */
if (typeof commons === 'undefined') { var commons = {}; }
commons.jstree = (function() {
    return {
        /**
         * 공통 :: jstree 초기화
         * possible plugins :: "dnd" "state" "changed" "contextmenu" "conditionalselect"
         */
        init: function(treeSelector) {
            if ($(treeSelector) === undefined) return;
            $(treeSelector).jstree({
                "plugins": [ "checkbox",  "search", "wholerow" ],
                "core" : {
                    "animation": 0,
                    "check_callback": true,
                    "expand_selected_onload": true,
                    "data" : {  }
                },
            }).init();
        },

        /**
         * 공통 :: jstree 트리 리셋
         */
        reset: function(treeSelector) {
            if ($(treeSelector) === undefined) return;
            $(treeSelector).jstree("destroy").empty();
        }
    }
})();
