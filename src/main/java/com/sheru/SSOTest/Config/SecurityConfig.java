package com.sheru.SSOTest.Config;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {
                    cors.configurationSource(corsConfigurationSource());
                })
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth2 -> {
                    oauth2.loginPage(frontendUrl + "/login");

                    oauth2.successHandler(oAuth2LoginSuccessHandler);
                }).logout((config) -> {
                    config.invalidateHttpSession(true);
                    config.clearAuthentication(true);
                    config.addLogoutHandler(((request, response, authentication) -> {
                        for (Cookie cookie : request.getCookies()) {
                            String cookieName = cookie.getName();
                            Cookie cookieToDelete = new Cookie(cookieName, null);
                            cookieToDelete.setMaxAge(0);
                            response.addCookie(cookieToDelete);
                        }

                    }));
                    config.logoutUrl("/logout").logoutSuccessUrl(frontendUrl).permitAll();
                    config.invalidateHttpSession(true);
                });

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5000"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
