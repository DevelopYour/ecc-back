package com.seoultech.ecc.member.service;

import com.seoultech.ecc.member.dto.CustomUserDetails;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security에서 사용자 인증을 처리하기 위한 서비스
 *
 * 이 클래스는 Spring Security 인터페이스를 구현하므로 loadUserByUsername 메서드를
 * 변경할 수 없음. 이 메서드는 로그인 시 username(학번)으로 사용자를 조회함.
 *
 * 그러나 사용자 식별은 내부적으로 username(학번)이 아닌 uuid를 사용함.
 * CustomUserDetails 클래스에서 getId() 메서드를 통해 uuid를 얻을 수 있음.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Autowired
    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Spring Security에서 호출하는 메서드
     * 로그인 시 학번(studentId)으로 사용자 조회
     *
     * 주의: 이 메서드는 로그인 용도로만 사용하고,
     * 실제 엔티티 간 관계 참조에는 항상 uuid를 사용할 것
     */
    @Override
    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {
        // 학번으로 사용자 조회 (로그인 용도)
        MemberEntity memberEntity = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        // 인증 성공 시 CustomUserDetails 객체를 반환
        // Controller에서는 getId() 메서드를 통해 uuid를 사용하여 사용자 식별 가능
        return new CustomUserDetails(memberEntity);
    }

    /**
     * UUID로 사용자 조회 (회원 식별용)
     * 인증 후 사용자 식별에 사용하는 메서드
     *
     * @param uuid 회원 UUID
     * @return CustomUserDetails 객체
     * @throws UsernameNotFoundException 회원을 찾을 수 없을 경우
     */
    public UserDetails loadUserByUuid(Integer uuid) throws UsernameNotFoundException {
        MemberEntity memberEntity = memberRepository.findById(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. UUID: " + uuid));

        return new CustomUserDetails(memberEntity);
    }
}