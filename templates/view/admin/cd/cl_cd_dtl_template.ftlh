<#--
  -- 분류 코드(CL_CD) > 상세 Template
  -- @included in : cl_cd_list
  -->
<script type="text/x-handlebars-template" id="cl_cd_dtl_template">
    <div class="card mt-0">
        <!--begin::Question-->
        <div class="mb-0">
            <!--begin::Head-->
            <div class="d-flex align-items-center mb-4">
                <!--begin::Title-->
                <div class="col-12">
                    <h1 class="fs-2x fw-bolder text-gray-900 mb-0 me-1">
                    <span class="vertical-middle">
                        <span class="fw-bold">{{clCdNm}}</span> (<span class="border-bottom">{{clCd}}</span>)
                    </span>
                    </h1>
                </div>
                <!--end::Title-->
            </div>
            <!--end::Head-->
            <!--begin::Foot-->
            <div class="d-flex flex-stack flex-wrap mb-3">
            </div>
            <!--end::Foot-->
            <div class="separator separator-dashed border-gray-300 my-8"></div>
            <!--begin::Question-->
            <div class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6">
                {{clCdDc}}
            </div>
            <div class="separator separator-dashed border-gray-300 my-8"></div>
                <table class="table align-middle table-row-dashed fs-small gy-5 table-fixed hoverTable mb-3">
                <!--begin::Table head-->
                <thead>
                    <tr class="text-start fw-bolder fs-7 text-uppercase gs-0 fw-bolder text-muted">
                        <th class="col-1 text-center wb-keepall w-8">번호</th>
                        <th class="col-2 text-center wb-keepall">상세코드</th>
                        <th class="col-2 text-center wb-keepall">상세코드명</th>
                        <th class="col-4 text-center wb-keepall">상세코드 설명</th>
                        <th class="col-1 text-center wb-keepall">정렬 순서</th>
                        <th class="col-1 text-center wb-keepall">사용 여부</th>
                    </tr>
                </thead>
                <!--end::Table head-->
                <!--begin::Table body-->
                <tbody>
                    {{#each dtlCdList}}
                        <tr class="bg-hover-secondary">
                            <td class="text-center">
                                {{rnum}}
                            </td>
                            <td class="text-start ps-6 cursor-pointer">
                                <span class="fw-bold border-bottom">
                                    {{dtlCd}}
                                </span>
                            </td>
                            <td class="text-start ps-6">
                                <span class="border-bottom">
                                    {{dtlCdNm}}
                                </span>
                            </td>
                            <td class="text-start ps-6">
                                {{dtlCdDc}}
                            </td>
                            <td class="text-center">
                                {{sortOrdr}}
                            </td>
                            <td class="text-center">
                                <span class="text-{{#if (trueFalse useYn "Y//N")}}success{{else}}muted{{/if}}">
                                    {{#if (trueFalse useYn "Y//N")}}
                                        사용<i class="bi bi-check text-success"></i>
                                    {{else}}
                                        미사용<i class="bi bi-dash text-muted"></i>
                                    {{/if}}
                                </span>
                            </td>
                        </tr>
                    {{/each}}
                    <!--end::Table row-->
                </tbody>
            </table>
        </div>
        <div class="separator separator-dashed border-gray-300 my-8"></div>

    </div>
</script>