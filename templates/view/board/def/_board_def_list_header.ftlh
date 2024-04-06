<#--
  -- 게시판 정의 > 목록 툴바 Header
  -- @included_in : board_def_list
  -->
<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap" method="get">
    <!-- 버튼:: 등록하기 -->
    <div class="me-md-2">
        <button type="button" class="btn btn-sm btn-primary"
             onclick="Page.regModal();"
             data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="등록 팝업을 호출합니다.">
            <i class="fas fa-plus"></i>등록
        </button>
    </div>
</form>
