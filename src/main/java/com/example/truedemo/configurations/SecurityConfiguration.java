package com.example.truedemo.configurations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/products/**").permitAll()
                .antMatchers(HttpMethod.GET, "/", "/images/**", "/main.css", "/webjars/**").permitAll()
                .antMatchers(HttpMethod.POST, "/images").hasRole("USER")
                .antMatchers("/imageMessages/**").permitAll()
                .antMatchers(HttpMethod.POST, "/products").permitAll()

                .antMatchers(HttpMethod.GET, "/").permitAll()

                .and()
                .formLogin().permitAll().and().logout().logoutSuccessUrl("/");
    }

    @Autowired
    public void configureInMemoryUsers(AuthenticationManagerBuilder amb) throws Exception {
        amb.inMemoryAuthentication()
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .withUser("greg").password("turn").roles("ADMIN", "USER")
                .and()
                .withUser("safi").password("zuri").roles("USER")
                .and()
                .withUser("baddie1").password("baddie1").roles("USER").disabled(true)
                .and()
                .withUser("baddie2").password("baddie2").roles("USER").accountLocked(true);
    }
}
