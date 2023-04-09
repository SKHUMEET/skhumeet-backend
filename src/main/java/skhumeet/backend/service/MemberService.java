package skhumeet.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import skhumeet.backend.domain.dto.HttpResponseDTO;
import skhumeet.backend.domain.dto.MemberDTO;
import skhumeet.backend.domain.dto.TokenDTO;
import skhumeet.backend.domain.member.*;
import skhumeet.backend.repository.MemberRepository;
import skhumeet.backend.token.TokenProvider;

import javax.validation.Valid;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseEntity<HttpResponseDTO> login(MemberDTO.@Valid Login request) {
        if (memberRepository.findByLoginId(request.getLoginId()).isEmpty()) {
            return new ResponseEntity<>(
                    new HttpResponseDTO("Member not found, signup needed", null, request.getLoginId()), HttpStatus.NOT_FOUND
            );
        }

        // Response DTOs
        TokenDTO tokens = tokenProvider.createTokens(request.getLoginId());
        MemberDTO.Response memberInfo = new MemberDTO.Response(memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new NoSuchElementException("Member not found")));

        return ResponseEntity.ok(new HttpResponseDTO("Login success", tokens, memberInfo));
    }

    @Transactional
    public ResponseEntity<HttpResponseDTO> join(MemberDTO.@Valid Join request) {
        if (memberRepository.findByLoginId(request.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("Duplicated ID that provided from OAuth 2.0 API");
        }

        TokenDTO tokens = tokenProvider.createTokens(request.getLoginId());
        Member member = memberRepository.saveAndFlush(request.toEntity());
        MemberDTO.Response memberInfo = new MemberDTO.Response(member);

        return ResponseEntity.ok(new HttpResponseDTO("Join success", tokens, memberInfo));
    }

    /* 회원가입 시, 유효성 체크 */
    @Transactional(readOnly = true)
    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        /* 유효성 검사에 실패한 필드 목록을 받음 */
        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }
}
