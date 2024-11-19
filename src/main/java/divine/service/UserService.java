package divine.service;

import divine.dto.SignupDTO;
import divine.model.User;
import divine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Method to create a new user
    public User create(SignupDTO signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());

        // Encoding password before saving
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        user.setPassword(encodedPassword);

        // Save and return the user
        return save(user);
    }

    // Method to load user by username (needed for authentication)
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User '%s' not found", username)));
    }

    // Pagination for retrieving users
    public Page<User> getUsers(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return userRepository.findAll(pageable);
    }

    // Saving the user to the database
    public User save(User user) {
        return userRepository.save(user);
    }

    // Deleting a user by their ID
    public void deleteById(int userId) {
        userRepository.deleteById(userId);
    }

    // Method to find users based on specific query parameters
    public List<User> findUsersByQueryParams(Specification<User> spec) {
        return userRepository.findAll(spec);
    }

    // Updating the last login time of the user
    public void updateLastLoginTime(String username, Instant lastLoginTime) {
        userRepository.updateUserLastLogin(username, lastLoginTime);
    }
}
