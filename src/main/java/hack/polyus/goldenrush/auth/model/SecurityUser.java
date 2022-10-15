package hack.polyus.goldenrush.auth.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Data
@ToString
public class SecurityUser implements UserDetails {

    private final String email;
    private final String password;
    private final Set<SimpleGrantedAuthority> authorities;
    private final boolean isActive;
    private final User user;

    public SecurityUser(String email, String password, Set<SimpleGrantedAuthority> authorities, boolean isActive, User user) {
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.isActive = isActive;
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public static UserDetails fromUser(User user) {
        /*return new SecurityUser(
                user.getEmail(), user.getPassword(),
                user.getRole().getAuthorities(),
                user.getStatus().equals(Status.ACTIVE)
        );*/
        /*UserDetails res = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getStatus().equals(Status.ACTIVE),
                user.getRole().getAuthorities()
        );*/
        return new SecurityUser(
                user.getEmail(), user.getPassword(),
                user.getRole().getAuthorities(),
                true,
                user
        );
    }

    public static SecurityUser getCurrent(){
        return  ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}

