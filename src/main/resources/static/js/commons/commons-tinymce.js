/**
 * commons-tinymce.js
 * @namespace: commons.tinymce
 * @author: nichefish
 * @since: 2022-06-27
 * @last-modified: 2022-08-04
 * @last-modieied-by: nichefish
 * @depdendency: tinymce.js
 * 공통 - tinymce 에디터 관련 함수 모듈
 * (노출식 모듈 패턴 적용 :: commons.tinymce.init("#aaa", func) 이런식으로 사용)
 */
if (typeof commons === 'undefined') { var commons = {}; }
commons.tinymce = (function() {
    return {
        /**
         * tinymce 에디터 init
         * @depdendency : tinyMCE
         */
        init: function(selectorStr, imgFunc) {
            if ($(selectorStr) === undefined) return;
            let options = {
                selector: selectorStr,
                editor_encoding: "raw",
                height: "360px !important",
                menubar: false,
                branding: false,
                statusbar: false,
                default_link_target: "_blank",
                convert_urls: false,
                /* textpattern */
                plugins: 'help quickbars searchreplace link autolink pageembed table lists advlist checklist emoticons hr visualchars visualblocks pagebreak code codesample',
                toolbar1: 'undo redo | searchreplace | styles styleselect fontselect fontsizeselect | bold italic underline strikethrough | forecolor backcolor | align | visualchars visualblocks pagebreak | code codesample | help',
                toolbar2: 'emoticons custom_image link pageembed | hr | numlist bullist checklist moreless | table tabledelete | tableprops tablerowprops tablecellprops | tableinsertrowbefore tableinsertrowafter tabledeleterow | tableinsertcolbefore tableinsertcolafter tabledeletecol',
                contextmenu: 'link custom_image lists table',
                /* https://www.tiny.cloud/docs/plugins/opensource/textpattern/
                textpattern_patterns: [
                    {start: '*', end: '*', format: 'italic'},
                    {start: '**', end: '**', format: 'bold'},
                    {start: '~', end: '~', cmd: 'createLink', value: 'https://tiny.cloud'}
                ], */
                setup: function (editor) {
                    // 자동 이스케이핑
                    editor.on('SaveContent', function (e) {
                        e.content = e.content.replace(/&#39/g, '&apos').replace(/&amp;/g, '&');
                    });
                    // 하단 option들 메뉴에 붙여두는 기능
                    editor.on('PostRender', function(e) {
                        var container = editor.getContainer();
                        var uiContainer = document.querySelectorAll('.tox-tinymce-aux');
                        uiContainer.forEach((c) => {
                            container.parentNode.appendChild(c);
                        });
                    });
                    // 이미지 업로드 로직
                    editor.ui.registry.addButton('custom_image', {
                        icon: 'image',
                        tooltip: 'insert Image',
                        onAction: function() {
                            imgFunc();
                        }
                    });
                    // 글접기/펼치기 로직
                    commons.tinymce.sectionCount = 0;
                    editor.ui.registry.addButton('moreless', {
                        icon: 'vertical-align',
                        tooltip: 'insert moreless',
                        onAction: function() {
                            commons.tinymce.morelessFunc();
                        }
                    });
                }
            };
            tinymce.init(options);
        },
        /**
         * tinymce 에디터 destroy
         */
        destroy: function(selector) {
            if (typeof tinymce != 'undefined' && tinymce != null) tinymce.remove([selector]);
        },
        /**
         * tinymce 에디터 이미지 첨부
         */
        imgRegFunc: function() {
            let $fileInput = $("#atchFile0");
            $fileInput.click();
            $fileInput.on("change", function() {
                if (this.value !== "") {
                    if (!commons.validate.fileSizeChck(this)) return false;      // fileSizeChck
                    if (!commons.validate.fileExtnChck(this, "jpg|jpeg|png")) return false;      // fileExtnChck
                    const url = "/file/fileUploadAjax.do";
                    let ajaxData = new FormData($("#tinymceImageForm")[0]);
                    commons.util.blockUIFileAjax(url, ajaxData, function(res) {
                        if (commons.util.isNotEmpty(res.message)) alert(res.message);
                        if (res.result === true) {
                            let fileInfo = res.resultObj;
                            let imgTag = "<img src='" + fileInfo.url + "' data-mce-src='" + fileInfo.url + "' data-originalFileName='" + fileInfo.orgnFileNm + "' >";
                            tinymce.execCommand('mceInsertContent', true, imgTag);
                            // file input 초기화
                            $("#atchFile0").val("");
                            let $imgFileDiv = $("#tinymceImageTemplate");
                            let fileInputHtml = $imgFileDiv.html();
                            $imgFileDiv.empty().html(fileInputHtml);
                        }
                    });
                }
            });
        },
        morelessFunc: function() {
            if (typeof commons.tinymce.sectionCount === "undefined") commons.tinymce.sectionCount = 0;
            const sectionId = `tinymce_section_` + commons.tinymce.sectionCount;
            const sectionContentId = `tinymce_section_content_` + commons.tinymce.sectionCount;
            const toggleId = `tinymce_toggle_` + commons.tinymce.sectionCount;

            const newSection = `
                <div class="tinymce-section" id="${sectionId}">
                  <span id="${toggleId}" class="tinymce-collapse-toggle">
                    Toggle Section ${commons.tinymce.sectionCount}
                  </span>
                  <div id="${sectionContentId}" class="tinymce-collapsed">
                    Section content goes here.
                  </div>
                </div>
              `;
            tinymce.execCommand('mceInsertContent', false, newSection);


            commons.tinymce.sectionCount++;
            console.log("sectionCount: " + commons.tinymce.sectionCount);
            // const toggleElement = document.getElementById(toggleId);
            // toggleElement.addEventListener('click', () => toggleCollapse(toggleId, sectionContentId));

            // function toggleCollapse(toggleId, sectionContentId) {
            //     const contentElement = document.getElementById(sectionContentId);
            //     if (contentElement) {
            //         contentElement.classList.toggle('collapsed');
            //     }
            // }
        }
    }
})();
