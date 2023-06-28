package antifraud.presentation;

import antifraud.businesslayer.Role;
import antifraud.persistence.UserRepository;
import antifraud.businesslayer.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
        if (userRepo.count() == 0) {
            user.setRole(Role.ADMINISTRATOR);
        } else {
            user.setRole(Role.MERCHANT);
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

    @PutMapping("/api/auth/role")
    @ResponseBody
    public ResponseEntity changeUserRole(@RequestBody Map<String, String> request) {
        if (!request.containsKey("username") || !request.containsKey("role")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String username = request.get("username");
        String role = request.get("role");
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (Objects.equals(user.get().getRole().name(), role)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (!role.matches("(SUPPORT|MERCHANT)")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        user.get().setRole(Role.valueOf(role));
        userRepo.save(user.get());
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
