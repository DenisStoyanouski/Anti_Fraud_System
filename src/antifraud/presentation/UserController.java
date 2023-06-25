package antifraud.presentation;

import antifraud.persistence.UserRepository;
import antifraud.businesslayer.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/actuator/shutdown")
    public void shutdown(){}

    @PostMapping("/api/auth/user")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        userRepo.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/api/auth/list")
    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    @DeleteMapping("/api/auth/user/{username}")
    @ResponseBody
    public ResponseEntity removeUser(@PathVariable String username) {
        if(userRepo.existsByUsername(username)) {
            userRepo.deleteByUsername(username);
            return ResponseEntity.ok(Map.of("username",  username, "status", "Deleted successfully!"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
