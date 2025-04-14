package com.seoultech.ecc.global.init;

import com.seoultech.ecc.member.datamodel.College;
import com.seoultech.ecc.member.datamodel.MajorEntity;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import com.seoultech.ecc.member.repository.MajorRepository;
import com.seoultech.ecc.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final MajorRepository majorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.student-id:00000000}")
    private String adminStudentId;

    @Value("${admin.name:ECC Admin}")
    private String adminName;

    @Value("${admin.password:admin1234}")
    private String adminPassword;

    @Value("${admin.tel:01012345678}")
    private String adminTel;

    @Value("kakaoteltest")
    private String kakaoTel;

    @Value("${admin.email:admin@ecc.com}")
    private String adminEmail;

    @Value("${admin.major-id:1}")
    private Long adminMajorId;

    @Value("${admin.auto-create:true}")
    private boolean autoCreateAdmin;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        System.out.println("============ AdminInitializer 실행 시작 ============");

        if (!autoCreateAdmin) {
            log.info("관리자 계정 자동 생성이 비활성화되어 있습니다. (admin.auto-create=false)");
            return;
        }

        try {
            initAdminAccount();
            System.out.println("============ AdminInitializer 실행 완료 ============");
        } catch (Exception e) {
            System.err.println("관리자 계정 생성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initAdminAccount() {
        log.info("Admin 설정 값: student-id={}, name={}, major-id={}",
                adminStudentId, adminName, adminMajorId);

        // 기존 관리자 계정이 있는지 확인
        if (memberRepository.findByStudentId(adminStudentId).isPresent()) {
            log.info("관리자 계정이 이미 존재합니다. 학번: {}", adminStudentId);
            return;
        }

        // Major 테이블 데이터 확인
        log.info("Major 테이블 데이터 확인 - 전체 개수: {}", majorRepository.count());

        // Major가 없으면 기본 Major 생성
        MajorEntity major;
        if (majorRepository.count() == 0) {
            log.info("Major 테이블에 데이터가 없습니다. 기본 Major 생성");
            MajorEntity defaultMajor = new MajorEntity();
            defaultMajor.setName("기술경영학과");
            defaultMajor.setCollege(College.BUSINESS);
            major = majorRepository.save(defaultMajor);
            log.info("기본 Major 생성 완료: id={}, name={}", major.getId(), major.getName());
            adminMajorId = major.getId(); // 새로 생성된 ID로 업데이트
        } else {
            // 관리자 전공 정보 확인
            log.info("Major 테이블에서 ID={}인 전공 조회 시도", adminMajorId);
            major = majorRepository.findById(adminMajorId)
                    .orElseThrow(() -> {
                        log.error("관리자 계정 생성 실패: 전공 ID {} 를 찾을 수 없습니다.", adminMajorId);
                        return new RuntimeException("관리자 계정 생성을 위한 전공 정보를 찾을 수 없습니다.");
                    });
            log.info("Major 조회 성공: id={}, name={}", major.getId(), major.getName());
        }

        // 관리자 계정 생성
        log.info("관리자 계정 생성 시작");
        MemberEntity admin = MemberEntity.builder()
                .studentId(adminStudentId)
                .name(adminName)
                .password(passwordEncoder.encode(adminPassword))
                .tel(adminTel)
                .kakaoTel(kakaoTel)
                .email(adminEmail)
                .level(3) // 관리자는 최고 레벨
                .rate(5.0) // 관리자는 최고 평점
                .status(MemberStatus.ACTIVE) // 활성 상태
                .major(major)
                .motivation("ECC 웹사이트 관리자")
                .role("ROLE_ADMIN") // 관리자 역할만 부여
                .build();

        memberRepository.save(admin);
        log.info("관리자 계정이 성공적으로 생성되었습니다. 학번: {}", adminStudentId);
    }
}