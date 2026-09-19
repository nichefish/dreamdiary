-- Keep 항목에 유효 기한(expire_dt)을 더한다.
-- @database : mariadb
-- @author : nichefish
-- date_text 는 사람이 쓴 일시 원문(다회차·범위)을 보존하는 표시용 컬럼이라 정렬·판정 축으로 쓸 수 없다.
-- expire_dt 는 그 판정 축으로, 「이 날이 지나면 더는 할 수 없다」는 단일 날짜 하나만 갖는다.
-- 다회차 항목은 마지막 회차 하루만 여기 담긴다. 시각은 갖지 않는다.
ALTER TABLE journal_todo ADD COLUMN expire_dt DATE COMMENT '유효 기한' AFTER date_text;
