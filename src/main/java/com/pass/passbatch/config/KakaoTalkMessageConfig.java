package com.pass.passbatch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 카카오톡 API 호스트 및 토큰 설정
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kakaotalk")
public class KakaoTalkMessageConfig {
    private String host;
    private String token;
}
