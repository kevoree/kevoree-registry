package org.kevoree.registry.web.rest.vm;

import org.kevoree.registry.domain.Authority;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.service.dto.UserDTO;

import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public ManagedUserVM(User user) {
        super(user);
        this.password = user.getPassword();
    }

    public ManagedUserVM(Long id, String login, String password, String firstName, String lastName,
                         String email, boolean activated, String langKey,
                         String createdBy, ZonedDateTime createdDate, String lastModifiedBy, ZonedDateTime lastModifiedDate,
                         Set<String> authorities, Set<String> namespaces) {
        this(new User.Builder()
                .id(id)
                .login(login)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .activated(activated)
                .langKey(langKey)
                .authorities(authorities.stream().map(name -> {
                    Authority authority = new Authority();
                    authority.setName(name);
                    return authority;
                }).collect(Collectors.toSet()))
                .namespaces(namespaces.stream().map(name -> {
                    Namespace ns = new Namespace();
                    ns.setName(name);
                    return ns;
                }).collect(Collectors.toSet()))
                .createdBy(createdBy)
                .createdDate(createdDate)
                .lastModifiedBy(lastModifiedBy)
                .lastModifiedDate(lastModifiedDate)
                .build());
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ManagedUserVM{" +
                "} " + super.toString();
    }
}
