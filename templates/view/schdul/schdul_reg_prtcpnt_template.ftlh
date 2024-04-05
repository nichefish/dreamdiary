<#--
  -- 일정 > 전체 일정 > 일정 등록 Template
  -- @included_in : schdul_cal
  -->
<script id="schdul_reg_prtcpnt_template" type="text/x-handlebars-template">
    <div id="prtcpntRow{{idx}}" class="d-flex align-items-center mb-1">
        <select name="prtcpntList[{{idx}}].userId" class="form-select form-select-solid me-2">
            <option value="">--선택--</option>
            <#list crtdUserList![] as user>
                <option value="${user.userId!}" id="${user.userId!}">${user.userNm!}</option>
            </#list>
        </select>
        <span class="btn btn-sm btn-icon btn-light-danger" onclick="Schdul.removePrtcpnt({{idx}});">
            <i class="fas fa-trash float-end"></i>
        </span>
    </div>
</script>