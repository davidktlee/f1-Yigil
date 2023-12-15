package kr.co.yigil.login.application.strategy;

import static kr.co.yigil.global.exception.ExceptionCode.INVALID_ACCESS_TOKEN;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import kr.co.yigil.global.exception.InvalidTokenException;
import kr.co.yigil.login.dto.request.LoginRequest;
import kr.co.yigil.login.dto.response.KakaoTokenInfoResponse;
import kr.co.yigil.login.dto.response.LoginResponse;
import kr.co.yigil.member.domain.Member;
import kr.co.yigil.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:url.properties")
public class KakaoLoginStrategy implements LoginStrategy {

    private final String PROVIDER_NAME = "kakao";

    @Value("${kakao.token.info.url}")
    private String KAKAO_TOKEN_INFO_URL;

    private final MemberRepository memberRepository;

    @Override
    public LoginResponse login(LoginRequest request, String accessToken, HttpSession session) {
        LoginRequest loginRequest = request;

        if(!isTokenValid(accessToken, loginRequest.getId())) {
            throw new InvalidTokenException(INVALID_ACCESS_TOKEN);
        }

        Member member = memberRepository.findMemberBySocialLoginId(loginRequest.getId().toString())
                .orElseGet(() -> registerNewMember(loginRequest));

        session.setAttribute("memberId", member.getId());

        LoginResponse response = new LoginResponse();
        response.setMessage("로그인 성공");
        return response;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    private boolean isTokenValid(String accessToken, Long expectedUserId) {
        KakaoTokenInfoResponse tokenInfo = requestKakaoTokenInfo(accessToken);
        return isUserIdValid(tokenInfo, expectedUserId);
    }

    private KakaoTokenInfoResponse requestKakaoTokenInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoTokenInfoResponse> response = restTemplate.exchange(
                    KAKAO_TOKEN_INFO_URL, HttpMethod.GET, entity, KakaoTokenInfoResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new InvalidTokenException(INVALID_ACCESS_TOKEN);
        }
    }

    private boolean isUserIdValid(KakaoTokenInfoResponse tokenInfo, Long expectedUserId) {
        return tokenInfo != null && tokenInfo.getId().equals(expectedUserId);
    }

    private Member registerNewMember(LoginRequest request) {
        Member newMember = new Member(request.getEmail(), request.getId().toString(), request.getNickname(), request.getProfileImageUrl());
        return memberRepository.save(newMember);
    }

}