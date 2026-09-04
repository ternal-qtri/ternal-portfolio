package com.ternal.ternalportfolio.config;

import com.ternal.ternalportfolio.entity.User;
import com.ternal.ternalportfolio.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(u -> org.springframework.security.core.userdetails.User
                        .withUsername(u.getUsername())
                        .password(u.getPasswordHash())
                        .authorities(u.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại: " + username));
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(UserRepository userRepo) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User oAuth2User = delegate.loadUser(request);
            String email = oAuth2User.getAttribute("email");
            if (email == null) {
                throw new OAuth2AuthenticationException(new OAuth2Error("invalid_request"), "Không tìm thấy email từ Google.");
            }

            // Chỉ cho phép đăng nhập nếu email đã được tạo trước trong database với quyền ROLE_ADMIN
            User user = userRepo.findByUsername(email)
                    .orElseThrow(() -> new OAuth2AuthenticationException(
                            new OAuth2Error("access_denied"), "Email " + email + " không tồn tại trong hệ thống."));

            if (!"ROLE_ADMIN".equals(user.getRole())) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("access_denied"), "Tài khoản không có quyền quản trị.");
            }

            return new DefaultOAuth2User(
                    Set.of(new SimpleGrantedAuthority(user.getRole())),
                    oAuth2User.getAttributes(),
                    "email"
            );
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserDetailsService userDetailsService,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/skills", "/projects", "/projects/detail", "/contact", "/contact/**").permitAll()
                .requestMatchers("/error", "/error/**").permitAll()
                .requestMatchers("/admin/login", "/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    request.getRequestDispatcher("/error/403").forward(request, response);
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/admin")) {
                        response.sendRedirect("/admin/login");
                    } else {
                        response.setStatus(401);
                        request.getRequestDispatcher("/error/401").forward(request, response);
                    }
                })
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/projects", true)
                .failureUrl("/admin/login?error=true")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("ternal-portfolio-secret-key")
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(30 * 24 * 60 * 60) // 30 ngày
                .userDetailsService(userDetailsService)
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/admin/login")
                .defaultSuccessUrl("/admin/projects", true)
                .failureUrl("/admin/login?error=oauth2")
                .userInfoEndpoint(info -> info.userService(oAuth2UserService))
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout=true")
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            );

        return http.build();
    }
}
