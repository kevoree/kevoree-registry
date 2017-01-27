package org.kevoree.registry.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kevoree.registry.Application;
import org.kevoree.registry.domain.Authority;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
public class UserServiceTest {

    private static final String JOHNDOE = "johndoe";

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private NamespaceRepository nsRepository;

    @Test
    public void testFindNotActivatedUsersByCreationDateBefore() {
        userService.removeNotActivatedUsers();
        ZonedDateTime now = ZonedDateTime.now();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        assertThat(users).isEmpty();
    }

    @Test
    public void testRemoveNotActivatedUser() {
        // create non-activated John Doe
        createNonActivatedJohnDoe();

        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime.now().minusDays(3));
        assertThat(users.size()).isEqualTo(1);

        // then try to remove non-activated users
        userService.removeNotActivatedUsers();

        // assert that it has been removed
        users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime.now().minusDays(3));
        assertThat(users).isEmpty();
    }

    @Test
    public void testRemoveNotActivatedUserThatHasBeenAddedInANamespace() {
        // create non-activated John Doe
        createNonActivatedJohnDoe();

        // add johndoe to a namespace
        User john = userRepository.findOneByLogin(JOHNDOE).get();
        Namespace kevoree = nsRepository.findOne("kevoree");
        kevoree.addMember(john);
        john.addNamespace(kevoree);
        nsRepository.save(kevoree);
        nsRepository.flush();

        // assert that it has been added to namespace
        Namespace dbKevoree = nsRepository.findOne("kevoree");
        User dbJohn = userRepository.findOneByLogin(JOHNDOE).get();
        assertThat(dbKevoree.getMembers()).contains(dbJohn);
        assertThat(dbJohn.getNamespaces()).contains(dbKevoree);

        // then try to remove non-activated users
        userService.removeNotActivatedUsers();

        // assert that it has been removed
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime.now().minusDays(3));
        assertThat(users).isEmpty();

        // assert that it has also been removed from namespace members
        dbKevoree = nsRepository.findOne("kevoree");
        for (User member : dbKevoree.getMembers()) {
            assertThat(member.getLogin()).isNotEqualTo(JOHNDOE);
        }
    }

    private void createNonActivatedJohnDoe() {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        authorities.add(authority);

        ZonedDateTime now = ZonedDateTime.now();

        // create a non-activated John Doe user
        User user = new User();
        user.setLogin(JOHNDOE);
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@doe.org");
        user.setAuthorities(authorities);
        // add it to the db
        User savedUser = userRepository.save(user);
        // trick to set an old createdDate for test purpose
        savedUser.setCreatedDate(now.minusDays(4));
        userRepository.save(savedUser);
        userRepository.flush();

        // assert that it has been added
        User john = userRepository.findOneByLogin(JOHNDOE).get();
        assertThat(john).isNotNull();
    }
}
