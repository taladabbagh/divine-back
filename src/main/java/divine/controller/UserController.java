package divine.controller;

import divine.dto.SignupDTO;
import divine.dto.UserDTO;
import divine.model.User;
import divine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User userDetails = userService.loadUserByUsername(username);
        if (userDetails == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDetails);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(@RequestBody UserDTO userDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User savedUser = userService.loadUserByUsername(username);
        if (savedUser == null) {
            return ResponseEntity.notFound().build();
        }

        savedUser.setUsername(userDTO.getUsername());
        savedUser.setEmail(userDTO.getEmail());
        savedUser.setFirstName(userDTO.getFirstName());
        savedUser.setLastName(userDTO.getLastName());
        String password = userDTO.getPassword();
        if (password != null) {
            savedUser.setPassword(passwordEncoder.encode(password));
        }

        userService.save(savedUser);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody SignupDTO signupDTO) {
        User user = userService.create(signupDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
