package io.nicheblog.dreamdiary.web.repository.user;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.user.reqst.UserReqstEntity;
import org.springframework.stereotype.Repository;

/**
 * UserReqstRepository
 * <pre>
 *  사용자 관리 > 신규계정 신청 계정 관리 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */

@Repository("userReqstRepository")
public interface UserReqstRepository
        extends BaseRepository<UserReqstEntity, Integer> {
    //
}
