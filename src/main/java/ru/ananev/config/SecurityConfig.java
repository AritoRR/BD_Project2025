package ru.ananev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("1111"))
                .roles("ADMIN")
                .build();

        UserDetails operator = User.builder()
                .username("operator")
                .password(passwordEncoder().encode("1111"))
                .roles("OPERATOR")
                .build();

        return new InMemoryUserDetailsManager(admin, operator);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                // Админ имеет доступ ко всему
                .antMatchers("/masters/**", "/cars/**", "/services/**", "/users/**").hasRole("ADMIN")
                // Оператор может только создавать работы и смотреть отчеты
                .antMatchers("/works/**", "/reports/**").hasAnyRole("OPERATOR", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                .and()
                .csrf().disable();
    }
}