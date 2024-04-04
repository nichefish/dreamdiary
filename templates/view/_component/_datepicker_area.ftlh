<!--
 -- datepicker 관련 컴포넌트
 -- @included_in : lib/components.ftl
 -->

<#macro datepicker param searchNm initDt >
    <script>
        $(document).ready(function() {
            const ptn = commons.datepicker.ptnDate;
            /** dateRangePicker init */
            let initDt = "${initDt!}";
            if(initDt === "") initDt = commons.date.getCurrDateStr(ptn);
            commons.datepicker.singleDatePicker("#${searchNm}", ptn, initDt);
        });
    </script>
    <span class="menu-icon me-md-2 vertical-middle">
        <label for="${searchNm}"><i class="bi bi-calendar3 fs-2 cursor-pointer"></i></label>
    </span>
    <span class="datepicker">
        <input type="text" class="form-control form-control-solid w-150px fs-7 me-md-2 fw-bolder d-inline" name="${searchNm}" id="${searchNm}"
               placeholder="Pick date" value="${param[searchNm]!initDt}" />
    </span>
</#macro>

<#macro datetimepicker param searchNm initDt >
    <script>
        const ptn = commons.datepicker.ptnDatetime;
        $(document).ready(function() {
            /** dateRangePicker init */
            let initDt = "${initDt!}";
            if(initDt === "") initDt = commons.date.getCurrDateStr(ptn);
            commons.datepicker.singleDatePicker("#${searchNm}", ptn, initDt);
        });
    </script>
    <span class="menu-icon me-md-2 vertical-middle">
        <label for="${searchNm}"><i class="bi bi-calendar3 fs-2 cursor-pointer"></i></label>
    </span>
    <span class="datepicker">
        <input type="text" class="form-control form-control-solid w-150px fs-7 me-md-2 fw-bolder d-inline" name="${searchNm}" id="${searchNm}"
               placeholder="Pick date" value="${param[searchNm]!initDt}" />
    </span>
</#macro>
