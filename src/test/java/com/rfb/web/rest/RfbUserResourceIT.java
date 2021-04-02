package com.rfb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.rfb.IntegrationTest;
import com.rfb.domain.RfbUser;
import com.rfb.repository.RfbUserRepository;
import com.rfb.service.EntityManager;
import com.rfb.service.dto.RfbUserDTO;
import com.rfb.service.mapper.RfbUserMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RfbUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RfbUserResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rfb-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RfbUserRepository rfbUserRepository;

    @Autowired
    private RfbUserMapper rfbUserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RfbUser rfbUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbUser createEntity(EntityManager em) {
        RfbUser rfbUser = new RfbUser().username(DEFAULT_USERNAME);
        return rfbUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbUser createUpdatedEntity(EntityManager em) {
        RfbUser rfbUser = new RfbUser().username(UPDATED_USERNAME);
        return rfbUser;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RfbUser.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        rfbUser = createEntity(em);
    }

    @Test
    void createRfbUser() throws Exception {
        int databaseSizeBeforeCreate = rfbUserRepository.findAll().collectList().block().size();
        // Create the RfbUser
        RfbUserDTO rfbUserDTO = rfbUserMapper.toDto(rfbUser);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbUserDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeCreate + 1);
        RfbUser testRfbUser = rfbUserList.get(rfbUserList.size() - 1);
        assertThat(testRfbUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
    }

    @Test
    void createRfbUserWithExistingId() throws Exception {
        // Create the RfbUser with an existing ID
        rfbUser.setId(1L);
        RfbUserDTO rfbUserDTO = rfbUserMapper.toDto(rfbUser);

        int databaseSizeBeforeCreate = rfbUserRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRfbUsersAsStream() {
        // Initialize the database
        rfbUserRepository.save(rfbUser).block();

        List<RfbUser> rfbUserList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(RfbUserDTO.class)
            .getResponseBody()
            .map(rfbUserMapper::toEntity)
            .filter(rfbUser::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(rfbUserList).isNotNull();
        assertThat(rfbUserList).hasSize(1);
        RfbUser testRfbUser = rfbUserList.get(0);
        assertThat(testRfbUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
    }

    @Test
    void getAllRfbUsers() {
        // Initialize the database
        rfbUserRepository.save(rfbUser).block();

        // Get all the rfbUserList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(rfbUser.getId().intValue()))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME));
    }

    @Test
    void getRfbUser() {
        // Initialize the database
        rfbUserRepository.save(rfbUser).block();

        // Get the rfbUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rfbUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rfbUser.getId().intValue()))
            .jsonPath("$.username")
            .value(is(DEFAULT_USERNAME));
    }

    @Test
    void getNonExistingRfbUser() {
        // Get the rfbUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRfbUser() throws Exception {
        // Initialize the database
        rfbUserRepository.save(rfbUser).block();

        int databaseSizeBeforeUpdate = rfbUserRepository.findAll().collectList().block().size();

        // Update the rfbUser
        RfbUser updatedRfbUser = rfbUserRepository.findById(rfbUser.getId()).block();
        updatedRfbUser.username(UPDATED_USERNAME);
        RfbUserDTO rfbUserDTO = rfbUserMapper.toDto(updatedRfbUser);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rfbUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbUserDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeUpdate);
        RfbUser testRfbUser = rfbUserList.get(rfbUserList.size() - 1);
        assertThat(testRfbUser.getUsername()).isEqualTo(UPDATED_USERNAME);
    }

    @Test
    void putNonExistingRfbUser() throws Exception {
        int databaseSizeBeforeUpdate = rfbUserRepository.findAll().collectList().block().size();
        rfbUser.setId(count.incrementAndGet());

        // Create the RfbUser
        RfbUserDTO rfbUserDTO = rfbUserMapper.toDto(rfbUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rfbUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRfbUser() throws Exception {
        int databaseSizeBeforeUpdate = rfbUserRepository.findAll().collectList().block().size();
        rfbUser.setId(count.incrementAndGet());

        // Create the RfbUser
        RfbUserDTO rfbUserDTO = rfbUserMapper.toDto(rfbUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRfbUser() throws Exception {
        int databaseSizeBeforeUpdate = rfbUserRepository.findAll().collectList().block().size();
        rfbUser.setId(count.incrementAndGet());

        // Create the RfbUser
        RfbUserDTO rfbUserDTO = rfbUserMapper.toDto(rfbUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRfbUserWithPatch() throws Exception {
        // Initialize the database
        rfbUserRepository.save(rfbUser).block();

        int databaseSizeBeforeUpdate = rfbUserRepository.findAll().collectList().block().size();

        // Update the rfbUser using partial update
        RfbUser partialUpdatedRfbUser = new RfbUser();
        partialUpdatedRfbUser.setId(rfbUser.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRfbUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeUpdate);
        RfbUser testRfbUser = rfbUserList.get(rfbUserList.size() - 1);
        assertThat(testRfbUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
    }

    @Test
    void fullUpdateRfbUserWithPatch() throws Exception {
        // Initialize the database
        rfbUserRepository.save(rfbUser).block();

        int databaseSizeBeforeUpdate = rfbUserRepository.findAll().collectList().block().size();

        // Update the rfbUser using partial update
        RfbUser partialUpdatedRfbUser = new RfbUser();
        partialUpdatedRfbUser.setId(rfbUser.getId());

        partialUpdatedRfbUser.username(UPDATED_USERNAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRfbUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeUpdate);
        RfbUser testRfbUser = rfbUserList.get(rfbUserList.size() - 1);
        assertThat(testRfbUser.getUsername()).isEqualTo(UPDATED_USERNAME);
    }

    @Test
    void patchNonExistingRfbUser() throws Exception {
        int databaseSizeBeforeUpdate = rfbUserRepository.findAll().collectList().block().size();
        rfbUser.setId(count.incrementAndGet());

        // Create the RfbUser
        RfbUserDTO rfbUserDTO = rfbUserMapper.toDto(rfbUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rfbUserDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRfbUser() throws Exception {
        int databaseSizeBeforeUpdate = rfbUserRepository.findAll().collectList().block().size();
        rfbUser.setId(count.incrementAndGet());

        // Create the RfbUser
        RfbUserDTO rfbUserDTO = rfbUserMapper.toDto(rfbUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRfbUser() throws Exception {
        int databaseSizeBeforeUpdate = rfbUserRepository.findAll().collectList().block().size();
        rfbUser.setId(count.incrementAndGet());

        // Create the RfbUser
        RfbUserDTO rfbUserDTO = rfbUserMapper.toDto(rfbUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RfbUser in the database
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRfbUser() {
        // Initialize the database
        rfbUserRepository.save(rfbUser).block();

        int databaseSizeBeforeDelete = rfbUserRepository.findAll().collectList().block().size();

        // Delete the rfbUser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rfbUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RfbUser> rfbUserList = rfbUserRepository.findAll().collectList().block();
        assertThat(rfbUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
