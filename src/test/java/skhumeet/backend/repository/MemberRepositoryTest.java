//package skhumeet.backend.repository;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import skhumeet.backend.domain.member.Authority;
//import skhumeet.backend.domain.member.Member;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//public class MemberRepositoryTest {
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Test
//    @DisplayName("Sign in test")
//    void join() {
//        // given
//        Member member = Member.builder()
//                .memberNumber("201914099")
//                .email("skfcb10@naver.com")
//                .password("@testpassword")
//                .authority(Authority.ADMIN)
//                .name("이한길")
//                .build();
//
//        // when
//        memberRepository.save(member);
//        Member findByMemberNumber = memberRepository.findByMemberNumber("201914099").orElseThrow();
//        Member findByEmail = memberRepository.findByEmail("skfcb10@naver.com").orElseThrow();
//
//        // then
//        Assertions.assertThat(findByMemberNumber.equals(findByEmail)).isTrue();
//    }
//}
