package com.kbsw.seasonthon.crew.repository;

import com.kbsw.seasonthon.crew.domain.Crew;
import com.kbsw.seasonthon.crew.enums.CrewStatus;
import com.kbsw.seasonthon.crew.enums.SafetyLevel;
import com.kbsw.seasonthon.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrewRepository extends JpaRepository<Crew, Long> {
    
    @Query("SELECT c FROM Crew c WHERE c.host = :user OR c.id IN " +
           "(SELECT cp.crew.id FROM CrewParticipant cp WHERE cp.user = :user AND cp.status = 'APPROVED')")
    List<Crew> findCrewsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(cp) FROM CrewParticipant cp WHERE cp.crew = :crew AND cp.status = 'APPROVED'")
    Long countApprovedParticipants(@Param("crew") Crew crew);
    
    Optional<Crew> findByIdAndHost(Long id, User host);
    
    // 크루 검색 메서드들
    @Query("SELECT DISTINCT c FROM Crew c " +
           "LEFT JOIN c.tags t " +
           "WHERE (:keyword IS NULL OR c.title LIKE %:keyword% OR c.description LIKE %:keyword%) " +
           "AND (:startLocation IS NULL OR c.startLocation LIKE %:startLocation%) " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:safetyLevel IS NULL OR c.safetyLevel = :safetyLevel) " +
           "AND (:minDistance IS NULL OR c.distanceKm >= :minDistance) " +
           "AND (:maxDistance IS NULL OR c.distanceKm <= :maxDistance) " +
           "AND (:minPace IS NULL OR c.pace <= :minPace) " +
           "AND (:maxPace IS NULL OR c.pace >= :maxPace) " +
           "AND (:startTimeFrom IS NULL OR c.startTime >= :startTimeFrom) " +
           "AND (:startTimeTo IS NULL OR c.startTime <= :startTimeTo) " +
           "AND (:minDuration IS NULL OR c.durationMin >= :minDuration) " +
           "AND (:maxDuration IS NULL OR c.durationMin <= :maxDuration) " +
           "AND (:tags IS NULL OR t IN :tags)")
    Page<Crew> searchCrews(@Param("keyword") String keyword,
                          @Param("startLocation") String startLocation,
                          @Param("status") CrewStatus status,
                          @Param("safetyLevel") SafetyLevel safetyLevel,
                          @Param("minDistance") Double minDistance,
                          @Param("maxDistance") Double maxDistance,
                          @Param("minPace") String minPace,
                          @Param("maxPace") String maxPace,
                          @Param("startTimeFrom") LocalDateTime startTimeFrom,
                          @Param("startTimeTo") LocalDateTime startTimeTo,
                          @Param("minDuration") Integer minDuration,
                          @Param("maxDuration") Integer maxDuration,
                          @Param("tags") List<String> tags,
                          Pageable pageable);
    
    // 인기 크루 조회 (참여자 수 기준)
    @Query("SELECT c FROM Crew c WHERE c.status = 'OPEN' ORDER BY " +
           "(SELECT COUNT(cp) FROM CrewParticipant cp WHERE cp.crew = c AND cp.status = 'APPROVED') DESC")
    Page<Crew> findPopularCrews(Pageable pageable);
    
    // 최신 크루 조회
    Page<Crew> findByStatusOrderByCreatedAtDesc(CrewStatus status, Pageable pageable);
}
