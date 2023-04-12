package skhumeet.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import skhumeet.backend.domain.dto.HttpResponseDTO;
import skhumeet.backend.domain.dto.MemberDTO;
import skhumeet.backend.domain.dto.TokenDTO;
import skhumeet.backend.service.MemberService;
import skhumeet.backend.token.TokenProvider;

import java.io.IOException;
import java.util.Map;

@Tag(name = "Member API (회원 관련 API)", description = "API for authentication and authorization (사용자 인증과 인가를 위한 API)")
@RequestMapping("/api/member")
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    // Create (회원 가입)
    @Operation(
            summary = "Join API (회원 가입 API)",
            description = """
                    Join with OAuth 2.0 accounts.<br/>
                    OAuth 2.0 로그인을 통해 제공받은 정보들로 자체 회원 가입.
                    """
    )
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

    // Read (회원 정보 조회 API)
    @Operation(
            summary = "Read Member information (회원 정보 조회 API)",
            description = """
                    Read member information from database. Authorize needed.<br/>
                    데이터베이스로부터 회원 정보 조회. 로그인(토큰) 필요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/info")
    public MemberDTO.Response getInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return memberService.findByLoginId(userDetails.getUsername());
    }

    // Update (회원 수정)
    @Operation(
            summary = "Update Member API (회원 정보 수정 API)",
            description = """
                    Update member information. Authorize needed.<br/>
                    회원 정보 수정. 로그인(토큰) 필요. (현재 프로필 이미지만 수정 가능)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PatchMapping
    public ResponseEntity<MemberDTO.Response> update(@AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestBody MemberDTO.Update update) throws IOException {
        return ResponseEntity.ok(memberService.update(userDetails.getUsername(), update));
    }

    // ETC (회원 관련 기타 API)
    @Operation(
            summary = "Login API (로그인 API)",
            description = """
                    Login with OAuth 2.0.<br/>
                    OAuth 2.0 로그인을 통해 제공 받은 ID 값으로 로그인.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/login")
    public ResponseEntity<HttpResponseDTO> login(@RequestBody MemberDTO.Login request) {
        return memberService.login(request);
    }

    @Operation(
            summary = "Token Reissue API (토큰 재발급 API)",
            description = """
                    Reissue expired token.<br/>
                    만료된 AccessToken을 RefreshToken을 통해 갱신.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/reissue")
    public ResponseEntity<HttpResponseDTO> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        return ResponseEntity.ok(new HttpResponseDTO("Reissue Success", tokenProvider.reissue(refreshToken)));
    }

    @Operation(
            summary = "Logout API (로그아웃 API)",
            description = """
                    Logout with Redis database<br/>
                    AccessToken과 RefreshToken을 통해 로그아웃
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("AccessToken") String accessToken,
                                         @RequestHeader("RefreshToken") String refreshToken) {
        String username = tokenProvider.getUsername(tokenProvider.resolveToken(accessToken));
        TokenDTO tokens = TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return memberService.logout(tokens, username);
    }
}
