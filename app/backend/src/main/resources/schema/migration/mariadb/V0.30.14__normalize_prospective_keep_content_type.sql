-- prospective_keep 본체의 content_type을 도메인 식별자 하나로 수렴시킨다.
-- @database : mariadb
-- @author : nichefish
--
-- 테이블 이관 전에 잘못된 다른 content_type을 가진 기존 행도 Keep으로 정규화한다.

UPDATE prospective_keep
SET content_type = 'PROSPECTIVE_KEEP'
WHERE content_type <> 'PROSPECTIVE_KEEP';
