package skhumeet.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skhumeet.backend.domain.dto.HttpResponseDTO;
import skhumeet.backend.domain.dto.MemberDTO;
import skhumeet.backend.service.MemberService;

import java.util.Map;

@Tag(name = "Member API (회원 관련 API)", description = "API for authentication and authorization (사용자 인증과 인가를 위한 API)")
@RequestMapping("/api/member")
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "Join API (회원가입 API)", description = "Join with OAuth 2.0 accounts (OAuth 2.0 로그인을 통해 제공받은 정보들로 회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/join")
    public ResponseEntity<HttpResponseDTO> join(@RequestBody @Validated MemberDTO.Join request, Errors errors, Model model) {
        if (errors.hasErrors()) {
            System.out.println(errors);
            /* 유효성 통과 못한 필드와 메시지를 핸들링 */
            Map<String, String> validatorResult = memberService.validateHandling(errors);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            /* 입력한 내용을 유지하고자 응답 DTO에 담아서 보냄 */
            return new ResponseEntity<>(
                    new HttpResponseDTO(errors.toString(), null, new MemberDTO.Response(request.toEntity())), HttpStatus.BAD_REQUEST
            );
        }
        return memberService.join(request);
    }

    @Operation(summary = "Login API (로그인 API)", description = "Login with OAuth 2.0 (OAuth 2.0 로그인을 통해 제공 받은 ID 값으로 서비스 로그인)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/login")
    public ResponseEntity<HttpResponseDTO> login(@RequestBody MemberDTO.Login request) {
        return memberService.login(request);
    }
}
