# 4. Securing Spring

> **This chapter covers**
> - Autoconfiguring Spring Security
> - Defining custom user storage
> - Customizing the login page
> - Securing against CSRF attacks
> - Knowing your user

### Enabling Spring Data Cassandra

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
</dependency>
```

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

Configuration methods to define how a path is to be secured | Method | |  
| ---- | ---- | access(String)
anonymous()
authenticated()
denyAll()
fullyAuthenticated()
hasAnyAuthority(String...)
hasAnyRole(String...)
hasAuthority(String)
hasIpAddress(String)
Allows access if the given SpEL expression evaluates to true Allows access to anonymous users Allows access to
authenticated users Denies access unconditionally Allows access if the user is fully authenticated (not remembered)
Allows access if the user has any of the given authorities Allows access if the user has any of the given roles Allows
access if the user has the given authority Allows access if the request comes from the given IP address

| Method | 表# 4. Securing Spring

> **This chapter covers**
> - Autoconfiguring Spring Security
> - Defining custom user storage
> - Customizing the login page
> - Securing against CSRF attacks
> - Knowing your user

### Enabling Spring Data Cassandra

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
</dependency>
```

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
        .access("hasRole('USER') && "+
        "T(java.util.Calendar).getInstance().get("+
        "T(java.util.Calendar).DAY_OF_WEEK) == "+
        "T(java.util.Calendar).TUESDAY")
        .antMatchers(“/”,"/**").access("permitAll")
        ;
        }
```

```java
 @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/design", "/orders")
                .hasRole("USER")
                .antMatchers("/", "/**").permitAll()

                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/design");
    }
```









