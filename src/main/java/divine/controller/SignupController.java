package divine.controller;

import divine.dto.SignupDTO;
import divine.model.User;
import divine.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
public class SignupController {

    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> signup(@RequestBody SignupDTO signupDTO) {
        User user = userService.create(signupDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
