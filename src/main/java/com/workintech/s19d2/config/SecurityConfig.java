package com.workintech.s19d2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    // PasswordEncoder bean'i lazım çünkü AuthenticationService ctor'unda PasswordEncoder var
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // testler için csrf kapatalım

                .authorizeHttpRequests(auth -> auth
                        // herkes erişebilsin:
                        .requestMatchers("/auth/register").permitAll()

                        // GET /account -> USER veya ADMIN erişsin
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/account", "/account/**")
                        .hasAnyAuthority("USER", "ADMIN")

                        // POST, PUT, DELETE /account -> sadece ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/account", "/account/**")
                        .hasAuthority("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/account", "/account/**")
                        .hasAuthority("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/account", "/account/**")
                        .hasAuthority("ADMIN")

                        // diğer her şey -> auth zorunlu
                        .anyRequest().authenticated()
                )

                .httpBasic(basic -> {}); // basic auth aktif

        return http.build();
    }

    // bazı test senaryoları için Spring UserDetailsService bean'i gerekiyor çünkü
    // @WebMvcTest SecurityConfig'i import ediyor ama UserDetailsService'i mockluyor.
    // Biz burada bean tanımlamazsak context patlayabilir normalde.
    // Ama test zaten @MockBean UserDetailsService veriyor. Yani bu bean zorunlu DEĞİL.
    // (Eğer patlama olursa buraya default bir in-memory impl koyarız.)

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
