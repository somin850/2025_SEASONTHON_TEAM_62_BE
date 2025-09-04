package com.kbsw.seasonthon.favorite.repository;

import com.kbsw.seasonthon.favorite.entity.Favorite;
import com.kbsw.seasonthon.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<Favorite> findByIdAndUser(Long id, User user);
    
    void deleteByIdAndUser(Long id, User user);
    
    long countByUser(User user);
}
