package org.kevoree.registry.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kevoree.registry.Application;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.service.NamespaceService;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
* Test class for the NamespaceResource REST controller.
*
* @see NamespaceResource
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class NamespaceResourceTest {

    private static final String DEFAULT_FQN = "org.kevoree";

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private NamespaceService namespaceService;

    private MockMvc restNamespaceMockMvc;

    private Namespace namespace;
    private User nsOwner;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NamespaceResource namespaceResource = new NamespaceResource();
        ReflectionTestUtils.setField(namespaceResource, "namespaceRepository", namespaceRepository);
        ReflectionTestUtils.setField(namespaceResource, "namespaceService", namespaceService);
        this.restNamespaceMockMvc = MockMvcBuilders.standaloneSetup(namespaceResource).build();
    }

    @Before
    public void initTest() {
        nsOwner = new User();
        nsOwner.setLogin("nsowner");
        nsOwner.setPassword("password");
        nsOwner.setCreatedBy("test");
        nsOwner.setActivated(true);

        namespace = new Namespace();
        namespace.setFqn(DEFAULT_FQN);
        namespace.setOwner(nsOwner);
    }

    @Test
    @Transactional
    public void createNamespace() throws Exception {
        // Validate the database is empty
        assertThat(namespaceRepository.findAll()).hasSize(0);

        // Create the User
        userRepository.saveAndFlush(nsOwner);

        // Add the user to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(nsOwner.getLogin(), AuthoritiesConstants.ADMIN));

        // Create the Namespace
        restNamespaceMockMvc.perform(post("/api/namespaces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(namespace)))
            .andExpect(status().isCreated());

        // Validate the Namespace in the database
        List<Namespace> namespaces = namespaceRepository.findAll();
        assertThat(namespaces).hasSize(1);
        Namespace testNamespace = namespaces.iterator().next();
        assertThat(testNamespace.getFqn()).isEqualTo(DEFAULT_FQN);
        assertThat(testNamespace.getOwner()).isEqualTo(nsOwner);
        assertThat(testNamespace.getMembers().iterator().next()).isEqualTo(nsOwner);

        // Validate the owner
        User testOwner = userRepository.findOneByLogin(nsOwner.getLogin()).get();
        assertThat(testOwner.getNamespaces()).hasSize(1);
    }

    @Test
    @Transactional
    public void getAllNamespaces() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(nsOwner);
        namespaceRepository.saveAndFlush(namespace);

        // Get all the namespaces
        restNamespaceMockMvc.perform(get("/api/namespaces"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[0].fqn").value(DEFAULT_FQN))
            .andExpect(jsonPath("$.[0].owner").value(nsOwner.getLogin()));
    }

    @Test
    @Transactional
    public void getNamespace() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(nsOwner);
        namespaceRepository.saveAndFlush(namespace);

        // Get the namespace
        restNamespaceMockMvc.perform(get("/api/namespaces/{fqn}", namespace.getFqn()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fqn").value(DEFAULT_FQN))
            .andExpect(jsonPath("$.owner").value(nsOwner.getLogin()));
    }

    @Test
    @Transactional
    public void getNonExistingNamespace() throws Exception {
        // Get the namespace
        restNamespaceMockMvc.perform(get("/api/namespaces/{fqn}", "unknown"))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void addMemberToNamespace() throws Exception {
        // Validate the database is empty
        assertThat(namespaceRepository.findAll()).hasSize(0);

        // create a new user to add as a member
        User newUser = new User();
        newUser.setLogin("newuser");
        newUser.setPassword("password");
        newUser.setCreatedBy("test");
        newUser.setActivated(true);

        // Initialize the database
        userRepository.saveAndFlush(nsOwner);
        userRepository.saveAndFlush(newUser);
        namespace.addMember(nsOwner);
        namespaceRepository.saveAndFlush(namespace);

        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(nsOwner.getLogin(), AuthoritiesConstants.ADMIN));

        // add member to namespace
        restNamespaceMockMvc.perform(post("/api/namespaces/{fqn}/add_member", namespace.getFqn())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(newUser.getLogin()))
                .andExpect(status().isOk());

        // Validate the Namespace in the database
        List<Namespace> namespaces = namespaceRepository.findAll();
        assertThat(namespaces).hasSize(1);
        Namespace testNamespace = namespaces.iterator().next();
        assertThat(testNamespace.getMembers().contains(nsOwner)).isTrue();
        assertThat(testNamespace.getMembers().contains(newUser)).isTrue();

        // Validate the newUser in the database
        User newUserFromDb = userRepository.findOneByLogin(newUser.getLogin()).get();
        assertThat(newUserFromDb.getNamespaces()).hasSize(1);
        assertThat(newUserFromDb.getNamespaces().iterator().next().getFqn()).isEqualTo(namespace.getFqn());
    }

    @Test
    @Transactional
    public void removeMemberFromNamespace() throws Exception {
        // Validate the database is empty
        assertThat(namespaceRepository.findAll()).hasSize(0);

        // init new member
        User newUser = new User();
        newUser.setLogin("newuser");
        newUser.setPassword("password");
        newUser.setCreatedBy("test");
        newUser.setActivated(true);

        // save "first state" users
        userRepository.saveAndFlush(nsOwner);
        userRepository.saveAndFlush(newUser);

        // add namespace to users namespaces
        nsOwner.addNamespace(namespace);
        newUser.addNamespace(namespace);
        // add users to namespace members
        namespace.addMember(nsOwner);
        namespace.addMember(newUser);
        // save namespace in db
        namespaceRepository.saveAndFlush(namespace);
        // save "second state" users
        userRepository.saveAndFlush(nsOwner);
        userRepository.saveAndFlush(newUser);

        // Validate the db state
        Namespace testNs = namespaceRepository.findOne(namespace.getFqn());
        assertThat(testNs.getOwner()).isEqualTo(nsOwner);
        assertThat(testNs.getMembers().contains(nsOwner)).isTrue();
        assertThat(testNs.getMembers().contains(newUser)).isTrue();
        User testOwner = userRepository.findOneByLogin(nsOwner.getLogin()).get();
        assertThat(testOwner.getNamespaces()).hasSize(1);
        User testNewUser = userRepository.findOneByLogin(newUser.getLogin()).get();
        assertThat(testNewUser.getNamespaces()).hasSize(1);

        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(nsOwner.getLogin(), AuthoritiesConstants.ADMIN));

        // remove member from namespace
        restNamespaceMockMvc.perform(post("/api/namespaces/{fqn}/remove_member", namespace.getFqn())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(newUser.getLogin()))
            .andExpect(status().isOk());

        // Validate the Namespace in the database
        List<Namespace> namespaces = namespaceRepository.findAll();
        assertThat(namespaces).hasSize(1);
        Namespace testNamespace = namespaces.iterator().next();
        assertThat(testNamespace.getMembers().contains(nsOwner)).isTrue();
        assertThat(testNamespace.getMembers().contains(newUser)).isFalse();

        // Validate the newUser in the database
        User newUserFromDb = userRepository.findOneByLogin(newUser.getLogin()).get();
        assertThat(newUserFromDb.getNamespaces()).hasSize(0);
    }

    @Test
    @Transactional
    public void cantLeaveOwnNamespace() throws Exception {
        // init user
        userRepository.saveAndFlush(nsOwner);

        // init the namespace
        namespace.addMember(nsOwner);
        namespaceRepository.saveAndFlush(namespace);
        // init the owner
        nsOwner.addNamespace(namespace);
        userRepository.saveAndFlush(nsOwner);

        // validate db
        Namespace testNs = namespaceRepository.findOne(namespace.getFqn());
        assertThat(testNs.getOwner()).isEqualTo(nsOwner);
        assertThat(testNs.getMembers().contains(nsOwner)).isTrue();
        User testOwner = userRepository.findOneByLogin(nsOwner.getLogin()).get();
        assertThat(testOwner.getNamespaces()).hasSize(1);

        // try to leave my own namespace
        restNamespaceMockMvc.perform(post("/api/namespaces/{fqn}/leave", namespace.getFqn()))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void leaveNamespace() throws Exception {
        // add nsOwner to the db
        userRepository.saveAndFlush(nsOwner);

        // create a fake user
        User fakeUser = new User();
        fakeUser.setLogin("fakeuser");
        fakeUser.setPassword("password");
        fakeUser.setCreatedBy("test");
        fakeUser.setActivated(true);
        userRepository.saveAndFlush(fakeUser);

        // create a new namespace
        Namespace newNs = new Namespace();
        newNs.setFqn("new.fqn");
        newNs.setOwner(fakeUser);
        newNs.addMember(fakeUser);
        newNs.addMember(nsOwner);
        namespaceRepository.saveAndFlush(newNs);

        // add newNs to users namespaces
        fakeUser.addNamespace(newNs);
        userRepository.saveAndFlush(fakeUser);
        nsOwner.addNamespace(newNs);
        userRepository.saveAndFlush(nsOwner);

        // validate db
        Namespace testNs = namespaceRepository.findOne(newNs.getFqn());
        assertThat(testNs.getOwner()).isEqualTo(fakeUser);
        assertThat(testNs.getMembers().contains(fakeUser)).isTrue();
        assertThat(testNs.getMembers().contains(nsOwner)).isTrue();
        User testOwner = userRepository.findOneByLogin(fakeUser.getLogin()).get();
        assertThat(testOwner.getNamespaces()).hasSize(1);
        User testMember = userRepository.findOneByLogin(nsOwner.getLogin()).get();
        assertThat(testMember.getNamespaces()).hasSize(1);

        // log in as nsOwner
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(nsOwner.getLogin(), AuthoritiesConstants.ADMIN));

        // make nsOwner leave newNs namespace
        restNamespaceMockMvc.perform(post("/api/namespaces/{fqn}/leave", newNs.getFqn()))
            .andExpect(status().isOk());

        testOwner = userRepository.findOneByLogin(nsOwner.getLogin()).get();
        assertThat(testOwner.getNamespaces()).isEmpty();
        testNs = namespaceRepository.findOne(newNs.getFqn());
        assertThat(testNs.getMembers()).hasSize(1);
        assertThat(testNs.getMembers().contains(fakeUser)).isTrue();
    }

    @Test
    @Transactional
    public void deleteNamespace() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(nsOwner);
        namespaceRepository.saveAndFlush(namespace);

        // Get the namespace
        restNamespaceMockMvc.perform(delete("/api/namespaces/{fqn}", namespace.getFqn())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Namespace> namespaces = namespaceRepository.findAll();
        assertThat(namespaces).hasSize(0);

        // Validate the user has not been deleted
        User testOwner = userRepository.findOneByLogin(nsOwner.getLogin()).get();
        assertThat(testOwner).isNotNull();
    }
}
