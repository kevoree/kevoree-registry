package org.kevoree.registry.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kevoree.registry.Application;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.web.rest.dto.TypeDefinitionDTO;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the TypeDefinitionResource REST controller.
 *
 * @see TypeDefinitionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TypeDefinitionResourceTest {

    private static final String NS_NAME = "testnamespace";

    @Inject
    private TypeDefinitionRepository tdefsRepository;

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private UserRepository userRepository;

    private MockMvc restTdefsMockMvc;

    private TypeDefinitionDTO tdef;
    private Namespace namespace;
    private User user;

    @Before
    public void setup() {
        TypeDefinitionResource tdefResource = new TypeDefinitionResource();
        ReflectionTestUtils.setField(tdefResource, "tdefsRepository", tdefsRepository);
        ReflectionTestUtils.setField(tdefResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(tdefResource, "namespaceRepository", namespaceRepository);
        this.restTdefsMockMvc = MockMvcBuilders.standaloneSetup(tdefResource).build();
    }

    @Before
    public void initTest() {
        user = userRepository.findOneByLogin("user").get();

        namespace = new Namespace();
        namespace.setName(NS_NAME);
        namespace.addMember(user);
        namespace.setOwner(user);

        user.addNamespace(namespace);
        namespaceRepository.saveAndFlush(namespace);
        userRepository.saveAndFlush(user);

        tdef = new TypeDefinitionDTO();
        tdef.setName("TestComp");
        tdef.setVersion("1.2.3");
        tdef.setModel("{}");
    }

    @Test
    @Transactional
    public void testCreateTypeDefinition() throws Exception {
        // add "user" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("user", null, AuthoritiesConstants.USER));

        restTdefsMockMvc.perform(post("/api/namespaces/{namespace}/tdefs", namespace.getName())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tdef)))
            .andExpect(status().isCreated());

        // Validate the namespace in db
        TypeDefinition dbTdef = tdefsRepository.findOneByNamespaceNameAndNameAndVersion(
            namespace.getName(), tdef.getName(), tdef.getVersion()).get();
        assertThat(dbTdef.getNamespace().getName()).isEqualTo(namespace.getName());
        Namespace dbNs = namespaceRepository.findOneByNameAndMemberName(namespace.getName(), user.getLogin()).get();
        assertThat(dbNs.getTypeDefinitions()).containsExactly(dbTdef);
    }

    @Test
    @Transactional
    public void testCreateAlreadyExistingTypeDefinition() throws Exception {
        // add "user" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("user", null, AuthoritiesConstants.USER));

        restTdefsMockMvc.perform(post("/api/namespaces/{namespaces}/tdefs", namespace.getName())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tdef)))
            .andExpect(status().isCreated());

        // Validate the namespace in db
        TypeDefinition dbTdef = tdefsRepository.findOneByNamespaceNameAndNameAndVersion(
            namespace.getName(), tdef.getName(), tdef.getVersion()).get();
        assertThat(dbTdef.getNamespace().getName()).isEqualTo(namespace.getName());
        Namespace dbNs = namespaceRepository.findOneByNameAndMemberName(namespace.getName(), user.getLogin()).get();
        assertThat(dbNs.getTypeDefinitions()).containsExactly(dbTdef);

        restTdefsMockMvc.perform(post("/api/namespaces/{namespaces}/tdefs", namespace.getName())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tdef)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testCreateTypeDefinitionWithExistingNameAndVersionButInDifferentNamespace() throws Exception {
        // add "user" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("user", null, AuthoritiesConstants.USER));

        // add default tdef to db
        TypeDefinition tdef = new TypeDefinition();
        tdef.setName(this.tdef.getName());
        tdef.setVersion(this.tdef.getVersion());
        tdef.setModel(this.tdef.getModel());
        tdef.setNamespace(namespace);
        namespace.addTypeDefinition(tdef);
        namespaceRepository.saveAndFlush(namespace);
        tdefsRepository.saveAndFlush(tdef);

        // create a new namespace
        Namespace newNs = new Namespace();
        newNs.setName("anothernamespace");
        newNs.setOwner(user);
        newNs.addMember(user);
        namespaceRepository.saveAndFlush(newNs);

        // add namespace to user
        user.addNamespace(newNs);
        userRepository.saveAndFlush(user);

        // validate db
        Namespace dbNewNs = namespaceRepository.findOne(newNs.getName());
        assertThat(dbNewNs).isNotNull();

        // create a new TypeDefinition with same name and version but using the newNs
        restTdefsMockMvc.perform(post("/api/namespaces/{namespaces}/tdefs", newNs.getName())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tdef)))
            .andExpect(status().isCreated());

        // Validate the namespace in db
        TypeDefinition dbTdef = tdefsRepository.findOneByNamespaceNameAndNameAndVersion(
            newNs.getName(), tdef.getName(), tdef.getVersion()).get();
        assertThat(dbTdef.getNamespace().getName()).isEqualTo(newNs.getName());
        Namespace dbNs = namespaceRepository.findOneByNameAndMemberName(newNs.getName(), user.getLogin()).get();
        assertThat(dbNs.getTypeDefinitions()).containsExactly(dbTdef);
    }
}
