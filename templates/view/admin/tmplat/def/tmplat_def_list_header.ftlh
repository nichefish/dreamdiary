<#--
  -- 공지사항 > 목록 Header
  -- @included_in : notice_list
  -->
<form name="listForm" id="listForm" method="get">
    <input type="hidden" name="pageNo" id="pageNo" value="${searchParam.pageNo!1}">
    <input type="hidden" name="pageSize" id="pageSize" value="${searchParam.pageSize!10}">
    <div class="row mb-8 p-0">
        <div class="col-lg-6 col-12 align-items-center d-flex my-2">
            <div class="position-relative me-md-2 col-lg-2 col-4 searchType">
                <!-- 인풋:: ctgrCd -->
                <@component.input_header_ctgrCd searchParam TMPLAT_CTGR_CD!false />
            </div>
            <div class="position-relative me-md-2 col-lg-2 col-4 searchType">
                <select name="searchType" id="searchType" class="form-select form-select-solid fw-bolder form-select-sm py-3">
                    <option value="title">제목</option>
                    <option value="nickNm">작성자</option>
                </select>
            </div>
            <div class="position-relative me-md-2 col-lg-4 col-4 searchType">
                <!-- 인풋:: searchKeyword -->
                <@component.input_header_searchKeyword "Page" />
            </div>
            <div class="col-lg-4 col-4">
                <!-- 버튼:: 목록 검색 -->
                <@component.btn_header_search "Page" />
                <!-- 내 글 보기 버튼 -->
                <@component.btn_header_mypapr "Page" MY_PAPR!false />
            </div>
        </div>
        <div class="col-lg-6 col-12 d-flex justify-content-end align-items-center buttons">
            <a href="javascript:void(0);" class="btn btn-primary mx-3 btn-sm" onclick="Page.regForm();"
               data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="등록 팝업을 호출합니다.">
                <i class="fas fa-plus"></i>등록
            </a>
        </div>
    </div>
</form>
