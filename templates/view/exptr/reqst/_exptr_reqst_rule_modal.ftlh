<#--
 * 물품구매/경조사비 신청(exptr_reqst) > 경조사비 규칙 Modal
 * @included_in : exptr_reqst_list_header
 * @included_in : exptr_reqst_reg_form
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="exptr_reqst_rule" size="xl">
    <#--begin::Modal Header-->
    <@component.modal_header "경조사비 기준" />
    <#--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <div class="table-responsive-sm">
            <#--begin::Table-->
            <table class="table table-row-bordered table-row-gray-300 gy-2 align-middle">
                <#--begin::Table head-->
                <thead>
                    <tr class="fw-bolder fs-6 text-gray-700">
                        <th class="min-w-150px">경조사 항목</th>
                        <th class="min-w-75px">휴가일</th>
                        <th class="min-w-150px">조사비</th>
                        <th class="min-w-175px">비고</th>
                        <th class="min-w-125px">지참서류</th>
                    </tr>
                </thead>
                <#--begin::Table body-->
                <tbody>
                    <tr>
                        <td>본인 결혼</td>
                        <td>5일</td>
                        <td>50만원</td>
                        <td>휴일제외<br>[회사 축의금 형식, 화환 별도]</td>
                        <td>청첩장</td>
                    </tr>
                    <tr>
                        <td>양가 형제 자매 결혼</td>
                        <td>1일</td>
                        <td>10만원</td>
                        <td>휴일제외</td>
                        <td>청첩장</td>
                    </tr>
                    <tr>
                        <td>4촌 이내 형제 자매 결혼</td>
                        <td>1일</td>
                        <td>5만원</td>
                        <td>휴일제외</td>
                        <td>청첩장</td>
                    </tr>
                    <tr>
                        <td rowspan="2">본인 사망</td>
                        <td rowspan="2"></td>
                        <td>300만원</td>
                        <td></td>
                        <td>회사의 비귀책 사유</td>
                    </tr>
                    <tr>
                        <td style="padding-left:10px;">이사회 결정으로 하되 최소 1000만원 이상</td>
                        <td></td>
                        <td>회사의 귀책사유</td>
                    </tr>
                    <tr>
                        <td>부모 사망(본가)</td>
                        <td>4일</td>
                        <td>50만원</td>
                        <td>휴일제외[조화]</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>부모 사망(처가)</td>
                        <td>4일</td>
                        <td>50만원</td>
                        <td>휴일제외</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>양가 조부모 사망</td>
                        <td>2일</td>
                        <td>10만원</td>
                        <td>휴일제외</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>직계 외조부모 사망</td>
                        <td>2일</td>
                        <td>10만원</td>
                        <td>휴일제외</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>양가 부모 회갑</td>
                        <td>1일</td>
                        <td>20만원</td>
                        <td>휴일제외</td>
                        <td>주민등록(부모)</td>
                    </tr>
                    <tr>
                        <td>자녀 돌</td>
                        <td>없음</td>
                        <td>10만원</td>
                        <td></td>
                        <td>주민등록등본</td>
                    </tr>
                    <tr>
                        <td>형제 사망</td>
                        <td>3일</td>
                        <td>30만원</td>
                        <td>휴일제외</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>자녀 사망</td>
                        <td>3일</td>
                        <td>50만원</td>
                        <td>휴일제외</td>
                        <td></td>
                    </tr>
                </tbody>
            </table>
            <div>
                <p class="text-noti">
                    ※휴일제외 : 휴일에 경조사 발생시 해당일 외의 연속되는 평일에 해당일수만큼 붙여서 경조휴가 사용 가능
                </p>
            </div>
        </div>
    </div>
    <#--begin::Modal Footer-->
    <div class="modal-footer">
        <#--버튼 영역-->
        <div class="d-flex justify-content-end">
            <#--모달:: 버튼: 모달 닫기-->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>
