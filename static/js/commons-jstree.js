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
         * @param {string} treeSelector - jstree를 초기화할 요소의 선택자.
         */
        init: function(treeSelector) {
            const $treeElmt = $(treeSelector);
            if ($treeElmt.length === 0) return;

            $treeElmt.jstree({
                "plugins": [ "checkbox",  "search", "wholerow" ],
                "core" : {
                    "animation": 0,
                    "check_callback": true,
                    "expand_selected_onload": true,
                    "data" : {}
                },
            });
        },

        /**
         * 공통 :: jstree 트리 리셋
         * @param {string} treeSelector - jstree를 리셋할 요소의 선택자.
         */
        reset: function(treeSelector) {
            const $treeElmt = $(treeSelector);
            if ($treeElmt.length === 0) return;

            // jstree 파괴 및 비우기
            $treeElmt.jstree("destroy").empty();
        }
    }
})();
