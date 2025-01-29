package divine.controller;

import divine.dto.AuthenticationDTO;
import divine.model.User;
import divine.service.AuthenticationService;
import divine.service.JWTService;
import divine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JWTService jwtService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Autowired
    public AuthController(JWTService jwtService, AuthenticationService authenticationService, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @CrossOrigin
    @PostMapping("/login")
    public String login(@RequestBody AuthenticationDTO requestBody) {
        Authentication authentication = authenticationService.authenticate(requestBody);
        if (authentication.isAuthenticated()) {
            // Retrieve authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.loadUserByEmail(userDetails.getUsername());
            System.out.println(jwtService.generateToken(String.valueOf(user.getId()), user.getEmail()));
            return jwtService.generateToken(String.valueOf(user.getId()), user.getEmail());

        } else {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @CrossOrigin
    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.loadUserById(id);
    }
}
