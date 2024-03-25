package io.nicheblog.dreamdiary.web.repository.user;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.user.UserInfoEntity;
import org.springframework.stereotype.Repository;

/**
 * UserInfoRepository
 * <pre>
 *  사용자 관리 > 사용자 정보 관리 Repository
 * </pre>
 *
 * @author nichefish
 */
@Repository("userInfoRepository")
public interface UserInfoRepository
        extends BaseRepository<UserInfoEntity, Integer> {
    //
}