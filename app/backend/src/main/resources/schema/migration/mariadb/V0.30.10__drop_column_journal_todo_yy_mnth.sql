-- Keep 항목에서 발생지(yy/mnth) 컬럼을 걷어낸다.
-- @database : mariadb
-- @author : nichefish
-- yy/mnth 는 「이 할 일이 발생한 저널 월」을 담는 불변 발생지로 도입했으나, 그 값을 쓰는
-- 조회·정렬·그룹핑이 끝내 만들어지지 않았다. Spec 은 yy/mnth 를 무시하고, 서비스는
-- searchParam 에서 명시적으로 null 로 지운다. 화면에 찍히던 「2026.09」 하나가 유일한 소비처였고,
-- 그 표시는 created_at 으로 동일하게 얻는다. 실측 38건 모두 yy/mnth 가 created_at 의 년월과
-- 같아 발생지가 갈라진 사례가 없었다.
-- 마스터 스키마에 잘못 걸려 있던 `yy INT UNIQUE`(연도당 1건 제약)도 컬럼과 함께 사라진다.
ALTER TABLE journal_todo DROP COLUMN yy;
ALTER TABLE journal_todo DROP COLUMN mnth;
