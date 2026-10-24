-- Keep 항목의 종결 어휘를 넓힌다 (EXPIRED·DROPPED).
-- @database : mariadb
-- @author : nichefish
-- 저널 기록은 시간이 지나도 사라지지 않지만 Keep 항목은 기회가 만료되고(EXPIRED),
-- 의미를 잃어 내려놓기도 한다(DROPPED). 셋을 RESOLVED 하나로 뭉치면 「해냈다」와
-- 「못 한 채 끝났다」가 구분되지 않는다.
-- 어느 컨텐츠 타입이 어떤 키를 쓰는지는 AttachableContentLifecyclePolicy 가 정한다.
-- CHECK 는 영속 가능한 키 집합만 제한한다(OPEN 은 행이 없는 기본 상태라 대상이 아니다).
ALTER TABLE lifecycle DROP CONSTRAINT ck_lifecycle_persisted_key;
ALTER TABLE lifecycle ADD CONSTRAINT ck_lifecycle_persisted_key
    CHECK (lifecycle_key IN ('PENDING', 'RESOLVED', 'EXPIRED', 'DROPPED'));
