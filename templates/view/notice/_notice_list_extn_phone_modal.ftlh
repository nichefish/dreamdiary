<#--
  -- 게시판 > 내선번호 관련 Modal (슬기로운 회사생활)
  -- @included_in : board_post_list
  -->

<div class="modal fade" id="extn_phone_modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "내선번호" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body my-5">
                <div class="table-responsive-sm">
                    <table class="table table-row-bordered table-row-gray-300 gy-2 align-middle fs-small">
                        <tbody>
                            <tr class="pt-10px">
                                <td>내선번호</td>
                                <td><u>번호 뒤 네자리</u> (전화기 및 전화선에 표시되어 있음)</td>
                            </tr>
                            <tr class="pt-10px">
                                <td>전화 돌려주기</td>
                                <td><u>플래시</u>(*밑에 있음) 누르고 <u>내선번호</u> 누름</td>
                            </tr>
                            <tr class="pt-10px">
                                <td>전화 당겨받기</td>
                                <td>수화기 들고 <u>**</u></td>
                            </tr>
                            <tr class="pt-10px">
                                <td>참고: 건물 내에 국번이 같은 곳은 내선번호만 누르면 연결됨</td>
                                <td></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>