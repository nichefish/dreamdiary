package io.nicheblog.dreamdiary.web.repository.user.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import org.springframework.stereotype.Repository;

/**
 * UserProflRepository
 * <pre>
 *  사용자 관리 > 사용자 프로필 Repository.
 * </pre>
 *
 * @author nichefish
 */
@Repository("userProflRepository")
public interface UserProflRepository
        extends BaseStreamRepository<UserProflEntity, Integer> {
    //

}