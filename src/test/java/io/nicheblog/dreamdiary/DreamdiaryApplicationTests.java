/*
package io.nicheblog.dreamdiary;

import lombok.extern.log4j.Log4j2;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.Mockito.*;

*/
/**
 * IntranetApplicationTests
 * 단위 테스트 모듈:: 초안 작성중
 * @author nichefish
 *//*

@SpringBootTest
@ActiveProfiles("test")
@Log4j2
class DreamdiaryApplicationTests {

    @InjectMocks
    private DreamdiaryApplication dreamdiaryApplication;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    */
/**
     * arrange:: 시스템 계정이 이미 존재하는 경우
     * verity:: 새로운 계정이 생성되지 않았는지 확인
     *//*

    @Test
    public void test_chckSystemAcnt_ifAccountExists() {
        */
/* when: you can mock the service's response here *//*

        when(userRepository.findByUserId(Constant.SYSTEM_ACNT))
                .thenReturn(Optional.of(new UserEntity()));

        dreamdiaryApplication.chkSystemAcnt();

        verify(userRepository, never()).saveAndFlush(any(UserEntity.class));
    }

    */
/**
     * arrange:: 시스템 계정이 존재하지 않는 경우
     * verity:: 새로운 계정이 생성되었는지 확인
     *//*

    @Test
    public void test_chckSystemAcnt_NewAccount() {
        */
/* when: you can mock the service's response here *//*

        when(userRepository.findByUserId(Constant.SYSTEM_ACNT))
                .thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(any(UserEntity.class)))
                .thenReturn(new UserEntity());

        dreamdiaryApplication.chkSystemAcnt();

        verify(userRepository).saveAndFlush(any(UserEntity.class));
    }

    */
/**
     * arrange:: 계정 생성 과정에서 예외가 발생하는 경우
     *//*

    @Test
    public void test_chckSystemAcnt_Exception() {
        */
/* when: you can mock the service's response here *//*

        // 예외가 발생하는 경우
        when(userRepository.findByUserId(Constant.SYSTEM_ACNT))
                .thenThrow(new RuntimeException());

        dreamdiaryApplication.chkSystemAcnt();
    }
}*/
