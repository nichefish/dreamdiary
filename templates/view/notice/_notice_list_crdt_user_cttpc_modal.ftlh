<#--
  -- 게시판 > 직원연락처 Modal (슬기로운 회사생활)
  -- @included_in : board_post_list
  -->

<div class="modal fade" id="crdt_user_cttpc_modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="crdtUserCttpcModalLongTitle">[직원 연락처 목록]</h5>
                <!--begin::Close-->
                <button type="button" class="btn btn-sm btn-light-primary me-md-2 ms-auto"
                        onclick="Page.cttpcXlsxDownload();"
                        data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="데이터를 엑셀 파일로&#10;다운로드합니다.">
                    <i class="fas fa-download fs-7"></i>다운로드
                </button>
                <div class="btn btn-sm btn-icon btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="fas fa-times"></i>
                </div>
                <!--end::Close-->
            </div>
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body my-5">
                <div class="table-responsive-sm">
                    <table class="table table-row-bordered table-row-gray-300 gy-2 align-middle">
                        <thead>
                            <tr class="fw-bolder fs-6 text-gray-700 table-fixed">
                                <th class="min-w-50px">이름</th>
                                <th class="min-w-75px hidden-table">부서</th>
                                <th class="min-w-75px">직급</th>
                                <th class="min-w-125px">연락처</th>
                                <th class="min-w-175px">메일</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#list crtdUserCttpcList![] as user>
                                <tr>
                                    <td>${user.userNm!}</td>
                                    <td class="hidden-table">${user.teamNm!}</td>
                                    <td>${user.rankNm!}</td>
                                    <td>${user.cttpc!}</td>
                                    <td>${user.email!}</td>
                                </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>