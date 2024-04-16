<#--
  -- 물품구매/경조사비 신청(exptr_reqst) > 작성예시 Modal
  -- @included_in : exptr_reqst_list
  -->

<div class="modal fade" id="exptr_reqst_exmpl_modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
            <!-- 영역:: 모달 헤더 -->
            <@component.modal_header "물품구매/경조사비 신청 작성 안내" />
            <!-- 영역:: 모달 내용 -->
            <div class="modal-body modal-mbl-body mb-5">
                <div class="table-responsive-sm">
                    <h4 class="text-start mt-10">[물품구매 요청]</h4>
                    <table class="table table-row-bordered table-row-gray-300 gy-2 align-middle fs-small">
                        <tr class="pt-10px">
                            <td>1. 구매 요청하실 품목의 상세를 작성해주세요.</td>
                        </tr>
                        <tr class="pt-10px">
                            <td>
                                <p class="mb-0">2. 품목의 상세는 해당 품목의 명칭, 수량, 옵션, 가격, 판매처(ex url)를 작성해 주시면 됩니다.</p>
                                <p class="ms-4 mt-1 mb-0 text-noti">(※구매시 선택/입력해야 하는 상품옵션이 있을 경우 전부 작성해주세요.)</p>
                            </td>
                        </tr>
                        <tr class="pt-10px">
                            <td>3. 구매 요청시 <span class="text-noti">지마켓, 옥션, 11번가, 전자랜드</span>를 우선적으로 이용해주세요. (기존 계정 존재)</td>
                        </tr>
                        <tr class="pt-10px">
                            <td>
                                <div class="pb-3">4. 작성 예시:</div>
                                <div class="div-text-example">
                                    <div class="ps-5 py-1">- 제품명 : 27인치 컴퓨터 모니터 / 27MK600MW</div>
                                    <div class="ps-5 py-1">- 수량 : 1개</div>
                                    <div class="ps-5 py-1">- 옵션 : 색상 등 있을 경우</div>
                                    <div class="ps-5 py-1">- 가격 : 249,300</div>
                                    <div class="ps-5 py-1">- 판매처 URL : <u>https://www.etlandmall.co.kr/pc/product/product.do?prdMstCd=S0485017&chlCode=A072&service_id=pcdn#prQnA</u></div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
                <h4 class="text-start mt-10">[경조사비 신청]</h4>
                <div class="table-responsive-sm">
                    <table class="table table-row-bordered table-row-gray-300 gy-2 align-middle fs-small">
                        <tr class="pt-10px">
                            <td>
                                <p class="mb-0">1. 경조사 항목을 작성해주세요.</p>
                                <p class="ms-4 mt-1 mb-0 text-noti">(※경조휴가 사용시 휴가신청서는 별도로 작성해주세요.)</p>
                            </td>
                        </tr>
                        <tr class="pt-10px">
                            <td>2. 결혼 등 특정 경조사의 경우 증빙서류가 필요할 수 있으니 확인하여 첨부바랍니다.</td>
                        </tr>
                        <tr class="pt-10px">
                            <td>
                                <div class="pb-3">3. 작성 예시:</div>
                                <div class="div-text-example">
                                    <div class="ps-5 py-1">- 경조사 항목 : 본인결혼</div>
                                    <div class="ps-5 py-1">- 휴가일 : 2022년 3월 28일 ~ 2022년 4월 1일</div>
                                    <div class="ps-5 py-1">- 첨부 : 청첩장</div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <!-- 버튼 영역 -->
                <div class="d-flex justify-content-end">
                    <!-- 버튼:: 모달 닫기 -->
                    <@component.btn_modal_close />
                </div>
            </div>
        </div>
    </div>
</div>