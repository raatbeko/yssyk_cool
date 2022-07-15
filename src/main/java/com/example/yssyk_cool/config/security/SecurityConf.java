package com.example.yssyk_cool.config.security;

import com.example.yssyk_cool.util.SecurityRole;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConf extends WebSecurityConfigurerAdapter {

    final DataSource dataSource;

    public SecurityConf(DataSource dataSource)     {
        this.dataSource = dataSource;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)throws Exception{
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("SELECT t.user_name, t.password, t.is_active FROM users t WHERE t.user_name = ?")
                .authoritiesByUsernameQuery(
                        "SELECT u.user_name, ur.name_role" +
                                "FROM user_role ur" +
                                "INNER JOIN users u " +
                                "on ur.user_id = u.id" +
                                "INNER JOIN roles r" +
                                "on ur.role_id = r.id" +
                                "WHERE u.user_name = ? AND u.is_active = 1");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/").permitAll()
                .antMatchers(HttpMethod.POST, "api/v1/user/auto").permitAll()
                .antMatchers(HttpMethod.POST, "api/v1/user/register").permitAll()
                .antMatchers(HttpMethod.GET,"api/v1/user/{id}").permitAll()
                .antMatchers(HttpMethod.GET,"api/v1/user").permitAll()

                .antMatchers(HttpMethod.GET,"api/common-reference/get-cities").permitAll()
                .antMatchers(HttpMethod.GET,"api/common-reference/get-areas").permitAll()

                .antMatchers(HttpMethod.POST,"api/complex/save").hasAnyRole(SecurityRole.ROLE_USER .getName(),SecurityRole.ROLE_PROVIDER.getName())
                .antMatchers(HttpMethod.GET,"api/complex/{id}").permitAll()
                .antMatchers(HttpMethod.GET,"api/complex").permitAll()
                .antMatchers(HttpMethod.GET,"api/complex/get-by-user-id/{id}").hasRole(SecurityRole.ROLE_PROVIDER.getName())

                .antMatchers(HttpMethod.POST,"api/file/save").hasRole(SecurityRole.ROLE_PROVIDER.getName())
                .antMatchers(HttpMethod.GET,"api/file/get-by-complex-id").permitAll()

                .antMatchers(HttpMethod.POST,"api/review/save").hasRole(SecurityRole.ROLE_USER.getName())
                .antMatchers(HttpMethod.GET,"api/review/get-by-complex-id/{id}").permitAll()
                .antMatchers(HttpMethod.GET,"api//review/get-all-by-complex-id/{id}").permitAll()

                .antMatchers(HttpMethod.GET,"/swagger-ui.html").permitAll()
                .and()
                .httpBasic();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
