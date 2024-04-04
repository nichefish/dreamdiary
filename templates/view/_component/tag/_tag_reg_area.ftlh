<!--
 -- 레이아웃 > 첨부파일 모듈
 -- @included_in : 파일 첨부 기능이 있는 페이지들
 -->
<script>
    let Tags = function() {
        return {
            init: function () {
                // 게시물 태그 tagify
                let boardTagInput = document.querySelector("#tagListStr");
                <#if (isMdf!false)>
                    <#if post.tag??>
                        boardTagInput.value = "${post.tag.tagListStr!}";
                    </#if>
                </#if>
                new Tagify(boardTagInput, {
                    maxTags: 10,
                    // pattern: commons.validate.ipv4regex,
                    // 특수문자 제외?몇몇개만 허용?
                    keepInvalidTags: false,
                    skipInvalid: true,
                    duplicates: false,
                    editTags: {
                        clicks: 2,
                        keepInvalid: false      // if after editing, tag is invalid, auto-revert
                    },
                });
            }
        }
    }();

    $(document).ready(function () {
        Tags.init();
    });
</script>

<!--begin::Row-->
<div class="row">
    <div id="board_tag_list_div">
        <label class="mb-2">
            <span class="text-gray-700 fs-6 fw-bolder">태그</span>
            <span class="text-gray-500 fs-9 mx-2">※ 태그는 최대 10개, 50자까지 추가 가능합니다.</span>
        </label>
    </div>
    <div class="col-xl-12 text-sm-start" id="tagSpan">
        <input name="tag.tagListStr" id="tagListStr" class="form-control form-control-solid no-space" autocomplete="off" value="<#if post.tag??>${post.tag.tagListStr!}</#if>"/>
    </div>
</div>