package licence.code.generator.entities;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Entity
@Table(name = "user_account")
@Data
public class User implements UserDetails {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    private boolean isLocked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @OneToMany(mappedBy = "user")
    private Collection<Licence> licences;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName().name()));
            role.getPrivileges().stream()
                    .map(p -> new SimpleGrantedAuthority(p.getName()))
                    .forEach(authorities::add);
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean hasRole(RoleName roleName) {
        return roles.stream().anyMatch(role -> role.getName().equals(roleName));
    }


    @Override
    public String toString() {
        List<String> roles = Stream.ofNullable(this.roles)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(Role::getName)
                .map(Enum::name)
                .collect(toList());
        List<String> products = Stream.ofNullable(this.licences)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(Licence::getProduct)
                .map(Product::getName)
                .collect(toList());
        return "User [username=" +
                username +
                ", id=" +
                id +
                ", isLocked=" +
                isLocked +
                ", roles=" +
                roles +
                ", products=" +
                products +
                ", email=" +
                email + "]";
    }
}
