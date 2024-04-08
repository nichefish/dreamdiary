<#--
  -- 게시판 정의 > 목록 툴바 Header
  -- @included_in : board_def_list
  -->

<form name="listForm" id="listForm" class="d-flex align-items-center flex-nowrap gap-2" method="get">
    <div>
        <!-- 버튼:: 등록 모달 호출하기 -->
        <@component.btn_header_reg_modal "Page.regModal();" />
    </div>
    <div>
        <!-- 버튼:: 정렬순서 -->
        <@component.btn_header_sort_ordr "Page.sortOrdr();" />
    </div>
</form>
