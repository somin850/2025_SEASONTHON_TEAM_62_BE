package com.kbsw.seasonthon.crew.repository;

import com.kbsw.seasonthon.crew.domain.Crew;
import com.kbsw.seasonthon.crew.domain.CrewParticipant;
import com.kbsw.seasonthon.crew.enums.ParticipantStatus;
import com.kbsw.seasonthon.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrewParticipantRepository extends JpaRepository<CrewParticipant, Long> {
    
    Optional<CrewParticipant> findByCrewAndUser(Crew crew, User user);
    
    List<CrewParticipant> findByCrewAndStatus(Crew crew, ParticipantStatus status);
    
    List<CrewParticipant> findByCrew(Crew crew);
    
    boolean existsByCrewAndUser(Crew crew, User user);
}
