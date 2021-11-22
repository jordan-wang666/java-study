# 4. Securing Spring

> **This chapter covers**
> - Autoconfiguring Spring Security
> - Defining custom user storage
> - Customizing the login page
> - Securing against CSRF attacks
> - Knowing your user

### Enabling Spring Data Cassandra

By doing nothing more than adding the security starter to the project build, you get the following security features:

- All HTTP request paths require authentication.
- No specific roles or authorities are required.
- There’s no login page.
- Authentication is prompted with HTTP basic authentication.
- There’s only one user; the username is user.

You can see password in log, similar like this:

```
Using generated security password: b9c6ed99-f699-444d-b944-f1bd96ccbd7c
```

### Configuring Spring Security

As it turns out, Spring Security offers several options for configuring a user store, including these:

- A memory user store
- JDBC-based user storage
- User store backed by LDAP
- Customized user details service

### In-memory user store

```java
package com.taco.cloud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("buzz")
                .password("infinity")
                .authorities("USER")
                .and()
                .withUser("woody")
                .password("bullseye")
                .authorities("USER");
    }
}
```

### JDBC-based user store

```java
@Override
protected void configure(AuthenticationManagerBuilder auth)
        throws Exception{

        auth
        .jdbcAuthentication()
        .dataSource(dataSource)
        .usersByUsernameQuery(
        "select username, password, enabled from Users "+
        "where username=?")
        .authoritiesByUsernameQuery(
        "select username, authority from UserAuthorities "+
        "where username=?")
        .passwordEncoder(new StandardPasswordEncoder("53cr3t");
```

The passwordEncoder() method accepts any implementation of Spring Security's passwordEncoder interface. Spring
Security's encryption module includes several such implementations:

- <kbd>BCryptPasswordEncoder</kbd> - uses bcrypt strong hash encryption
- <kbd>NoOpPasswordEncoder</kbd> - no encoding is applied
- <kbd>Pbkdf2PasswordEncoder</kbd> - apply PBKDF2 encryption
- <kbd>SCryptPasswordEncoder</kbd> - applied scrypt hash encryption
- <kbd>StandardPasswordEncoder</kbd> - apply SHA-256 hash encryption

### LDAP-backed user store

LDAP - Lightweight Directory Access Protocol

```java
@Override
protected void configure(AuthenticationManagerBuilder auth)throws Exception{
        auth
        .ldapAuthentication()
        .userSearchBase("ou=people")
        .userSearchFilter("(uid={0})")
        .groupSearchBase("ou=groups")
        .groupSearchFilter("member={0}")
        .passwordCompare()
        .passwordEncoder(new BCryptPasswordEncoder())
        .passwordAttribute("passcode")
        .contextSource()
        .root("dc=tacocloud,dc=com")
        .ldif("classpath:users.ldif");
        }
```

```
dn: ou=groups,dc=tacocloud,dc=com
objectclass: top
objectclass: organizationalUnit
ou: groups
dn: ou=people,dc=tacocloud,dc=com
objectclass: top
objectclass: organizationalUnit
ou: people
dn: uid=buzz,ou=people,dc=tacocloud,dc=com
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Buzz Lightyear
sn: Lightyear
uid: buzz
userPassword: password
dn: cn=tacocloud,ou=groups,dc=tacocloud,dc=com
objectclass: top
objectclass: groupOfNames
cn: tacocloud
member: uid=buzz,ou=people,dc=tacocloud,dc=com
```

### Customizing user authentication

User implements the UserDetails interface from Spring Security

```java
package com.taco.cloud.entity.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Collections;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String fullname;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String phoneNumber;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

Defining a custom user details service

```java
package com.taco.cloud.service;

import com.taco.cloud.dao.UserRepository;
import com.taco.cloud.entity.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

    private UserRepository userRepo;

    @Autowired
    public UserRepositoryUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException("User '" + username + "' not found");
    }
}
```

### Securing web requests

The security requirements for Taco Cloud should require that a user be authenticated before designing tacos or placing
orders. But the homepage, login page, and registration page should be available to unauthenticated users. To configure
these security rules, let me introduce you to <kbd>WebSecurityConfigurer- Adapter’s</kbd> other <kbd>configure()</kbd>
method:

```java
@Override
protected void configure(HttpSecurity http)throws Exception{
        ...
        }
```

This <kbd>configure()</kbd> method accepts an <kbd>HttpSecurity</kbd> object, which can be used to configure how
security is handled at the web level. Among the many things you can configure with <kbd>HttpSecurity</kbd> are these:

- Requiring that certain security conditions be met before allowing a request to be served
- Configuring a custom login page
- Enabling users to log out of the application
- Configuring cross-site request forgery protection

Intercepting requests to ensure that the user has proper authority is one of the most common things you’ll configure
<kbd>HttpSecurity</kbd> to do.

The following con- figure() implementation does exactly that:

```java
@Override
protected void configure(HttpSecurity http)throws Exception{
        http
        .authorizeRequests()
        .antMatchers("/design","/orders")
        .hasRole("USER")
        .antMatchers(“/”,"/**").permitAll()
        ;
        }
```

Configuration methods to define how a path is to be secured
### 4. Securing Spring

> **This chapter covers**
> - Autoconfiguring Spring Security
> - Defining custom user storage
> - Customizing the login page
> - Securing against CSRF attacks
> - Knowing your user

By doing nothing more than adding the security starter to the project build, you get the following security features:

- All HTTP request paths require authentication.
- No specific roles or authorities are required.
- There’s no login page.
- Authentication is prompted with HTTP basic authentication.
- There’s only one user; the username is user.

You can see password in log, similar like this:

```
Using generated security password: b9c6ed99-f699-444d-b944-f1bd96ccbd7c
```

### Configuring Spring Security

As it turns out, Spring Security offers several options for configuring a user store, including these:

- A memory user store
- JDBC-based user storage
- User store backed by LDAP
- Customized user details service

### In-memory user store

```java
package com.taco.cloud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("buzz")
                .password("infinity")
                .authorities("USER")
                .and()
                .withUser("woody")
                .password("bullseye")
                .authorities("USER");
    }
}
```

### JDBC-based user store

```java
@Override
protected void configure(AuthenticationManagerBuilder auth)
        throws Exception{

        auth
        .jdbcAuthentication()
        .dataSource(dataSource)
        .usersByUsernameQuery(
        "select username, password, enabled from Users "+
        "where username=?")
        .authoritiesByUsernameQuery(
        "select username, authority from UserAuthorities "+
        "where username=?")
        .passwordEncoder(new StandardPasswordEncoder("53cr3t");
```

The passwordEncoder() method accepts any implementation of Spring Security's passwordEncoder interface. Spring
Security's encryption module includes several such implementations:

- <kbd>BCryptPasswordEncoder</kbd> - uses bcrypt strong hash encryption
- <kbd>NoOpPasswordEncoder</kbd> - no encoding is applied
- <kbd>Pbkdf2PasswordEncoder</kbd> - apply PBKDF2 encryption
- <kbd>SCryptPasswordEncoder</kbd> - applied scrypt hash encryption
- <kbd>StandardPasswordEncoder</kbd> - apply SHA-256 hash encryption

### LDAP-backed user store

LDAP - Lightweight Directory Access Protocol

```java
@Override
protected void configure(AuthenticationManagerBuilder auth)throws Exception{
        auth
        .ldapAuthentication()
        .userSearchBase("ou=people")
        .userSearchFilter("(uid={0})")
        .groupSearchBase("ou=groups")
        .groupSearchFilter("member={0}")
        .passwordCompare()
        .passwordEncoder(new BCryptPasswordEncoder())
        .passwordAttribute("passcode")
        .contextSource()
        .root("dc=tacocloud,dc=com")
        .ldif("classpath:users.ldif");
        }
```

```
dn: ou=groups,dc=tacocloud,dc=com
objectclass: top
objectclass: organizationalUnit
ou: groups
dn: ou=people,dc=tacocloud,dc=com
objectclass: top
objectclass: organizationalUnit
ou: people
dn: uid=buzz,ou=people,dc=tacocloud,dc=com
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Buzz Lightyear
sn: Lightyear
uid: buzz
userPassword: password
dn: cn=tacocloud,ou=groups,dc=tacocloud,dc=com
objectclass: top
objectclass: groupOfNames
cn: tacocloud
member: uid=buzz,ou=people,dc=tacocloud,dc=com
```

### Customizing user authentication

User implements the UserDetails interface from Spring Security

```java
package com.taco.cloud.entity.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Collections;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String fullname;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String phoneNumber;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

Defining a custom user details service

```java
package com.taco.cloud.service;

import com.taco.cloud.dao.UserRepository;
import com.taco.cloud.entity.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

    private UserRepository userRepo;

    @Autowired
    public UserRepositoryUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException("User '" + username + "' not found");
    }
}
```

### Securing web requests

The security requirements for Taco Cloud should require that a user be authenticated before designing tacos or placing
orders. But the homepage, login page, and registration page should be available to unauthenticated users. To configure
these security rules, let me introduce you to <kbd>WebSecurityConfigurer- Adapter’s</kbd> other <kbd>configure()</kbd>
method:

```java
@Override
protected void configure(HttpSecurity http)throws Exception{
        ...
        }
```

This <kbd>configure()</kbd> method accepts an <kbd>HttpSecurity</kbd> object, which can be used to configure how
security is handled at the web level. Among the many things you can configure with <kbd>HttpSecurity</kbd> are these:

- Requiring that certain security conditions be met before allowing a request to be served
- Configuring a custom login page
- Enabling users to log out of the application
- Configuring cross-site request forgery protection

Intercepting requests to ensure that the user has proper authority is one of the most common things you’ll configure
<kbd>HttpSecurity</kbd> to do.

The following con- figure() implementation does exactly that:

```java
@Override
protected void configure(HttpSecurity http)throws Exception{
        http
        .authorizeRequests()
        .antMatchers("/design","/orders")
        .hasRole("USER")
        .antMatchers(“/”,"/**").permitAll()
        ;
        }
```

Configuration methods to define how a path is to be secured

|  Method   | What it does  |
|  ----  | ----  |
| access(String) | Allows access if the given SpEL expression evaluates to true
| anonymous() | Allows access to anonymous users
| authenticated() | Allows access to authenticated users
| denyAll() | Denies access unconditionally
| fullyAuthenticated() | Allows access if the user is fully authenticated (not remembered)
| hasAnyAuthority(String...) | Allows access if the user has any of the given authorities
| hasAnyRole(String...) | Allows access if the user has any of the given roles
| hasAuthority(String) | Allows access if the user has the given authority
| hasIpAddress(String) | Allows access if the request comes from the given IP address
| hasRole(String) | Allows access if the user has the given role
| not() | Negates the effect of any of the other access methods
| permitAll() | Allows access unconditionally
| rememberMe() | Allows access for users who are authenticated via remember-me

Spring Security extensions to the Spring Expression Language

| Security expression | What it evaluates to |
|  ----  | ----  |
| authentication | The user’s authentication object |
| denyAll | Always evaluates to false
| hasAnyRole(list of roles) | true if the user has any of the given roles
| hasRole(role) | true if the user has the given role
| hasIpAddress(IP address) | true if the request comes from the given IP address
| isAnonymous() | true if the user is anonymous
| isAuthenticated() | true if the user is authenticated
| isFullyAuthenticated() | true if the user is fully authenticated (not authenticated with remember-me)
| isRememberMe() | true if the user was authenticated via remember-me
| permitAll | Always evaluates to true
| principal | The user’s principal object

```java
@Override
protected void configure(HttpSecurity http)throws Exception{
        http
        .authorizeRequests()
        .antMatchers("/design","/orders")
        .access("hasRole('ROLE_USER') && "+
        "T(java.util.Calendar).getInstance().get("+
        "T(java.util.Calendar).DAY_OF_WEEK) == "+
        "T(java.util.Calendar).TUESDAY")
        .antMatchers(“/”,"/**").access("permitAll")
        ;
        }
```

```java
 @Override
protected void configure(HttpSecurity http)throws Exception{
        http
        .authorizeRequests()
        .antMatchers("/design","/orders")
        .hasRole("USER")
        .antMatchers("/","/**").permitAll()

        .and()
        .formLogin()
        .loginPage("/login")
        .defaultSuccessUrl("/design");
        }
```

### Logging out

```
    .and()
          .logout()
            .logoutSuccessUrl("/")
```

```html

<form method="POST" th:action="@{/logout}">
    <input type="submit" value="Logout"/>
</form>
```

### Preventing cross-site request forgery

Cross-site request forgery (CSRF) is a common security attack.

It’s possible to disable CSRF support, but I’m hesitant to show you how. CSRF protec- tion is important and easily
handled in forms, so there’s little reason to disable it. But if you insist on disabling it, you can do so by calling
disable() like this:

```
    .and()
        .csrf()
        .disable()
```

Again, I caution you not to disable CSRF protection, especially for production applications.

### Knowing your user

There are several ways to determine who the user is. These are a few of the most common ways:

- Inject a Principal object into the controller method
- Inject an Authentication object into the controller method
- Use SecurityContextHolder to get at the security context
- Use an @AuthenticationPrincipal annotated method

```java
@PostMapping
public String processOrder(@Valid Order order,Errors errors,
        SessionStatus sessionStatus,
        Principal principal){
        ...
        User user=userRepository.findByUsername(
        principal.getName());
        order.setUser(user);
        ...
        }


@PostMapping
public String processOrder(@Valid Order order,Errors errors,
        SessionStatus sessionStatus,
        Authentication authentication){
        ...
        User user=(User)authentication.getPrincipal();
        order.setUser(user);
        ...
        }

@PostMapping
public String processOrder(@Valid Order order,Errors errors,
        SessionStatus sessionStatus,
@AuthenticationPrincipal User user){
        if(errors.hasErrors()){
        return"orderForm";
        }
        order.setUser(user);
        orderRepo.save(order);
        sessionStatus.setComplete();
        return"redirect:/";
        }
```

There’s one other way of identifying who the authenticated user is, although it’s a bit messy in the sense that it’s
very heavy with security-specific code. You can obtain an `Authentication` object from the security context and then
request its principal like this:

```
Authentication authentication =
SecurityContextHolder.getContext().getAuthentication();
User user = (User) authentication.getPrincipal();
```

Although this snippet is thick with security-specific code, it has one advantage over the other approaches described:
**it can be used anywhere in the application, not only in a controller’s handler methods**. This makes it suitable for
use in lower levels of the code.

### Summary

- Spring Security autoconfiguration is a great way to get started with security, but most applications will need to
  explicitly configure security to meet their unique security requirements.
- User details can be managed in user stores backed by relational databases, LDAP, or completely custom implementations.
- Spring Security automatically protects against CSRF attacks.
- Information about the authenticated user can be obtained via the Security- Context object (returned from
  SecurityContextHolder.getContext()) or injected into controllers using @AuthenticationPrincipal.



