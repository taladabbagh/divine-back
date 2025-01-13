package divine.controller;

import divine.dto.AuthenticationDTO;
import divine.service.AuthenticationService;
import divine.service.JWTService;
import divine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JWTService jwtService;
    private final AuthenticationService authenticationService;
//    private final UserService userService;

    @Autowired
    public AuthController(JWTService jwtService, AuthenticationService authenticationService, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
//        this.userService = userService;
    }

    @CrossOrigin
    @PostMapping("/login")
    public String login(@RequestBody AuthenticationDTO requestBody) {
        Authentication authentication = authenticationService.authenticate(requestBody);
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authentication);
        } else {
            throw new BadCredentialsException("Bad credentials");
        }
    }
}
