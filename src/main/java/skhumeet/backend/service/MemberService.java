package skhumeet.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import skhumeet.backend.domain.member.Member;
import skhumeet.backend.domain.member.MemberDTO;
import skhumeet.backend.domain.member.NaverUserInfo;
import skhumeet.backend.domain.member.OAuth2UserInfo;
import skhumeet.backend.repository.MemberRepository;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;
        String provider = userRequest.getClientRegistration().getRegistrationId();

        if(provider.equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        }

        Optional<Member> optionalMember = memberRepository.findByLoginId(oAuth2UserInfo.getProviderId());
        Member member = null;

        return oAuth2User;
    }

    @Transactional
    public MemberDTO.Response join(MemberDTO.@Valid JoinRequest request) {
        if(memberRepository.findByMemberNumber(request.getMemberNumber()).isPresent()) {
            throw new IllegalArgumentException("Already joined member");
        }
        if(!request.getPassword().equals(request.getCheckedPassword())) {
            throw new IllegalArgumentException("Password mismatch");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        memberRepository.saveAndFlush(request.toEntity());
        return new MemberDTO.Response(memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("Member not found, member save failed")));
    }

    @Transactional(readOnly = true)
    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }

        return validatorResult;
    }
}
