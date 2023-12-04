package com.sheru.SSOTest.Controller;

import com.sheru.SSOTest.Model.User;
import com.sheru.SSOTest.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/demo")
@CrossOrigin(origins = "http://localhost:5000")
public class DemoController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello ji");
    }

    @GetMapping("/username")
    @ResponseBody
    public ResponseEntity<String> getUsername(Authentication authentication) {
//        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        String firstName = (String) attributes.get("given_name");
//        String lastName = (String) attributes.get("family_name");
//        String userId = oAuth2User.getName();
//        String response = "First Name: " + firstName + "\n\nLast Name: " + lastName + "\n\nUser Id: " + userId;
        return ResponseEntity.ok(authentication.toString());
    }

    @GetMapping("/userDetails")
    public User getUserDetails(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();
        String firstName, lastName, userId;
        if (attributes.containsKey("given_name")) {
            firstName = (String) attributes.get("given_name");
            lastName = (String) attributes.get("family_name");
        } else {
            String[] names = attributes.get("name").toString().split(" ");
            firstName = names[0];
            lastName = names[1];
        }
        userId = principal.getName();
        User newUser = userService.findById(userId);
        System.out.println(newUser);
        return newUser;
    }
}
