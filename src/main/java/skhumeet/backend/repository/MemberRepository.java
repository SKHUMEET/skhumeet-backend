package skhumeet.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skhumeet.backend.domain.member.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByMemberNumber(String memberNumber);
    Optional<Member> findByLoginId(String loginId);
}
