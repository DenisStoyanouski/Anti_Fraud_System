package antifraud.presentation;

import antifraud.persistence.UserRepository;
import antifraud.businesslayer.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        if (userRepo.findUserByUsername(user.getUsername()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        userRepo.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/api/auth/list")
    public List<User> getAllUsers() {
        return userRepo.findAllUsers();
    }

}
