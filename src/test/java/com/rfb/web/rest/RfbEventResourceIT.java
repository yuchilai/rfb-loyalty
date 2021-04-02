package com.rfb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.rfb.IntegrationTest;
import com.rfb.domain.RfbEvent;
import com.rfb.repository.RfbEventRepository;
import com.rfb.service.EntityManager;
import com.rfb.service.dto.RfbEventDTO;
import com.rfb.service.mapper.RfbEventMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link RfbEventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RfbEventResourceIT {

    private static final LocalDate DEFAULT_EVENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EVENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_EVENT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rfb-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RfbEventRepository rfbEventRepository;

    @Autowired
    private RfbEventMapper rfbEventMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RfbEvent rfbEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbEvent createEntity(EntityManager em) {
        RfbEvent rfbEvent = new RfbEvent().eventDate(DEFAULT_EVENT_DATE).eventCode(DEFAULT_EVENT_CODE);
        return rfbEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbEvent createUpdatedEntity(EntityManager em) {
        RfbEvent rfbEvent = new RfbEvent().eventDate(UPDATED_EVENT_DATE).eventCode(UPDATED_EVENT_CODE);
        return rfbEvent;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RfbEvent.class).block();
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
        rfbEvent = createEntity(em);
    }

    @Test
    void createRfbEvent() throws Exception {
        int databaseSizeBeforeCreate = rfbEventRepository.findAll().collectList().block().size();
        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeCreate + 1);
        RfbEvent testRfbEvent = rfbEventList.get(rfbEventList.size() - 1);
        assertThat(testRfbEvent.getEventDate()).isEqualTo(DEFAULT_EVENT_DATE);
        assertThat(testRfbEvent.getEventCode()).isEqualTo(DEFAULT_EVENT_CODE);
    }

    @Test
    void createRfbEventWithExistingId() throws Exception {
        // Create the RfbEvent with an existing ID
        rfbEvent.setId(1L);
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        int databaseSizeBeforeCreate = rfbEventRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRfbEvents() {
        // Initialize the database
        rfbEventRepository.save(rfbEvent).block();

        // Get all the rfbEventList
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
            .value(hasItem(rfbEvent.getId().intValue()))
            .jsonPath("$.[*].eventDate")
            .value(hasItem(DEFAULT_EVENT_DATE.toString()))
            .jsonPath("$.[*].eventCode")
            .value(hasItem(DEFAULT_EVENT_CODE));
    }

    @Test
    void getRfbEvent() {
        // Initialize the database
        rfbEventRepository.save(rfbEvent).block();

        // Get the rfbEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rfbEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rfbEvent.getId().intValue()))
            .jsonPath("$.eventDate")
            .value(is(DEFAULT_EVENT_DATE.toString()))
            .jsonPath("$.eventCode")
            .value(is(DEFAULT_EVENT_CODE));
    }

    @Test
    void getNonExistingRfbEvent() {
        // Get the rfbEvent
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRfbEvent() throws Exception {
        // Initialize the database
        rfbEventRepository.save(rfbEvent).block();

        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().collectList().block().size();

        // Update the rfbEvent
        RfbEvent updatedRfbEvent = rfbEventRepository.findById(rfbEvent.getId()).block();
        updatedRfbEvent.eventDate(UPDATED_EVENT_DATE).eventCode(UPDATED_EVENT_CODE);
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(updatedRfbEvent);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rfbEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
        RfbEvent testRfbEvent = rfbEventList.get(rfbEventList.size() - 1);
        assertThat(testRfbEvent.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        assertThat(testRfbEvent.getEventCode()).isEqualTo(UPDATED_EVENT_CODE);
    }

    @Test
    void putNonExistingRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().collectList().block().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rfbEventDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().collectList().block().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().collectList().block().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRfbEventWithPatch() throws Exception {
        // Initialize the database
        rfbEventRepository.save(rfbEvent).block();

        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().collectList().block().size();

        // Update the rfbEvent using partial update
        RfbEvent partialUpdatedRfbEvent = new RfbEvent();
        partialUpdatedRfbEvent.setId(rfbEvent.getId());

        partialUpdatedRfbEvent.eventCode(UPDATED_EVENT_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRfbEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
        RfbEvent testRfbEvent = rfbEventList.get(rfbEventList.size() - 1);
        assertThat(testRfbEvent.getEventDate()).isEqualTo(DEFAULT_EVENT_DATE);
        assertThat(testRfbEvent.getEventCode()).isEqualTo(UPDATED_EVENT_CODE);
    }

    @Test
    void fullUpdateRfbEventWithPatch() throws Exception {
        // Initialize the database
        rfbEventRepository.save(rfbEvent).block();

        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().collectList().block().size();

        // Update the rfbEvent using partial update
        RfbEvent partialUpdatedRfbEvent = new RfbEvent();
        partialUpdatedRfbEvent.setId(rfbEvent.getId());

        partialUpdatedRfbEvent.eventDate(UPDATED_EVENT_DATE).eventCode(UPDATED_EVENT_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRfbEvent.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbEvent))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
        RfbEvent testRfbEvent = rfbEventList.get(rfbEventList.size() - 1);
        assertThat(testRfbEvent.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        assertThat(testRfbEvent.getEventCode()).isEqualTo(UPDATED_EVENT_CODE);
    }

    @Test
    void patchNonExistingRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().collectList().block().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rfbEventDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().collectList().block().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRfbEvent() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventRepository.findAll().collectList().block().size();
        rfbEvent.setId(count.incrementAndGet());

        // Create the RfbEvent
        RfbEventDTO rfbEventDTO = rfbEventMapper.toDto(rfbEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RfbEvent in the database
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRfbEvent() {
        // Initialize the database
        rfbEventRepository.save(rfbEvent).block();

        int databaseSizeBeforeDelete = rfbEventRepository.findAll().collectList().block().size();

        // Delete the rfbEvent
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rfbEvent.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RfbEvent> rfbEventList = rfbEventRepository.findAll().collectList().block();
        assertThat(rfbEventList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
