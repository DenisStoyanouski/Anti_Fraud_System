package antifraud.presentation;

import antifraud.businesslayer.*;
import antifraud.persistence.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
// TODO: 02.07.2023 Use @RequestMapping("/api/auth") and @XMapping("/user")
// TODO: 02.07.2023 Use @PreAuthorize("hasRole('ADMINISTRATOR')") instead of antMatchers in Security FilterChain 
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

    @Operation(summary = "Registration of a new user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User is registered",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Wrong response format",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "User exists",
                    content = @Content) })
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

    // TODO: 02.07.2023 use @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Get list of all users")
    @GetMapping("/api/auth/list")
    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Operation(summary = "Delete user by his username")
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

    @Operation(summary = "Change user's role")
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

    @Operation(summary = "Lock/Unlock use by his username")
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
