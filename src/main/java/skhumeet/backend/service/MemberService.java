package skhumeet.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import skhumeet.backend.domain.member.MemberDTO;
import skhumeet.backend.repository.MemberRepository;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
