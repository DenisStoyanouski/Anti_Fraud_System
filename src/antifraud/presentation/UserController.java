package antifraud.presentation;

import antifraud.persistence.UserRepository;
import antifraud.businesslayer.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/actuator/shutdown")
    public void shutdown(){}

    @PostMapping("/api/auth/user")
    public void register(@Validated @RequestBody User user) {
        // TODO: 24.06.2023  input validation omitted for brevity

        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
    }


}
