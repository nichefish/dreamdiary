<#--
  -- 활동 로그 목록 화면
  -->
<#import "/layout/layout.ftlh" as layout >
<#import "/spring.ftl" as spring />

<#--<script defer="defer" src="/static/react/static/js/main.js"></script>
<link rel="stylesheet" href="/static/react/static/css/main.css">-->

<@layout.layout>

<script>
    const Page = function() {
        return {
            /** init */
            init: function () {
                // 모든 table 헤더에 클릭 이벤트를 설정한다.
                Page.sortMode = "FORWARD";
                commons.util.fnSortTable();

                Page.setIframe('da2e0bf52cec445eb5edc7990e1471ee');
            },
            setIframe: function (pageId) {
                const iframe = $('<iframe>', {
                  src: 'http://115.144.8.81:3000/' + pageId,
                  id: 'myFrame',
                  frameborder: 0,
                  scrolling: 'yes',
                  width: '100%',
                  height: '700px',
                  css: {
                    border: 'none'
                  }
                }).appendTo('#react_notion_root');
            }
        }
    }();

    $(document).ready(function() {
        Page.init();
    });
</script>

<div class="card post">
    <div id="notion_page_rendered">
        ${notionPage!?no_esc}
    </div>

    <div id="react_notion_root">
        <!-- Handlebars Template goes in here -->
    </div>
</div>
<!--end::Card-->

</@layout.layout>
