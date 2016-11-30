package org.kevoree.registry.web.rest;

import org.joda.time.DateTime;
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
import org.kevoree.registry.service.UserService;
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
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private UserService userService;

    private MockMvc restTdefsMockMvc;

    private TypeDefinitionDTO tdef;
    private Namespace namespace;
    private User user;

    @Before
    public void setup() {
        TypeDefinitionResource tdefResource = new TypeDefinitionResource();
        ReflectionTestUtils.setField(tdefResource, "tdefsRepository", tdefsRepository);
        ReflectionTestUtils.setField(tdefResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(tdefResource, "userService", userService);
        ReflectionTestUtils.setField(tdefResource, "authorityRepository", authorityRepository);
        ReflectionTestUtils.setField(tdefResource, "namespaceRepository", namespaceRepository);
        this.restTdefsMockMvc = MockMvcBuilders.standaloneSetup(tdefResource).build();
    }

    @Before
    public void initTest() throws ParseException {
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
        tdef.setVersion(1L);
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
    public void testDeleteTypeDefinitionByNamespaceAndNameAndVersion() throws Exception {
        // Initialize the database
        TypeDefinition tdef = new TypeDefinition();
        tdef.setName(this.tdef.getName());
        tdef.setVersion(this.tdef.getVersion());
        tdef.setModel(this.tdef.getModel());
        tdef.setCreatedBy("user");
        tdef.setNamespace(this.namespace);
        namespace.addTypeDefinition(tdef);
        namespaceRepository.saveAndFlush(this.namespace);
        tdefsRepository.saveAndFlush(tdef);

        // retrieve db size for tdefs
        int databaseSizeBeforeUpdate = tdefsRepository.findAll().size();

        // add "user" as current user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken("user", null, AuthoritiesConstants.USER));

        restTdefsMockMvc.perform(delete("/api/namespaces/{namespaces}/tdefs/{name}/{version}",
            namespace.getName(), tdef.getName(), tdef.getVersion())
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate db
        List<TypeDefinition> tdefs = tdefsRepository.findAll();
        assertThat(tdefs).hasSize(databaseSizeBeforeUpdate - 1);
        Optional<TypeDefinition> deletedTdef = tdefsRepository.findOneByNamespaceNameAndNameAndVersion(
            namespace.getName(), tdef.getName(), tdef.getVersion());
        assertThat(deletedTdef.isPresent()).isFalse();
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
        tdef.setCreatedBy("user");
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

        // add user to namespace
        user.addNamespace(newNs);
        userRepository.saveAndFlush(user);

        // validate db
        Namespace dbNewNs = namespaceRepository.findOne(newNs.getName());
        assertThat(dbNewNs).isNotNull();
        assertThat(dbNewNs.getOwner()).isEqualTo(user);

        // create same name/version but different namespace
        TypeDefinitionDTO newTdef = new TypeDefinitionDTO();
        newTdef.setName(this.tdef.getName());
        newTdef.setVersion(this.tdef.getVersion());
        newTdef.setModel("{}");

        // create a new TypeDefinition with same name and version but using the newNs
        restTdefsMockMvc.perform(post("/api/namespaces/{namespaces}/tdefs", newNs.getName())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(newTdef)))
            .andExpect(status().isCreated());

        // validate the tdef in db
        TypeDefinition dbTdef = tdefsRepository.findOneByNamespaceNameAndNameAndVersion(
            newNs.getName(), tdef.getName(), tdef.getVersion()).get();
        assertThat(dbTdef.getNamespace().getName()).isEqualTo(newNs.getName());
        assertThat(dbTdef.getCreatedBy()).isEqualTo("user");
        assertThat(dbTdef.getCreatedDate().getMillis()).isLessThan(DateTime.now().getMillis());
        // validate the namespace in db
        Namespace dbNs = namespaceRepository.findOneByNameAndMemberName(newNs.getName(), user.getLogin()).get();
        assertThat(dbNs.getTypeDefinitions()).containsExactly(dbTdef);
    }
}
