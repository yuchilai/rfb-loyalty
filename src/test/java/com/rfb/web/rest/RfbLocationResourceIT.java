package com.rfb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.rfb.IntegrationTest;
import com.rfb.domain.RfbLocation;
import com.rfb.repository.RfbLocationRepository;
import com.rfb.service.EntityManager;
import com.rfb.service.dto.RfbLocationDTO;
import com.rfb.service.mapper.RfbLocationMapper;
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
 * Integration tests for the {@link RfbLocationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RfbLocationResourceIT {

    private static final String DEFAULT_LOCATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_RUN_DAY_OF_WEEK = 1;
    private static final Integer UPDATED_RUN_DAY_OF_WEEK = 2;

    private static final String ENTITY_API_URL = "/api/rfb-locations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RfbLocationRepository rfbLocationRepository;

    @Autowired
    private RfbLocationMapper rfbLocationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RfbLocation rfbLocation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbLocation createEntity(EntityManager em) {
        RfbLocation rfbLocation = new RfbLocation().locationName(DEFAULT_LOCATION_NAME).runDayOfWeek(DEFAULT_RUN_DAY_OF_WEEK);
        return rfbLocation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbLocation createUpdatedEntity(EntityManager em) {
        RfbLocation rfbLocation = new RfbLocation().locationName(UPDATED_LOCATION_NAME).runDayOfWeek(UPDATED_RUN_DAY_OF_WEEK);
        return rfbLocation;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RfbLocation.class).block();
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
        rfbLocation = createEntity(em);
    }

    @Test
    void createRfbLocation() throws Exception {
        int databaseSizeBeforeCreate = rfbLocationRepository.findAll().collectList().block().size();
        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeCreate + 1);
        RfbLocation testRfbLocation = rfbLocationList.get(rfbLocationList.size() - 1);
        assertThat(testRfbLocation.getLocationName()).isEqualTo(DEFAULT_LOCATION_NAME);
        assertThat(testRfbLocation.getRunDayOfWeek()).isEqualTo(DEFAULT_RUN_DAY_OF_WEEK);
    }

    @Test
    void createRfbLocationWithExistingId() throws Exception {
        // Create the RfbLocation with an existing ID
        rfbLocation.setId(1L);
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        int databaseSizeBeforeCreate = rfbLocationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRfbLocations() {
        // Initialize the database
        rfbLocationRepository.save(rfbLocation).block();

        // Get all the rfbLocationList
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
            .value(hasItem(rfbLocation.getId().intValue()))
            .jsonPath("$.[*].locationName")
            .value(hasItem(DEFAULT_LOCATION_NAME))
            .jsonPath("$.[*].runDayOfWeek")
            .value(hasItem(DEFAULT_RUN_DAY_OF_WEEK));
    }

    @Test
    void getRfbLocation() {
        // Initialize the database
        rfbLocationRepository.save(rfbLocation).block();

        // Get the rfbLocation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rfbLocation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rfbLocation.getId().intValue()))
            .jsonPath("$.locationName")
            .value(is(DEFAULT_LOCATION_NAME))
            .jsonPath("$.runDayOfWeek")
            .value(is(DEFAULT_RUN_DAY_OF_WEEK));
    }

    @Test
    void getNonExistingRfbLocation() {
        // Get the rfbLocation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRfbLocation() throws Exception {
        // Initialize the database
        rfbLocationRepository.save(rfbLocation).block();

        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().collectList().block().size();

        // Update the rfbLocation
        RfbLocation updatedRfbLocation = rfbLocationRepository.findById(rfbLocation.getId()).block();
        updatedRfbLocation.locationName(UPDATED_LOCATION_NAME).runDayOfWeek(UPDATED_RUN_DAY_OF_WEEK);
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(updatedRfbLocation);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rfbLocationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
        RfbLocation testRfbLocation = rfbLocationList.get(rfbLocationList.size() - 1);
        assertThat(testRfbLocation.getLocationName()).isEqualTo(UPDATED_LOCATION_NAME);
        assertThat(testRfbLocation.getRunDayOfWeek()).isEqualTo(UPDATED_RUN_DAY_OF_WEEK);
    }

    @Test
    void putNonExistingRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().collectList().block().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rfbLocationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().collectList().block().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().collectList().block().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRfbLocationWithPatch() throws Exception {
        // Initialize the database
        rfbLocationRepository.save(rfbLocation).block();

        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().collectList().block().size();

        // Update the rfbLocation using partial update
        RfbLocation partialUpdatedRfbLocation = new RfbLocation();
        partialUpdatedRfbLocation.setId(rfbLocation.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRfbLocation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbLocation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
        RfbLocation testRfbLocation = rfbLocationList.get(rfbLocationList.size() - 1);
        assertThat(testRfbLocation.getLocationName()).isEqualTo(DEFAULT_LOCATION_NAME);
        assertThat(testRfbLocation.getRunDayOfWeek()).isEqualTo(DEFAULT_RUN_DAY_OF_WEEK);
    }

    @Test
    void fullUpdateRfbLocationWithPatch() throws Exception {
        // Initialize the database
        rfbLocationRepository.save(rfbLocation).block();

        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().collectList().block().size();

        // Update the rfbLocation using partial update
        RfbLocation partialUpdatedRfbLocation = new RfbLocation();
        partialUpdatedRfbLocation.setId(rfbLocation.getId());

        partialUpdatedRfbLocation.locationName(UPDATED_LOCATION_NAME).runDayOfWeek(UPDATED_RUN_DAY_OF_WEEK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRfbLocation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbLocation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
        RfbLocation testRfbLocation = rfbLocationList.get(rfbLocationList.size() - 1);
        assertThat(testRfbLocation.getLocationName()).isEqualTo(UPDATED_LOCATION_NAME);
        assertThat(testRfbLocation.getRunDayOfWeek()).isEqualTo(UPDATED_RUN_DAY_OF_WEEK);
    }

    @Test
    void patchNonExistingRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().collectList().block().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rfbLocationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().collectList().block().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRfbLocation() throws Exception {
        int databaseSizeBeforeUpdate = rfbLocationRepository.findAll().collectList().block().size();
        rfbLocation.setId(count.incrementAndGet());

        // Create the RfbLocation
        RfbLocationDTO rfbLocationDTO = rfbLocationMapper.toDto(rfbLocation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbLocationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RfbLocation in the database
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRfbLocation() {
        // Initialize the database
        rfbLocationRepository.save(rfbLocation).block();

        int databaseSizeBeforeDelete = rfbLocationRepository.findAll().collectList().block().size();

        // Delete the rfbLocation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rfbLocation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RfbLocation> rfbLocationList = rfbLocationRepository.findAll().collectList().block();
        assertThat(rfbLocationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
