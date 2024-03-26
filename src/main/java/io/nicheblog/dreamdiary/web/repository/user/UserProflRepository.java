package io.nicheblog.dreamdiary.web.repository.user;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.user.UserProflEntity;
import org.springframework.stereotype.Repository;

/**
 * UserProflRepository
 * <pre>
 *  사용자 관리 > 사용자 프로필 Repository
 * </pre>
 *
 * @author nichefish
 */
@Repository("userProflRepository")
public interface UserProflRepository
        extends BaseRepository<UserProflEntity, Integer> {
    //
}