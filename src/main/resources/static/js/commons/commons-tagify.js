/**
 * commons-tagify.js
 * @namespace: commons.taf
 * @author: nichefish
 * @since: 2022-06-27~
 * @dependency: jquery.blockUI.js, jquery.forms.js
 * 공통 - 일반 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.util.enterKey("#userId") 이런식으로 사용)
 */
if (typeof commons === 'undefined') { var commons = {}; }
commons.tagify = (function() {
    return {
        /**
         * tagify init
         */
        init: function(selectorStr) {
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
         * tagify init :: 카테고리 기능 추가
         */
        initWithCtgr: function(selectorStr, tagCtgrMap) {
            const tagify = commons.tagify.init(selectorStr);
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
    }
})();
