package skhumeet.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skhumeet.backend.domain.member.MemberDTO;
import skhumeet.backend.service.MemberService;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/member")
@RestController
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/new")
    public ResponseEntity<MemberDTO.Response> join(MemberDTO.JoinRequest joinRequest, Errors errors, Model model) {
        if (errors.hasErrors()) {
            System.out.println(errors);
            Map<String, String> validatorResult = memberService.validateHandling(errors);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return ResponseEntity.badRequest().body(new MemberDTO.Response(joinRequest.toEntity()));
        }
        return ResponseEntity.ok(memberService.join(joinRequest));
    }
}
