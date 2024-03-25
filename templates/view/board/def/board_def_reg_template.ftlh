<#--
  -- 게시판 정의 > 등록 Template
  -- @included in : board_def_list
  -->
<script type="text/x-handlebars-template" id="board_def_reg_template">
    <!--begin::Table body-->
    <tr>
        <td class="text-start px-2 pt-2">
            <input type="text" class="form-control required" name="boardCd">
            <input type="hidden" class="form-control required" name="useYn" value="N">
        </td>
        <td class="text-center pt-2">
            <input type="text" class="form-control required" name="boardNm">
        </td>
        <td class="text-center pt-2">
            <input type="text" class="form-control" name="ctgrClCd">
        </td>
        <td class="text-center pt-2">
            <input type="text" class="form-control required number" name="menuNo">
        </td>
        <td class="text-center pt-2 pe-2">
            <input type="text" class="form-control required number" name="sortOrdr">
        </td>
        <td class="text-center px-2">
            -
        </td>
    </tr>
</script>