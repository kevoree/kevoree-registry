package org.kevoree.registry.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kevoree.registry.Application;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.AuthorityRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.service.NamespaceService;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.service.dto.NamedDTO;
import org.kevoree.registry.service.mapper.NamespaceMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the NamespaceResource REST controller.
 *
 * @see NamespaceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class NamespaceResourceTest {

    private static final String DEFAULT_NAME = "testnamespace";

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private NamespaceService namespaceService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private TypeDefinitionRepository typeDefinitionRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private NamespaceMapper namespaceMapper;

    private MockMvc restNamespaceMockMvc;

    private Namespace namespace;
    private User user;

    @Before
    public void setup() {
        NamespaceResource namespaceResource = new NamespaceResource();
        ReflectionTestUtils.setField(namespaceResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(namespaceResource, "userService", userService);
        ReflectionTestUtils.setField(namespaceResource, "namespaceRepository", namespaceRepository);
        ReflectionTestUtils.setField(namespaceResource, "namespaceService", namespaceService);
        ReflectionTestUtils.setField(namespaceResource, "authorityRepository", authorityRepository);
        ReflectionTestUtils.setField(namespaceResource, "namespaceMapper", namespaceMapper);
        this.restNamespaceMockMvc = MockMvcBuilders.standaloneSetup(namespaceResource)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Before
    public void initTest() {
        user = userRepository.findOneByLogin("user").get();

        namespace = new Namespace();
        namespace.setName(DEFAULT_NAME);
        namespace.addMember(user);
        namespace.setOwner(user);
        namespaceRepository.saveAndFlush(namespace);
    }

    @Test
    @Transactional
    public void testGetExistingNamespaces() throws Exception {
        restNamespaceMockMvc.perform(get("/api/namespaces")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(namespaceRepository.findAll().size())));
    }

    @Test
    @Transactional
    public void testGetExistingNamespace() throws Exception {
        restNamespaceMockMvc.perform(get("/api/namespaces/{name}", DEFAULT_NAME)
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.members.[0].login").value(user.getLogin()));
    }

    @Test
    @Transactional
    public void testCreateNamespace() throws Exception {
        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user.getLogin(), AuthoritiesConstants.USER));

        restNamespaceMockMvc.perform(post("/api/namespaces")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new NamedDTO("newnamespace"))))
            .andExpect(status().isCreated());

        // Validate the namespace in db
        Namespace dbNs = namespaceRepository.findOne("newnamespace");
        assertThat(dbNs.getMembers()).contains(user);
        assertThat(dbNs.getOwner()).isEqualTo(user);
        // Validate the user's namespaces in db
        User dbUser = userRepository.findOneByLogin(user.getLogin()).get();
        assertThat(dbUser.getNamespaces()).contains(dbNs);
    }

    @Test
    @Transactional
    public void testAddMemberNamespace() throws Exception {
        // create a random user
        User u = new User();
        u.setLogin("foo");
        u.setPassword("foopassword");
        u.setAuthorities(Collections.singleton(authorityRepository.findOne(AuthoritiesConstants.USER)));
        u = userRepository.saveAndFlush(u);

        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user.getLogin(), AuthoritiesConstants.USER));

        restNamespaceMockMvc.perform(post("/api/namespaces/{name}/members", DEFAULT_NAME)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new NamedDTO("foo"))))
            .andExpect(status().isOk());

        // Validate the namespace in db
        Namespace dbNs = namespaceRepository.findOne(DEFAULT_NAME);
        User fooUser = userRepository.findOneByLogin(u.getLogin()).get();
        assertThat(dbNs.getMembers()).containsOnly(user, fooUser);
        assertThat(dbNs.getOwner()).isEqualTo(user);
        // Validate the user in db
        assertThat(fooUser.getNamespaces()).contains(namespace);
    }

    @Test
    @Transactional
    public void testAddNsAndTdefAndDeleteNs() throws Exception {
        // add a TypeDefinition to the Namespace
        TypeDefinition tdef = new TypeDefinition();
        tdef.setName("TestComp");
        tdef.setVersion(1L);
        tdef.setNamespace(namespace);
        tdef.setModel("{}");
        namespace.addTypeDefinition(tdef);
        typeDefinitionRepository.saveAndFlush(tdef);

        // Add the owner to the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user.getLogin(), AuthoritiesConstants.USER));

        restNamespaceMockMvc.perform(delete("/api/namespaces/{name}", DEFAULT_NAME)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // validate db
        assertThat(userRepository.findOneByLogin(user.getLogin()).isPresent()).isTrue();
        assertThat(namespaceRepository.findOne(namespace.getName())).isNull();
        assertThat(typeDefinitionRepository.findOneByNamespaceNameAndNameAndVersion(namespace.getName(), tdef.getName(), tdef.getVersion()).isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testUnauthorizedDelete() throws Exception {
        // Authenticate "user"
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getLogin(), AuthoritiesConstants.USER));

        // try to delete non-owned namespace "kevoree"
        restNamespaceMockMvc.perform(delete("/api/namespaces/{name}", "kevoree")
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                // expect it to fail
                .andExpect(status().isUnauthorized());

        // validate in db that namespace is still here
        assertThat(namespaceRepository.findOne("kevoree")).isNotNull();
    }
}
