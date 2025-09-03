package com.kbsw.seasonthon.user.repository;



import com.kbsw.seasonthon.security.oauth2.enums.ProviderType;
import com.kbsw.seasonthon.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일을 기준으로 사용자 찾기
    Optional<User> findByUsername(String username);

    //OAuth 정보( provider 벤더명과 해당 provider 상의 식별자값 ) 기준으로 사용자 찾기
    @Query("select u from User u where u.providerType = :providerType and u.providerId = :providerId " )
    Optional<User> findByOAuthInfo(@Param("providerType") ProviderType providerType,
                                   @Param("providerId") String providerId);



}