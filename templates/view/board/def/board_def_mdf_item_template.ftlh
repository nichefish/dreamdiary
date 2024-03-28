<#--
  -- 게시판 정의 > 항목 수정 Template
  -- @included in : board_def_list
  -->
<script type="text/x-handlebars-template" id="board_def_mdf_item_template">
    <!--begin::Question-->
    <div class="fs-4 mx-4 px-4 fw-normal text-gray-800">
        <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
            <!--begin::Table body-->
            <tbody>
                <tr class="bg-hover-secondary">
                    <td class="col-2 text-start col-form-label">
                        {{mdfItemNm}} :
                    </td>
                    <td class="col-2 text-start">
                        <b class="border-bottom">{{boardCd}}</b>
                        <input type="hidden" name="boardCd" value="{{boardCd}}">
                    </td>
                    <td class="col-4 text-start">
                        <div class="d-flex align-items-center">
                            <em class="text-danger px-2">*</em>
                            <input type="text" class="form-control required" name="{{mdfItem}}" id="{{mdfItem}}" value="{{mdfItemVal}}">
                        </div>
                    </td>
                    <td class="col-2 text-center">
                        <button type="button" class="btn btn-sm btn-primary" onclick="Page.mdfItemAjax();">
                            <i class="bi bi-pencil-square"></i>저장
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</script>