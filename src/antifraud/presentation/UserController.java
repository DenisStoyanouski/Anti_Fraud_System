package antifraud.presentation;

import antifraud.businesslayer.*;
import antifraud.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.*;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @PostMapping("/actuator/shutdown")
    public void shutdown() {
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        if (userRepo.count() == 0) {
            user.setRole(Role.ADMINISTRATOR);
            user.setNonLooked(true);
        } else {
            user.setRole(Role.MERCHANT);
            user.setNonLooked(false);
        }
        userRepo.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/api/auth/list")
    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    @DeleteMapping("/api/auth/user/{username}")
    @ResponseBody
    public ResponseEntity removeUser(@AuthenticationPrincipal UserDetails details, @PathVariable String username) {
        if (userRepo.existsByUsername(username) && !Objects.equals(username, details.getUsername())) {
            userRepo.deleteByUsername(username);
            Map<String, String> resp = new LinkedHashMap<>();
            resp.put("username", username);
            resp.put("status", "Deleted successfully!");
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/api/auth/role")
    @ResponseBody
    public ResponseEntity changeUserRole(@Valid @RequestBody RoleChanger roleChanger) {
        String username = roleChanger.getUsername();
        String role = roleChanger.getRole();
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (Objects.equals(user.get().getRole().name(), role)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (!user.get().getRole().name().matches("(MERCHANT|SUPPORT)")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        user.get().setRole(Role.valueOf(role));
        user.get().setNonLooked(true);
        userRepo.save(user.get());

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/api/auth/access")
    @ResponseBody
    public ResponseEntity unlockUser(@Valid @RequestBody UserLocker userLocker) {
        Optional<User> user = userRepo.findByUsername(userLocker.getUsername());
        if (user.isPresent()) {
            if ("ADMINISTRATOR".equals(user.get().getRole().name())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            switch (userLocker.getOperation()) {
                case "LOCK" -> user.get().setNonLooked(false);
                case "UNLOCK" -> user.get().setNonLooked(true);
            }
            userRepo.save(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String status = String.format("User %s %sed!", userLocker.getUsername(), userLocker.getOperation().toLowerCase());
        return ResponseEntity.ok(Map.of("status", status));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handler(
            MethodArgumentNotValidException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
