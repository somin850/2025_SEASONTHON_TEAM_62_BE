package com.kbsw.seasonthon.global.config;

import com.kbsw.seasonthon.crew.domain.Crew;
import com.kbsw.seasonthon.crew.domain.CrewParticipant;
import com.kbsw.seasonthon.crew.enums.CrewStatus;
import com.kbsw.seasonthon.crew.enums.ParticipantStatus;
import com.kbsw.seasonthon.crew.enums.SafetyLevel;
import com.kbsw.seasonthon.crew.repository.CrewParticipantRepository;
import com.kbsw.seasonthon.crew.repository.CrewRepository;
import com.kbsw.seasonthon.favorite.entity.Favorite;
import com.kbsw.seasonthon.favorite.repository.FavoriteRepository;
import com.kbsw.seasonthon.report.entity.Report;
import com.kbsw.seasonthon.report.enums.ReportStatus;
import com.kbsw.seasonthon.report.enums.TargetType;
import com.kbsw.seasonthon.report.repository.ReportRepository;
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
    private final ReportRepository reportRepository;
    private final FavoriteRepository favoriteRepository;
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
        
        // 4. 신고 테스트 데이터 생성
        createTestReports(testUsers);
        
        // 5. 즐겨찾기 테스트 데이터 생성
        createTestFavorites(testUsers);
    }

    private List<User> createTestUsers() {
        log.info("테스트 사용자 생성 중...");
        
        List<User> users = Arrays.asList(
            // 지방 청년들의 다양한 상황을 반영한 15명의 사용자
            User.builder()
                .username("jobseeker_kim")
                .password(passwordEncoder.encode("password123"))
                .nickname("취준러너김")
                .email("jobseeker@example.com")
                .role(Role.USER)
                .address("대전시 유성구")
                .phone("010-1234-5678")
                .build(),
            
            User.builder()
                .username("shy_worker")
                .password(passwordEncoder.encode("password123"))
                .nickname("내성적인박")
                .email("shyworker@example.com")
                .role(Role.USER)
                .address("부산시 해운대구")
                .phone("010-2345-6789")
                .build(),
                
            User.builder()
                .username("college_runner")
                .password(passwordEncoder.encode("password123"))
                .nickname("대학생조")
                .email("college@example.com")
                .role(Role.USER)
                .address("대구시 북구")
                .phone("010-3456-7890")
                .build(),
                
            User.builder()
                .username("friendly_lee")
                .password(passwordEncoder.encode("password123"))
                .nickname("친화적인이")
                .email("friendly@example.com")
                .role(Role.USER)
                .address("광주시 서구")
                .phone("010-4567-8901")
                .build(),
                
            User.builder()
                .username("freelancer_choi")
                .password(passwordEncoder.encode("password123"))
                .nickname("프리랜서최")
                .email("freelancer@example.com")
                .role(Role.USER)
                .address("전주시 완산구")
                .phone("010-5678-9012")
                .build(),
                
            User.builder()
                .username("newbie_kang")
                .password(passwordEncoder.encode("password123"))
                .nickname("러닝초보강")
                .email("newbie@example.com")
                .role(Role.USER)
                .address("청주시 흥덕구")
                .phone("010-6789-0123")
                .build(),
                
            User.builder()
                .username("social_jung")
                .password(passwordEncoder.encode("password123"))
                .nickname("사교적인정")
                .email("social@example.com")
                .role(Role.USER)
                .address("천안시 동남구")
                .phone("010-7890-1234")
                .build(),
                
            User.builder()
                .username("lonely_yoon")
                .password(passwordEncoder.encode("password123"))
                .nickname("외로운윤")
                .email("lonely@example.com")
                .role(Role.USER)
                .address("창원시 의창구")
                .phone("010-8901-2345")
                .build(),
                
            User.builder()
                .username("office_worker")
                .password(passwordEncoder.encode("password123"))
                .nickname("직장인한")
                .email("office@example.com")
                .role(Role.USER)
                .address("포항시 북구")
                .phone("010-9012-3456")
                .build(),
                
            User.builder()
                .username("beginner_song")
                .password(passwordEncoder.encode("password123"))
                .nickname("운동초보송")
                .email("beginner@example.com")
                .role(Role.USER)
                .address("진주시 진주대로")
                .phone("010-0123-4567")
                .build(),
                
            User.builder()
                .username("introverted_oh")
                .password(passwordEncoder.encode("password123"))
                .nickname("조용한오")
                .email("introverted@example.com")
                .role(Role.USER)
                .address("순천시 중앙로")
                .phone("010-1234-5679")
                .build(),
                
            User.builder()
                .username("motivated_lim")
                .password(passwordEncoder.encode("password123"))
                .nickname("의욕적인임")
                .email("motivated@example.com")
                .role(Role.USER)
                .address("목포시 용해동")
                .phone("010-2345-6780")
                .build(),
                
            User.builder()
                .username("graduate_nam")
                .password(passwordEncoder.encode("password123"))
                .nickname("졸업생남")
                .email("graduate@example.com")
                .role(Role.USER)
                .address("군산시 나운동")
                .phone("010-3456-7891")
                .build(),
                
            User.builder()
                .username("newcomer_seo")
                .password(passwordEncoder.encode("password123"))
                .nickname("새내기서")
                .email("newcomer@example.com")
                .role(Role.USER)
                .address("익산시 영등동")
                .phone("010-4567-8902")
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
            // 지방 청년들을 위한 사회적 연결 중심의 15개 크루
            Crew.builder()
                .title("취준생 응원 러닝")
                .description("취업 준비하느라 지친 마음, 함께 달리며 서로 응원해요! 스트레스 해소와 동기부여까지 💪")
                .host(users.get(0)) // 취준러너김
                .maxParticipants(8)
                .routeId("route_jobseeker_001")
                .type("safe")
                .distanceKm(4.5)
                .safetyScore(92)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(35)
                .startLocation("대전 유성구 금강공원")
                .pace("6'30\"/km")
                .startTime(LocalDateTime.now().plusDays(2).withHour(19).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "36.3504,127.3845",
                    "36.3514,127.3855", 
                    "36.3509,127.3835"
                ))
                .tags(Arrays.asList("취준생", "스트레스해소", "동기부여", "친화적인", "대전"))
                .build(),
                
            Crew.builder()
                .title("내성적인 분들 환영 🤗")
                .description("말 걸기 어려워하는 분들도 괜찮아요. 천천히 자연스럽게 친해져요. 부담없는 분위기!")
                .host(users.get(1)) // 내성적인박
                .maxParticipants(6)
                .routeId("route_shy_002")
                .type("safe")
                .distanceKm(3.8)
                .safetyScore(95)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(28)
                .startLocation("부산 해운대 해수욕장")
                .pace("7'20\"/km")
                .startTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(30))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "35.1595,129.1606",
                    "35.1605,129.1616",
                    "35.1615,129.1626"
                ))
                .tags(Arrays.asList("내성적인", "부담없는", "천천히", "초보환영", "부산"))
                .build(),
                
            Crew.builder()
                .title("대학생 친구 만들기 🎓")
                .description("새 학기, 새로운 친구들과 함께 달려요! 대학생활 적응하기 힘든 분들 모여라~")
                .host(users.get(2)) // 대학생조
                .maxParticipants(12)
                .routeId("route_college_003")
                .type("safe")
                .distanceKm(5.0)
                .safetyScore(88)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(38)
                .startLocation("대구 앞산공원")
                .pace("7'30\"/km")
                .startTime(LocalDateTime.now().plusDays(3).withHour(17).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "35.8200,128.5400",
                    "35.8210,128.5410",
                    "35.8220,128.5420"
                ))
                .tags(Arrays.asList("대학생", "친구만들기", "사교적인", "새학기", "대구"))
                .build(),
                
            Crew.builder()
                .title("친화적인 분들과 함께 💕")
                .description("밝고 긍정적인 에너지로 함께 달려요! 서로 응원하고 격려하는 따뜻한 모임입니다.")
                .host(users.get(3)) // 친화적인이
                .maxParticipants(10)
                .routeId("route_friendly_004")
                .type("safe")
                .distanceKm(4.2)
                .safetyScore(90)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(32)
                .startLocation("광주 5·18기념공원")
                .pace("7'40\"/km")
                .startTime(LocalDateTime.now().plusDays(4).withHour(18).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "35.1500,126.9100",
                    "35.1510,126.9110",
                    "35.1520,126.9120"
                ))
                .tags(Arrays.asList("친화적인", "긍정적인", "따뜻한", "응원", "광주"))
                .build(),
                
            Crew.builder()
                .title("프리랜서 네트워킹 💼")
                .description("집에만 있어 답답한 프리랜서들! 운동도 하고 인맥도 만들어요. 서로 정보도 공유하고!")
                .host(users.get(4)) // 프리랜서최
                .maxParticipants(7)
                .routeId("route_freelancer_005")
                .type("safe")
                .distanceKm(5.5)
                .safetyScore(85)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(42)
                .startLocation("전주 덕진공원")
                .pace("7'40\"/km")
                .startTime(LocalDateTime.now().plusDays(5).withHour(16).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "35.8242,127.1480",
                    "35.8252,127.1490",
                    "35.8262,127.1500"
                ))
                .tags(Arrays.asList("프리랜서", "네트워킹", "정보공유", "자유로운", "전주"))
                .build(),
                
            Crew.builder()
                .title("러닝 완전 초보 모임 🌱")
                .description("운동화도 처음 신어보는 분들을 위한 모임! 아무것도 모르셔도 되어요. 천천히 시작해요.")
                .host(users.get(5)) // 러닝초보강
                .maxParticipants(15)
                .routeId("route_beginner_006")
                .type("safe")
                .distanceKm(2.8)
                .safetyScore(98)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(25)
                .startLocation("청주 용담대 주변")
                .pace("8'30\"/km")
                .startTime(LocalDateTime.now().plusDays(6).withHour(15).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "36.6358,127.4916",
                    "36.6368,127.4926"
                ))
                .tags(Arrays.asList("초보자", "운동초보", "천천히", "부담없는", "청주"))
                .build(),
                
            Crew.builder()
                .title("사교적인 사람들 모여라! 🎉")
                .description("말 많고 신나는 사람들과 함께! 러닝 후 맥주한잔도 좋아요. 에너지 넘치는 모임!")
                .host(users.get(6)) // 사교적인정
                .maxParticipants(14)
                .routeId("route_social_007")
                .type("safe")
                .distanceKm(6.2)
                .safetyScore(87)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(48)
                .startLocation("천안 독립기념관 일대")
                .pace("7'45\"/km")
                .startTime(LocalDateTime.now().plusDays(3).withHour(19).withMinute(30))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "36.8151,127.1139",
                    "36.8161,127.1149",
                    "36.8171,127.1159"
                ))
                .tags(Arrays.asList("사교적인", "에너지넘치는", "신나는", "맥주", "천안"))
                .build(),
                
            Crew.builder()
                .title("외로운 사람들의 힐링 러닝 💙")
                .description("혼자 있는 시간이 많아 외로운 분들, 함께 달리며 마음의 위로를 받아요. 서로 이해하는 따뜻한 모임.")
                .host(users.get(7)) // 외로운윤
                .maxParticipants(8)
                .routeId("route_lonely_008")
                .type("safe")
                .distanceKm(4.0)
                .safetyScore(92)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(32)
                .startLocation("창원 용지공원")
                .pace("8'00\"/km")
                .startTime(LocalDateTime.now().plusDays(4).withHour(18).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "35.2281,128.6811",
                    "35.2291,128.6821",
                    "35.2301,128.6831"
                ))
                .tags(Arrays.asList("외로운", "힐링", "위로", "따뜻한", "창원"))
                .build(),
                
            Crew.builder()
                .title("직장인 퇴근 후 스트레스 해소 💼")
                .description("하루 종일 일한 피로와 스트레스, 러닝으로 날려버려요! 직장 생활의 고충을 서로 나누며 달려요.")
                .host(users.get(8)) // 직장인한
                .maxParticipants(10)
                .routeId("route_office_009")
                .type("safe")
                .distanceKm(5.8)
                .safetyScore(86)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(45)
                .startLocation("포항 영일대해수욕장")
                .pace("7'45\"/km")
                .startTime(LocalDateTime.now().plusDays(5).withHour(19).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "36.0190,129.3650",
                    "36.0200,129.3660",
                    "36.0210,129.3670"
                ))
                .tags(Arrays.asList("직장인", "스트레스해소", "퇴근후", "공감", "포항"))
                .build(),
                
            Crew.builder()
                .title("운동 완전 처음이에요 🥺")
                .description("운동이라곤 체육시간 이후 처음인 분들! 같이 천천히 시작해요. 걷기부터 조깅까지 단계별로!")
                .host(users.get(9)) // 운동초보송
                .maxParticipants(12)
                .routeId("route_exercise_010")
                .type("safe")
                .distanceKm(3.2)
                .safetyScore(96)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(28)
                .startLocation("진주 남강댐 공원")
                .pace("8'45\"/km")
                .startTime(LocalDateTime.now().plusDays(6).withHour(16).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "35.1797,128.1076",
                    "35.1807,128.1086",
                    "35.1817,128.1096"
                ))
                .tags(Arrays.asList("운동초보", "완전처음", "단계별", "천천히", "진주"))
                .build(),
                
            Crew.builder()
                .title("조용한 성격이지만 친구는 만들고 싶어요 🤫")
                .description("말수는 적지만 좋은 사람들과 함께하고 싶은 분들! 조용히 달리다가 자연스럽게 친해져요.")
                .host(users.get(10)) // 조용한오
                .maxParticipants(6)
                .routeId("route_quiet_011")
                .type("safe")
                .distanceKm(4.8)
                .safetyScore(90)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(38)
                .startLocation("순천만국가정원")
                .pace("7'55\"/km")
                .startTime(LocalDateTime.now().plusDays(7).withHour(17).withMinute(30))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "34.8853,127.5095",
                    "34.8863,127.5105",
                    "34.8873,127.5115"
                ))
                .tags(Arrays.asList("내성적인", "조용한", "자연스럽게", "소수정예", "순천"))
                .build(),
                
            Crew.builder()
                .title("의욕 넘치는 사람들 모집! ⚡")
                .description("뭔가 새로운 도전을 하고 싶은 의욕적인 분들! 목표를 세우고 함께 달성해나가요. 동기부여 최고!")
                .host(users.get(11)) // 의욕적인임
                .maxParticipants(9)
                .routeId("route_motivated_012")
                .type("safe")
                .distanceKm(6.5)
                .safetyScore(84)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(50)
                .startLocation("목포 평화광장")
                .pace("7'40\"/km")
                .startTime(LocalDateTime.now().plusDays(4).withHour(19).withMinute(0))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "34.7881,126.3925",
                    "34.7891,126.3935",
                    "34.7901,126.3945"
                ))
                .tags(Arrays.asList("의욕적인", "도전", "목표달성", "동기부여", "목포"))
                .build(),
                
            Crew.builder()
                .title("갓 졸업한 사회초년생 모임 🎓")
                .description("대학교 막 졸업하고 사회생활 시작한 분들! 새로운 환경에 적응하며 함께 성장해요. 선후배 없이 편하게!")
                .host(users.get(12)) // 졸업생남
                .maxParticipants(11)
                .routeId("route_graduate_013")
                .type("safe")
                .distanceKm(5.2)
                .safetyScore(88)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(42)
                .startLocation("군산 은파호수공원")
                .pace("8'05\"/km")
                .startTime(LocalDateTime.now().plusDays(6).withHour(18).withMinute(30))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "35.9674,126.7188",
                    "35.9684,126.7198",
                    "35.9694,126.7208"
                ))
                .tags(Arrays.asList("졸업생", "사회초년생", "새로운환경", "편한분위기", "군산"))
                .build(),
                
            Crew.builder()
                .title("새내기들의 설렘 러닝 ✨")
                .description("뭔가 새로 시작하는 분들! 새 학교, 새 직장, 새로운 도시... 설레는 마음으로 함께 달려요!")
                .host(users.get(13)) // 새내기서
                .maxParticipants(13)
                .routeId("route_newcomer_014")
                .type("safe")
                .distanceKm(4.6)
                .safetyScore(91)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(36)
                .startLocation("익산 미륵사지")
                .pace("7'50\"/km")
                .startTime(LocalDateTime.now().plusDays(3).withHour(16).withMinute(30))
                .status(CrewStatus.OPEN)
                .waypoints(Arrays.asList(
                    "35.9907,126.9624",
                    "35.9917,126.9634",
                    "35.9927,126.9644"
                ))
                .tags(Arrays.asList("새내기", "새로운시작", "설렘", "적응", "익산"))
                .build(),
                
            Crew.builder()
                .title("완료된 힐링 러닝 (지난주)")
                .description("지난주에 성공적으로 마친 힐링 러닝 모임입니다. 다들 좋은 시간 보냈어요!")
                .host(users.get(1)) // 내성적인박
                .maxParticipants(8)
                .routeId("route_completed_015")
                .type("safe")
                .distanceKm(3.5)
                .safetyScore(94)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(28)
                .startLocation("부산 해운대 해수욕장")
                .pace("8'00\"/km")
                .startTime(LocalDateTime.now().minusDays(3).withHour(17).withMinute(0))
                .status(CrewStatus.CLOSED)
                .waypoints(Arrays.asList(
                    "35.1595,129.1606",
                    "35.1605,129.1616"
                ))
                .tags(Arrays.asList("완료", "힐링", "성공적", "좋은시간", "부산"))
                .build()
        );
        
        return crewRepository.saveAll(crews);
    }

    private void createCrewParticipants(List<User> users, List<Crew> crews) {
        log.info("크루 참여자 관계 생성 중...");
        
        // 취준생 응원 러닝 (index 0)
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(0))
                .user(users.get(12)) // 졸업생남
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(0))
                .user(users.get(13)) // 새내기서
                .status(ParticipantStatus.APPLIED)
                .build()
        );
        
        // 내성적인 분들 환영 (index 1)
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(1))
                .user(users.get(10)) // 조용한오
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(1))
                .user(users.get(7)) // 외로운
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        // 대학생 친구 만들기 (index 2)
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(2))
                .user(users.get(13)) // 새내기서
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(2))
                .user(users.get(6)) // 사교적인정
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        // 추가 참여자 관계들 - 다양한 크루에 다양한 사용자들이 참여
        
        // 한강 러닝 크루 (index 5)에 참여자들 추가
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(5)) // 한강 러닝 크루
                .user(users.get(0))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(5))
                .user(users.get(1))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(5))
                .user(users.get(2))
                .status(ParticipantStatus.APPLIED)
                .build()
        );
        
        // 새벽 5시 기상 크루 (index 6)에 참여자들 추가
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(6))
                .user(users.get(4))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(6))
                .user(users.get(7))
                .status(ParticipantStatus.APPLIED)
                .build()
        );
        
        // 주말 장거리 크루 (index 7)에 참여자들 추가
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(7))
                .user(users.get(4))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(7))
                .user(users.get(0))
                .status(ParticipantStatus.REJECTED)
                .build()
        );
        
        // 야간 러닝 동호회 (index 8)에 참여자들 추가
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(8))
                .user(users.get(1))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(8))
                .user(users.get(2))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(8))
                .user(users.get(5))
                .status(ParticipantStatus.APPLIED)
                .build()
        );
        
        // 초보자 환영 크루 (index 9)에 많은 참여자들 추가
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(9))
                .user(users.get(0))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(9))
                .user(users.get(2))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(9))
                .user(users.get(4))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(9))
                .user(users.get(5))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(9))
                .user(users.get(6))
                .status(ParticipantStatus.APPLIED)
                .build()
        );
        
        // 고강도 인터벌 크루 (index 10)에 소수 참여자들 추가
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(10))
                .user(users.get(0))
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(10))
                .user(users.get(6))
                .status(ParticipantStatus.APPLIED)
                .build()
        );
        
        // 여성 전용 러닝 크루 (index 11)에 참여자들 추가 (여성 사용자들만)
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(11))
                .user(users.get(5)) // 새벽러너
                .status(ParticipantStatus.APPROVED)
                .build()
        );
        
        crewParticipantRepository.save(
            CrewParticipant.builder()
                .crew(crews.get(11))
                .user(users.get(6)) // 주말러너
                .status(ParticipantStatus.APPLIED)
                .build()
        );
    }

    private void createTestReports(List<User> users) {
        log.info("테스트 신고 데이터 생성 중...");
        
        List<Report> reports = Arrays.asList(
            // 다양한 상태와 타입의 신고 데이터들
            Report.builder()
                .targetType(TargetType.ROUTE)
                .targetId(123L)
                .reporter(users.get(0)) // 취준러너김
                .reason("이 경로에 공사 중인 구간이 있어서 러닝하기 위험합니다. 특히 밤에는 조명이 부족해서 더 위험해요.")
                .status(ReportStatus.OPEN)
                .build(),
                
            Report.builder()
                .targetType(TargetType.LOCATION)
                .targetId(456L)
                .reporter(users.get(1)) // 내성적인박
                .reason("이 지역에 개가 풀어져 있어서 러닝하다가 놀랐습니다. 주인이 리드줄을 안 하고 있어요.")
                .status(ReportStatus.RESOLVED)
                .build(),
                
            Report.builder()
                .targetType(TargetType.HAZARD)
                .targetId(789L)
                .reporter(users.get(2)) // 대학생조
                .reason("맨홀 뚜껑이 열려있어서 발을 헛디딜 뻔했습니다. 안전사고 위험이 큽니다.")
                .status(ReportStatus.OPEN)
                .build(),
                
            Report.builder()
                .targetType(TargetType.ROUTE)
                .targetId(101L)
                .reporter(users.get(3)) // 친화적인이
                .reason("도로에 큰 구멍이 여러 개 있어서 넘어질 위험이 있어요. 비 온 후에는 물이 고여서 더 위험합니다.")
                .status(ReportStatus.OPEN)
                .build(),
                
            Report.builder()
                .targetType(TargetType.LOCATION)
                .targetId(202L)
                .reporter(users.get(4)) // 프리랜서최
                .reason("이 구간에 가로등이 고장나서 밤에는 너무 어둡습니다. 여성 혼자 뛰기에는 무서워요.")
                .status(ReportStatus.RESOLVED)
                .build(),
                
            Report.builder()
                .targetType(TargetType.HAZARD)
                .targetId(303L)
                .reporter(users.get(5)) // 러닝초보강
                .reason("인도에 자전거가 불법 주차되어 있어서 러닝하기 어렵습니다. 피하려다 차도로 나가게 돼요.")
                .status(ReportStatus.REJECTED)
                .build(),
                
            Report.builder()
                .targetType(TargetType.ROUTE)
                .targetId(404L)
                .reporter(users.get(6)) // 사교적인정
                .reason("이 경로 중간에 계단이 너무 가파르고 미끄러워서 위험합니다. 특히 비 올 때는 매우 위험해요.")
                .status(ReportStatus.OPEN)
                .build(),
                
            Report.builder()
                .targetType(TargetType.LOCATION)
                .targetId(505L)
                .reporter(users.get(7)) // 외로운윤
                .reason("공원 화장실 근처에서 이상한 냄새가 나고 위생상태가 좋지 않습니다.")
                .status(ReportStatus.OPEN)
                .build(),
                
            Report.builder()
                .targetType(TargetType.HAZARD)
                .targetId(606L)
                .reporter(users.get(8)) // 직장인한
                .reason("나무 가지가 너무 낮게 뻗어있어서 러닝하다가 머리를 부딪힐 뻔했습니다.")
                .status(ReportStatus.RESOLVED)
                .build(),
                
            Report.builder()
                .targetType(TargetType.ROUTE)
                .targetId(707L)
                .reporter(users.get(9)) // 운동초보송
                .reason("이 코스가 너무 가파르고 초보자에게는 위험한 것 같습니다. 난이도 조정이 필요해요.")
                .status(ReportStatus.OPEN)
                .build(),
                
            Report.builder()
                .targetType(TargetType.LOCATION)
                .targetId(808L)
                .reporter(users.get(10)) // 조용한오
                .reason("이 지역에 쓰레기가 많이 버려져 있어서 러닝 환경이 좋지 않습니다.")
                .status(ReportStatus.OPEN)
                .build(),
                
            Report.builder()
                .targetType(TargetType.HAZARD)
                .targetId(909L)
                .reporter(users.get(11)) // 의욕적인임
                .reason("운동기구가 고장나서 안전사고 위험이 있습니다. 빨리 수리가 필요해요.")
                .status(ReportStatus.RESOLVED)
                .build(),
                
            // 같은 사용자가 여러 신고를 한 경우
            Report.builder()
                .targetType(TargetType.ROUTE)
                .targetId(111L)
                .reporter(users.get(0)) // 취준러너김 (두 번째 신고)
                .reason("또 다른 경로에서 발견한 문제입니다. 보도블록이 들뜨거나 깨진 곳이 많아요.")
                .status(ReportStatus.OPEN)
                .build(),
                
            Report.builder()
                .targetType(TargetType.LOCATION)
                .targetId(222L)
                .reporter(users.get(1)) // 내성적인박 (두 번째 신고)
                .reason("공원 입구 근처에서 흡연하는 사람들이 많아서 연기 때문에 러닝하기 어려워요.")
                .status(ReportStatus.OPEN)
                .build(),
                
            Report.builder()
                .targetType(TargetType.HAZARD)
                .targetId(333L)
                .reporter(users.get(2)) // 대학생조 (두 번째 신고)
                .reason("벤치가 부러져서 앉으면 위험할 것 같습니다. 교체가 필요해요.")
                .status(ReportStatus.REJECTED)
                .build()
        );
        
        reportRepository.saveAll(reports);
        log.info("신고 테스트 데이터 {} 개 생성 완료", reports.size());
    }

    private void createTestFavorites(List<User> users) {
        log.info("테스트 즐겨찾기 데이터 생성 중...");
        
        List<Favorite> favorites = Arrays.asList(
            // 다양한 안전도와 태그를 가진 즐겨찾기 데이터들
            Favorite.builder()
                .user(users.get(0)) // 취준러너김
                .name("대전 유성구 안전한 러닝 코스")
                .waypoints(Arrays.asList(
                    "36.3504,127.3845",
                    "36.3514,127.3855", 
                    "36.3509,127.3835"
                ))
                .savedPolyline("encoded_polyline_data_1")
                .distanceM(4500)
                .durationS(2100) // 35분
                .safetyScore(92)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("안전한", "가로등", "평지", "공원", "대전"))
                .build(),
                
            Favorite.builder()
                .user(users.get(1)) // 내성적인박
                .name("부산 해운대 조용한 산책로")
                .waypoints(Arrays.asList(
                    "35.1595,129.1606",
                    "35.1605,129.1616",
                    "35.1615,129.1626"
                ))
                .savedPolyline("encoded_polyline_data_2")
                .distanceM(3800)
                .durationS(1680) // 28분
                .safetyScore(95)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("조용한", "해변", "경치좋은", "부산", "초보환영"))
                .build(),
                
            Favorite.builder()
                .user(users.get(2)) // 대학생조
                .name("대구 앞산공원 친구들과 함께")
                .waypoints(Arrays.asList(
                    "35.8200,128.5400",
                    "35.8210,128.5410",
                    "35.8220,128.5420"
                ))
                .savedPolyline("encoded_polyline_data_3")
                .distanceM(5000)
                .durationS(2280) // 38분
                .safetyScore(88)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("대학생", "친구들과", "공원", "대구", "활기찬"))
                .build(),
                
            Favorite.builder()
                .user(users.get(3)) // 친화적인이
                .name("광주 5·18기념공원 힐링 코스")
                .waypoints(Arrays.asList(
                    "35.1500,126.9100",
                    "35.1510,126.9110",
                    "35.1520,126.9120"
                ))
                .savedPolyline("encoded_polyline_data_4")
                .distanceM(4200)
                .durationS(1920) // 32분
                .safetyScore(90)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("힐링", "역사적", "평화로운", "광주", "의미있는"))
                .build(),
                
            Favorite.builder()
                .user(users.get(4)) // 프리랜서최
                .name("전주 덕진공원 프리랜서 네트워킹")
                .waypoints(Arrays.asList(
                    "35.8242,127.1480",
                    "35.8252,127.1490",
                    "35.8262,127.1500"
                ))
                .savedPolyline("encoded_polyline_data_5")
                .distanceM(5500)
                .durationS(2520) // 42분
                .safetyScore(85)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("프리랜서", "네트워킹", "연못", "전주", "자유로운"))
                .build(),
                
            Favorite.builder()
                .user(users.get(5)) // 러닝초보강
                .name("청주 용담댓가 초보자 코스")
                .waypoints(Arrays.asList(
                    "36.6358,127.4916",
                    "36.6368,127.4926"
                ))
                .savedPolyline("encoded_polyline_data_6")
                .distanceM(2800)
                .durationS(1500) // 25분
                .safetyScore(98)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("초보자", "짧은거리", "안전한", "청주", "부담없는"))
                .build(),
                
            Favorite.builder()
                .user(users.get(6)) // 사교적인정
                .name("천안 독립기념관 에너지 충전 코스")
                .waypoints(Arrays.asList(
                    "36.8151,127.1139",
                    "36.8161,127.1149",
                    "36.8171,127.1159"
                ))
                .savedPolyline("encoded_polyline_data_7")
                .distanceM(6200)
                .durationS(2880) // 48분
                .safetyScore(87)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("에너지충전", "역사", "긴거리", "천안", "활동적"))
                .build(),
                
            Favorite.builder()
                .user(users.get(7)) // 외로운윤
                .name("창원 용지공원 혼자만의 시간")
                .waypoints(Arrays.asList(
                    "35.2281,128.6811",
                    "35.2291,128.6821",
                    "35.2301,128.6831"
                ))
                .savedPolyline("encoded_polyline_data_8")
                .distanceM(4000)
                .durationS(1920) // 32분
                .safetyScore(92)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("혼자", "조용한", "사색", "창원", "평화로운"))
                .build(),
                
            Favorite.builder()
                .user(users.get(8)) // 직장인한
                .name("포항 영일대 퇴근 후 스트레스 해소")
                .waypoints(Arrays.asList(
                    "36.0190,129.3650",
                    "36.0200,129.3660",
                    "36.0210,129.3670"
                ))
                .savedPolyline("encoded_polyline_data_9")
                .distanceM(5800)
                .durationS(2700) // 45분
                .safetyScore(86)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("직장인", "스트레스해소", "바다", "포항", "퇴근후"))
                .build(),
                
            Favorite.builder()
                .user(users.get(9)) // 운동초보송
                .name("진주 남강댐 운동 첫걸음")
                .waypoints(Arrays.asList(
                    "35.1797,128.1076",
                    "35.1807,128.1086",
                    "35.1817,128.1096"
                ))
                .savedPolyline("encoded_polyline_data_10")
                .distanceM(3200)
                .durationS(1680) // 28분
                .safetyScore(96)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("운동초보", "첫걸음", "댐", "진주", "천천히"))
                .build(),
                
            // 중간 안전도 코스들
            Favorite.builder()
                .user(users.get(10)) // 조용한오
                .name("순천만 국가정원 중간 난이도")
                .waypoints(Arrays.asList(
                    "34.8853,127.5095",
                    "34.8863,127.5105",
                    "34.8873,127.5115"
                ))
                .savedPolyline("encoded_polyline_data_11")
                .distanceM(4800)
                .durationS(2280) // 38분
                .safetyScore(75)
                .safetyLevel(SafetyLevel.MEDIUM)
                .tags(Arrays.asList("자연", "습지", "중간난이도", "순천", "생태"))
                .build(),
                
            Favorite.builder()
                .user(users.get(11)) // 의욕적인임
                .name("목포 평화광장 도전 코스")
                .waypoints(Arrays.asList(
                    "34.7881,126.3925",
                    "34.7891,126.3935",
                    "34.7901,126.3945"
                ))
                .savedPolyline("encoded_polyline_data_12")
                .distanceM(6500)
                .durationS(3000) // 50분
                .safetyScore(70)
                .safetyLevel(SafetyLevel.MEDIUM)
                .tags(Arrays.asList("도전적", "긴거리", "바다", "목포", "의욕적"))
                .build(),
                
            // 위험도가 있는 코스들 (고급자용)
            Favorite.builder()
                .user(users.get(12)) // 졸업생남
                .name("군산 은파호수 고급자 코스")
                .waypoints(Arrays.asList(
                    "35.9674,126.7188",
                    "35.9684,126.7198",
                    "35.9694,126.7208"
                ))
                .savedPolyline("encoded_polyline_data_13")
                .distanceM(5200)
                .durationS(2520) // 42분
                .safetyScore(65)
                .safetyLevel(SafetyLevel.MEDIUM)
                .tags(Arrays.asList("고급자", "호수", "도전", "군산", "경험자"))
                .build(),
                
            Favorite.builder()
                .user(users.get(13)) // 새내기서
                .name("익산 미륵사지 탐험 코스")
                .waypoints(Arrays.asList(
                    "35.9907,126.9624",
                    "35.9917,126.9634",
                    "35.9927,126.9644"
                ))
                .savedPolyline("encoded_polyline_data_14")
                .distanceM(4600)
                .durationS(2160) // 36분
                .safetyScore(78)
                .safetyLevel(SafetyLevel.MEDIUM)
                .tags(Arrays.asList("탐험", "역사유적", "문화", "익산", "새로운"))
                .build(),
                
            // 같은 사용자가 여러 즐겨찾기를 가진 경우
            Favorite.builder()
                .user(users.get(0)) // 취준러너김 (두 번째 즐겨찾기)
                .name("대전 엑스포공원 야간 러닝")
                .waypoints(Arrays.asList(
                    "36.3704,127.3845",
                    "36.3714,127.3855"
                ))
                .savedPolyline("encoded_polyline_data_15")
                .distanceM(3500)
                .durationS(1800) // 30분
                .safetyScore(82)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("야간", "조명좋은", "엑스포", "대전", "안전한"))
                .build(),

            // 강원도 소외지역 즐겨찾기들
            Favorite.builder()
                .user(users.get(5)) // 러닝초보강
                .name("태백 황지연못 힐링 러닝")
                .waypoints(Arrays.asList(
                    "37.1641,128.9856",
                    "37.1651,128.9866",
                    "37.1661,128.9876"
                ))
                .savedPolyline("encoded_polyline_data_16")
                .distanceM(4200)
                .durationS(2400) // 40분
                .safetyScore(88)
                .safetyLevel(SafetyLevel.SAFE)
                .tags(Arrays.asList("태백", "황지연못", "힐링", "소외지역", "자연", "평화로운", "강원도"))
                .build(),

            Favorite.builder()
                .user(users.get(7)) // 외로운윤
                .name("정선 아리랑시장 주변 산책")
                .waypoints(Arrays.asList(
                    "37.3804,128.6607",
                    "37.3814,128.6617",
                    "37.3824,128.6627"
                ))
                .savedPolyline("encoded_polyline_data_17")
                .distanceM(3600)
                .durationS(2160) // 36분
                .safetyScore(75)
                .safetyLevel(SafetyLevel.MEDIUM)
                .tags(Arrays.asList("정선", "아리랑", "전통시장", "문화", "소외지역", "향토", "강원도"))
                .build(),

            Favorite.builder()
                .user(users.get(10)) // 조용한오
                .name("영월 동강 자전거길 러닝 코스")
                .waypoints(Arrays.asList(
                    "37.1833,128.4611",
                    "37.1843,128.4621",
                    "37.1853,128.4631",
                    "37.1863,128.4641"
                ))
                .savedPolyline("encoded_polyline_data_18")
                .distanceM(5800)
                .durationS(3480) // 58분
                .safetyScore(68)
                .safetyLevel(SafetyLevel.MEDIUM)
                .tags(Arrays.asList("영월", "동강", "자전거길", "강변", "소외지역", "자연경관", "강원도", "긴거리"))
                .build()
        );
        
        favoriteRepository.saveAll(favorites);
        log.info("즐겨찾기 테스트 데이터 {} 개 생성 완료", favorites.size());
    }
}
