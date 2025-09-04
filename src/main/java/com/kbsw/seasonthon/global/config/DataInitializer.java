package com.kbsw.seasonthon.global.config;

import com.kbsw.seasonthon.crew.domain.Crew;
import com.kbsw.seasonthon.crew.domain.CrewParticipant;
import com.kbsw.seasonthon.crew.enums.CrewStatus;
import com.kbsw.seasonthon.crew.enums.ParticipantStatus;
import com.kbsw.seasonthon.crew.enums.SafetyLevel;
import com.kbsw.seasonthon.crew.repository.CrewParticipantRepository;
import com.kbsw.seasonthon.crew.repository.CrewRepository;
import com.kbsw.seasonthon.security.jwt.enums.Role;
import com.kbsw.seasonthon.user.entity.User;
import com.kbsw.seasonthon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!prod") // 프로덕션 환경에서는 실행하지 않음
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CrewRepository crewRepository;
    private final CrewParticipantRepository crewParticipantRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("=== 시드 데이터 초기화 시작 ===");
            initializeTestData();
            log.info("=== 시드 데이터 초기화 완료 ===");
        } else {
            log.info("시드 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
        }
    }

    private void initializeTestData() {
        // 1. 테스트 사용자들 생성
        List<User> testUsers = createTestUsers();
        
        // 2. 테스트 크루들 생성
        List<Crew> testCrews = createTestCrews(testUsers);
        
        // 3. 크루 참여자 관계 생성
        createCrewParticipants(testUsers, testCrews);
    }

    private List<User> createTestUsers() {
        log.info("테스트 사용자 생성 중...");
        
        List<User> users = Arrays.asList(
            User.builder()
                .username("testuser1")
                .password(passwordEncoder.encode("password123"))
                .nickname("러너김")
                .email("runner1@example.com")
                .role(Role.USER)
                .address("대전시 유성구")
                .phone("010-1234-5678")
                .build(),
            
            User.builder()
                .username("testuser2")
                .password(passwordEncoder.encode("password123"))
                .nickname("달리기박")
                .email("runner2@example.com")
                .role(Role.USER)
                .address("부산시 해운대구")
                .phone("010-2345-6789")
                .build(),
                
            User.builder()
                .username("testuser3")
                .password(passwordEncoder.encode("password123"))
                .nickname("조깅이")
                .email("runner3@example.com")
                .role(Role.USER)
                .address("대구시 북구")
                .phone("010-3456-7890")
                .build(),
                
            User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .nickname("관리자")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .address("광주시 서구")
                .phone("010-0000-0000")
                .build()
        );
        
        return userRepository.saveAll(users);
    }

    private List<Crew> createTestCrews(List<User> users) {
        log.info("테스트 크루 생성 중...");
        
        List<Crew> crews = Arrays.asList(
            Crew.builder()
                .title("금강 달리기 크루루")
                .description("금강 산책로에서 가볍게 뛰며 함께 운동해요! 초보자도 환영합니다.")
                .host(users.get(0))
                .maxParticipants(10)
                .routeId("route_geumgang_001")
                .type("safe")
                .distanceKm(8.5)
                .safetyScore(90)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(45)
                .startLocation("금강공원 입구")
                .pace("5'30\"/km")
                .startTime(LocalDateTime.now().plusDays(2).withHour(19).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "36.3504,127.3845",
                    "36.3514,127.3855", 
                    "36.3509,127.3835"
                ))
                .tags(Arrays.asList("취준생", "20대", "주말", "초보환영"))
                .build(),
                
            Crew.builder()
                .title("경북대 러닝 크루")
                .description("경북대 주변 러닝 코스로 함께 달려요. 체력 향상이 목표입니다!")
                .host(users.get(1))
                .maxParticipants(6)
                .routeId("route_knu_002")
                .type("normal")
                .distanceKm(5.2)
                .safetyScore(75)
                .safetyLevel(SafetyLevel.MEDIUM)
                .durationMin(35)
                .startLocation("경북대학교 정문")
                .pace("6'00\"/km")
                .startTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(30))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "35.8895,128.6137",
                    "35.8905,128.6147",
                    "35.8900,128.6150"
                ))
                .tags(Arrays.asList("자유로운 분위기기", "대학교", "평일", "체력향상"))
                .build(),
                
            Crew.builder()
                .title("새벽 조깅 모임")
                .description("상쾌한 밤 공기를 마시며 조깅해요!")
                .host(users.get(2))
                .maxParticipants(8)
                .routeId("route_morning_003")
                .type("safe")
                .distanceKm(3.8)
                .safetyScore(85)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(25)
                .startLocation("중앙공원 입구")
                .pace("6'30\"/km")
                .startTime(LocalDateTime.now().plusDays(3).withHour(6).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "35.8700,128.5900",
                    "35.8710,128.5910",
                    "35.8705,128.5920"
                ))
                .tags(Arrays.asList("직장인", "밤", "공원", "가벼운운동"))
                .build(),
                
            Crew.builder()
                .title("마라톤 준비반")
                .description("마라톤 대회 준비를 위한 고강도 훈련 크루입니다. 경험자만 참여해주세요.")
                .host(users.get(0))
                .maxParticipants(5)
                .routeId("route_marathon_004")
                .type("challenging")
                .distanceKm(15.0)
                .safetyScore(60)
                .safetyLevel(SafetyLevel.UNSAFE)
                .durationMin(90)
                .startLocation("대전 월드컵경기장")
                .pace("4'45\"/km")
                .startTime(LocalDateTime.now().plusDays(5).withHour(7).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "36.3504,127.3845",
                    "36.3554,127.3895",
                    "36.3534,127.3925",
                    "36.3514,127.3865"
                ))
                .tags(Arrays.asList("마라톤", "고강도", "경험자", "대회준비"))
                .build(),
                
            Crew.builder()
                .title("완료된 크루 (예시)")
                .description("이미 완료된 크루입니다.")
                .host(users.get(3))
                .maxParticipants(4)
                .routeId("route_completed_005")
                .type("safe")
                .distanceKm(4.0)
                .safetyScore(88)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(30)
                .startLocation("부산 해운대해수욕장")
                .pace("7'00\"/km")
                .startTime(LocalDateTime.now().minusDays(1).withHour(19).withMinute(0))
                .status(CrewStatus.CLOSED)
                .waypoints(Arrays.asList(
                    "35.1595,129.1606",
                    "35.1605,129.1616"
                ))
                .tags(Arrays.asList("완료", "해변", "가벼운운동"))
                .build()
        );
        
        return crewRepository.saveAll(crews);
    }

    private void createCrewParticipants(List<User> users, List<Crew> crews) {
        log.info("크루 참여자 관계 생성 중...");
        
        // 첫 번째 크루에 여러 사용자 참여
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(0))
                .user(users.get(1))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(0))
                .user(users.get(2))
                .status(ParticipantStatus.APPLIED)
                .build()
        );
        
        // 두 번째 크루에 사용자 참여
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(1))
                .user(users.get(0))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(1))
                .user(users.get(2))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        // 세 번째 크루에 사용자 참여
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(2))
                .user(users.get(0))
                .status(ParticipantStatus.APPLIED)
                .build()
        );
    }
}
