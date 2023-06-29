package antifraud.businesslayer;

import antifraud.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = userRepo.findByUsername(username).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new UsernameNotFoundException("Not found: " + username);
        }
        return new UserDetailsImpl(user);
    }
}
