<!--
 -- 레이아웃 > 모달 레이아웃
 -- metronic v8.2.5
 -->

<#macro layout id size>
    <div class="modal fade" id="${id}_modal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-${size}" role="document">
            <div class="modal-content">
                <#nested>
            </div>
        </div>
    </div>
</#macro>