package divine.controller;

import divine.dto.SignupDTO;
import divine.dto.UserDTO;
import divine.model.User;
import divine.service.UserService;
import jakarta.validation.Valid;
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
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // Authenticated email
        User userDetails = userService.loadUserByEmail(email);
        if (userDetails == null) {
            return ResponseEntity.notFound().build();
        }

        // Map User to UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(userDetails.getEmail());
        userDTO.setFirstName(userDetails.getFirstName());
        userDTO.setLastName(userDetails.getLastName());

        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserDTO userDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // Authenticated email
        User savedUser = userService.loadUserByEmail(email);
        if (savedUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Update user details
        savedUser.setFirstName(userDTO.getFirstName());
        savedUser.setLastName(userDTO.getLastName());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            savedUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userService.save(savedUser);

        // Map updated User to UserDTO
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setEmail(savedUser.getEmail());
        updatedUserDTO.setFirstName(savedUser.getFirstName());
        updatedUserDTO.setLastName(savedUser.getLastName());

        return ResponseEntity.ok(updatedUserDTO);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody SignupDTO signupDTO) {
        User newUser = userService.create(signupDTO);

        // Map new User to UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(newUser.getEmail());
        userDTO.setFirstName(newUser.getFirstName());
        userDTO.setLastName(newUser.getLastName());

        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }
}
