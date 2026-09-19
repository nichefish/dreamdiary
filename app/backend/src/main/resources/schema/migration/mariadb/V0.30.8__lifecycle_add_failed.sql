-- Keep 항목의 종결 어휘에 FAILED 를 더한다.
-- @database : mariadb
-- @author : nichefish
-- 실사용 근거: 시도했으나 기한 내 성사되지 않은 항목에 맞는 칸이 없어 사용자가 전이를 포기하고
-- OPEN 으로 방치하고 있었다. RESOLVED(해냄)·EXPIRED(시간이 끝냄)·DROPPED(내가 접음) 어디에
-- 넣어도 사실과 달라진다. 종결 넷은 시도 여부와 끝낸 주체로 갈린다.
ALTER TABLE lifecycle DROP CONSTRAINT ck_lifecycle_persisted_key;
ALTER TABLE lifecycle ADD CONSTRAINT ck_lifecycle_persisted_key
    CHECK (lifecycle_key IN ('PENDING', 'RESOLVED', 'FAILED', 'EXPIRED', 'DROPPED'));
