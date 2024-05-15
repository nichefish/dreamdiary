<#--
 * 레이아웃 > 태그 모듈
 -->

<#--begin::Row-->
<div class="row">
    <div>
        <label class="mb-2">
            <span class="text-gray-700 fs-6 fw-bolder">태그</span>
            <span class="text-gray-500 fs-9 mx-2">※ 태그는 최대 15개, 50자까지 추가 가능합니다.</span>
        </label>
    </div>
    <div class="col-xl-12 text-sm-start" id="tagSpan">
        <input name="tag.tagListStr" id="tagListStr" class="form-control form-control-solid no-space" autocomplete="off" value="<#if post.tag??>${post.tag.tagListStr!}</#if>"/>
    </div>
</div>