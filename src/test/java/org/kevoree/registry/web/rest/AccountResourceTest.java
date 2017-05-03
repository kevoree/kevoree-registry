package org.kevoree.registry.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kevoree.registry.Application;
import org.kevoree.registry.domain.Authority;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.AuthorityRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.service.MailService;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.web.rest.vm.ManagedUserVM;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.collections.Sets;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AccountResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
//@WebAppConfiguration
public class AccountResourceTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private UserService userService;

    @Inject
    private Environment env;

    @Mock
    private UserService mockUserService;

    @Mock
    private MailService mockMailService;

    private MockMvc restUserMockMvc;

    private MockMvc restMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMailService).sendActivationEmail(anyObject());

        AccountResource accountResource = new AccountResource();
        ReflectionTestUtils.setField(accountResource, "env", env);
        ReflectionTestUtils.setField(accountResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(accountResource, "namespaceRepository", namespaceRepository);
        ReflectionTestUtils.setField(accountResource, "userService", userService);
        ReflectionTestUtils.setField(accountResource, "mailService", mockMailService);

        AccountResource accountUserMockResource = new AccountResource();
        ReflectionTestUtils.setField(accountUserMockResource, "env", env);
        ReflectionTestUtils.setField(accountUserMockResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(accountUserMockResource, "namespaceRepository", namespaceRepository);
        ReflectionTestUtils.setField(accountUserMockResource, "userService", mockUserService);
        ReflectionTestUtils.setField(accountUserMockResource, "mailService", mockMailService);

        this.restMvc = MockMvcBuilders.standaloneSetup(accountResource).build();
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(accountUserMockResource).build();
    }

    @Test
    public void testNonAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/api/authenticate")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/api/authenticate")
                .with(request -> {
                    request.setRemoteUser("test");
                    return request;
                })
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

    @Test
    public void testGetExistingAccount() throws Exception {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorities.add(authority);

        User user = new User();
        user.setLogin("test");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setEmail("john.doe@jhipter.com");
        user.setAuthorities(authorities);
        when(mockUserService.getUserWithAuthorities()).thenReturn(user);

        restUserMockMvc.perform(get("/api/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.login").value("test"))
                .andExpect(jsonPath("$.firstName").value("john"))
                .andExpect(jsonPath("$.lastName").value("doe"))
                .andExpect(jsonPath("$.email").value("john.doe@jhipter.com"))
                .andExpect(jsonPath("$.authorities").value(AuthoritiesConstants.ADMIN));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        when(mockUserService.getUserWithAuthorities()).thenReturn(null);

        restUserMockMvc.perform(get("/api/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    public void testRegisterValid() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM(
                null,                   // id
                "joe",                  // login
                "password",             // password
                "Joe",                  // firstName
                "Shmoe",                // lastName
                "joe@example.com",      // e-mail
                true,                   // activated
                "en",                   // langKey
                null,                   // createdBy
                null,                   // createdDate
                null,                   // lastModifiedBy
                null,                   // lastModifiedDate
                Sets.newSet(AuthoritiesConstants.USER),
                new HashSet<>()
        );

        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(validUser)))
                .andExpect(status().isCreated());

        Optional<User> user = userRepository.findOneByLogin("joe");
        assertThat(user.isPresent()).isTrue();
        Namespace namespace = namespaceRepository.findOne("joe");
        assertThat(namespace).isNotNull();
        assertThat(user.get().getNamespaces()).contains(namespace);
        assertThat(namespace.getOwner()).isEqualTo(user.get());
        assertThat(namespace.getMembers()).contains(user.get());
    }

    @Test
    @Transactional
    public void testRegisterInvalidLogin() throws Exception {
        ManagedUserVM u = new ManagedUserVM(
                null,                   // id
                "funky-log!n",          // login <-- invalid
                "password",             // password
                "Joe",                  // firstName
                "Shmoe",                // lastName
                "funky@example.com",      // e-mail
                true,                   // activated
                "en",                   // langKey
                null,                   // createdBy
                null,                   // createdDate
                null,                   // lastModifiedBy
                null,                    // lastModifiedDate
                Sets.newSet(AuthoritiesConstants.USER),
                new HashSet<>()
        );

        restUserMockMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(u)))
                .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByEmail("funky@example.com");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterInvalidEmail() throws Exception {
        ManagedUserVM u = new ManagedUserVM(
                null,                   // id
                "bob",                  // login
                "password",             // password
                "Bob",                  // firstName
                "Green",                // lastName
                "invalid",              // e-mail <-- invalid
                true,                   // activated
                "en",                   // langKey
                null,                   // createdBy
                null,                   // createdDate
                null,                   // lastModifiedBy
                null,                    // lastModifiedDate
                Sets.newSet(AuthoritiesConstants.USER),
                new HashSet<>()
        );

        restUserMockMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(u)))
                .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterDuplicateLogin() throws Exception {
        // Good
        ManagedUserVM u = new ManagedUserVM(
                null,                   // id
                "alice",                // login
                "password",             // password
                "Alice",                // firstName
                "Something",            // lastName
                "alice@example.com",    // e-mail
                true,                   // activated
                "en",                   // langKey
                null,                   // createdBy
                null,                   // createdDate
                null,                   // lastModifiedBy
                null,                    // lastModifiedDate
                Sets.newSet(AuthoritiesConstants.USER),
                new HashSet<>()
        );

        // Duplicate login, diff e-mail
        ManagedUserVM dup = new ManagedUserVM(
                null,                   // id
                u.getLogin(),                // login
                u.getPassword(),             // password
                u.getLogin(),                // firstName
                u.getLastName(),            // lastName
                "alicerrr@example.com", // e-mail <-- different email
                u.isActivated(),                   // activated
                u.getLangKey(),                   // langKey
                null,                   // createdBy
                null,                   // createdDate
                null,                   // lastModifiedBy
                null,                    // lastModifiedDate
                u.getAuthorities(),
                u.getNamespaces()
        );

        // Good
        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(u)))
                .andExpect(status().isCreated());

        // Duplicate
        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(dup)))
                .andExpect(status().is4xxClientError());

        Optional<User> userDup = userRepository.findOneByEmail("alicerrr@example.com");
        assertThat(userDup.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterDuplicateEmail() throws Exception {
        // Good
        ManagedUserVM u = new ManagedUserVM(
                null,                   // id
                "john",                 // login
                "johnnyjohn",           // password
                "John",                 // firstName
                "Doe",                  // lastName
                "john@doe.com",         // e-mail
                true,                   // activated
                "en",                   // langKey
                null,                   // createdBy
                null,                   // createdDate
                null,                   // lastModifiedBy
                null,                    // lastModifiedDate
                Sets.newSet(AuthoritiesConstants.USER),
                new HashSet<>()
        );

        // Duplicate e-mail, diff login
        ManagedUserVM dup = new ManagedUserVM(
                null,                   // id
                "johnny",               // login <-- different login
                u.getPassword(),        // password
                u.getLogin(),           // firstName
                u.getLastName(),        // lastName
                u.getEmail(),           // email
                u.isActivated(),        // activated
                u.getLangKey(),         // langKey
                null,                   // createdBy
                null,                   // createdDate
                null,                   // lastModifiedBy
                null,                   // lastModifiedDate
                u.getAuthorities(),     // authorities
                u.getNamespaces()       // namespaces
        );

        // Good
        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(u)))
                .andExpect(status().isCreated());

        // Duplicate e-mail
        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(dup)))
                .andExpect(status().is4xxClientError());

        Optional<User> userDup = userRepository.findOneByLogin("johnny");
        assertThat(userDup.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterAdminIsIgnored() throws Exception {
        // Good
        ManagedUserVM u = new ManagedUserVM(
                null,                   // id
                "badguy",               // login
                "johnnyjohn",           // password
                "John",                 // firstName
                "Doe",                  // lastName
                "john@doe.com",         // e-mail
                true,                   // activated
                "en",                   // langKey
                null,                   // createdBy
                null,                   // createdDate
                null,                   // lastModifiedBy
                null,                    // lastModifiedDate
                Sets.newSet(AuthoritiesConstants.ADMIN),// <-- only admin should be able to do that
                new HashSet<>()
        );

        restMvc.perform(
                post("/api/register")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(u)))
                .andExpect(status().isCreated());

        Optional<User> user = userRepository.findOneByLogin("badguy");
        assertThat(user.isPresent()).isTrue();
        assertThat(user.get().getAuthorities()).hasSize(1)
                .containsExactly(authorityRepository.findOne(AuthoritiesConstants.USER));
    }
}
