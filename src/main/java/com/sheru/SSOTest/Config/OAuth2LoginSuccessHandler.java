package com.sheru.SSOTest.Config;

import com.sheru.SSOTest.Model.User;
import com.sheru.SSOTest.Service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Value("${frontend.url}")
    private String frontendUrl;

    @Autowired
    private UserService userService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(frontendUrl + "/fetch");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String firstName, lastName, userId;
        if (attributes.containsKey("given_name")) {
            firstName = (String) attributes.get("given_name");
            lastName = (String) attributes.get("family_name");
        } else {
            String[] names = attributes.get("name").toString().split(" ");
            firstName = names[0];
            lastName = names[1];
        }
        userId = authentication.getName();
        User savedUser = userService.registerUser(User.builder().firstName(firstName).lastName(lastName).userId(userId).build());
        System.out.println(savedUser.toString());
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
