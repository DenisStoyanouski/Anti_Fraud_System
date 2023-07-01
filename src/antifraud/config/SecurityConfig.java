package antifraud.config;

import antifraud.businesslayer.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(userDetailsService);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .antMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasAuthority(Role.ADMINISTRATOR.name())
                .antMatchers(HttpMethod.POST, "/api/auth/user/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/auth/list/**").hasAnyAuthority(Role.ADMINISTRATOR.name(), Role.SUPPORT.name())
                .antMatchers(HttpMethod.POST, "/api/antifraud/**").hasAuthority(Role.MERCHANT.name())
                .antMatchers(HttpMethod.PUT, "/api/auth/access/**").hasAuthority(Role.ADMINISTRATOR.name())
                .antMatchers(HttpMethod.PUT, "/api/auth/role/**").hasAuthority(Role.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasAuthority(Role.SUPPORT.name())
                .mvcMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/{ip}").hasAuthority(Role.SUPPORT.name())
                .mvcMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasAuthority(Role.SUPPORT.name())
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasAuthority(Role.SUPPORT.name())
                .mvcMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/{number}").hasAuthority(Role.SUPPORT.name())
                .mvcMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasAuthority(Role.SUPPORT.name())
                .antMatchers("/actuator/shutdown").permitAll()// needs to run test
                .anyRequest().authenticated()
                // other matchers
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
        return http.build();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
