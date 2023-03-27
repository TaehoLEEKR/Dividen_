package com.example.dividend.web;

import com.example.dividend.security.TokenProvider;
import com.example.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.dividend.model.Auth;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    //@PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> signup (@RequestBody Auth.SignUp request){
        var result = this.memberService.register(request);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/signin")
    //@PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> signin (@RequestBody Auth.SignIn request){
        var member  = this.memberService.authenticate(request);
        var token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());
        log.info("user login -> " + request.getUsername());
        return ResponseEntity.ok(token);
    }
}
