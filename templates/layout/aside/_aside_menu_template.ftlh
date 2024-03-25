<!--
 -- 레이아웃 > 기본 레이아웃
 -->
<#macro layout siteMenu>
    <#assign isNotTopMenu = (topMenuNo != siteMenu.topMenuNo) />
    <div class="menu-item show" id="${siteMenu.menuNo!}">
        <a href="${siteMenu.url!}" class="menu-link <#if isNotTopMenu>text-inverse-light</#if>">
            <span class="menu-icon">
                <span class="svg-icon svg-icon-3 svg-icon-gray-600">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bell-fill" viewBox="0 0 16 16">
                        <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2zm.995-14.901a1 1 0 1 0-1.99 0A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-2-5.902-2-7 0-2.42-1.72-4.44-4.005-4.901z"/>
                    </svg>
                </span>
            </span>
            <span class="menu-title <#if isNotTopMenu>text-inverse-light</#if>">${siteMenu.menuNm!}</span>
        </a>
    </div>
</#macro>