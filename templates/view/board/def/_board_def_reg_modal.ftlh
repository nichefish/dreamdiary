<#--
 * 게시판 정의(board_def) > 등록 Modal
 * @included_in : board_def_list
 -->

<#import "/layout/modal_layout.ftlh" as modal>

<@modal.layout id="board_def_reg" size="xl">
    <!--begin::Modal Header-->
    <@component.modal_header "게시판 정의 저장" />
    <!--begin::Modal Body-->
    <div class="modal-body modal-mbl-body my-5">
        <form name="boardDefRegForm" id="boardDefRegForm">
        <div class="table-responsive-sm">
            <#--begin::Table-->
            <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3 fs-4 mx-4 px-4 fw-normal text-gray-800">
                <#--begin::Table head-->
                <thead>
                    <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 text-muted">
                        <th class="col-2 text-center wb-keepall pb-2">
                            <em class="text-danger">*</em>
                            <label class="cursor-help required"
                                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="게시판의 이름을 입력합니다.">
                                게시판 이름
                            </label>
                        </th>
                        <th class="col-2 text-center wb-keepall pb-2">
                            <em class="text-danger">*</em>
                            <span class="cursor-help"
                                  data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                  title="게시판을 특정할 때 내부적으로&#10;사용되는 게시판 고유 코드를&#10;입력합니다.&#10;(등록 후 수정할 수 없습니다.)">
                                게시판 코드
                            </span>
                        </th>
                        <th class="col-2 text-center wb-keepall pb-2">
                            <label class="cursor-help"
                                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                   title="게시물을 작성할 때 사용할&#10;글분류 코드를 입력합니다.&#10;(코드 정보는 '코드 관리'에서&#10;관리할 수 있습니다.)">
                                글분류 코드
                            </label>
                        </th>
                        <th class="col-2 text-center wb-keepall pb-2">
                            <label class="cursor-help required"
                                   data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click"
                                   title="좌측 메뉴 목록에서&#10;메뉴를 특정할 때 사용할&#10;메뉴 번호를 입력합니다.">
                                메뉴 번호
                            </label>
                        </th>
                    </tr>
                </thead>
                <#--begin::Table body-->
                <tbody id="board_def_reg_div">
                    <!-- contents -->
                </tbody>
            </table>
        </div>
        </form>
    </div>
    <!--begin::Modal Footer-->
    <div class="modal-footer">
        <!-- 버튼 영역 -->
        <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-sm btn-primary me-2" onclick="BoardDef.regAjax();"
                    data-bs-toggle="tooltip" data-bs-placement="top" data-bs-dismiss="click" title="항목을 저장합니다.">
                <i class="bi bi-pencil-square"></i>저장
            </button>
            <!-- 버튼:: 모달 닫기 -->
            <@component.modal_btn_close />
        </div>
    </div>
</@modal.layout>

<!-- include:: Handlebars template -->
<#include "/view/board/def/_board_def_reg_template.hbs">