package com.rfb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.rfb.IntegrationTest;
import com.rfb.domain.RfbEventAttendance;
import com.rfb.repository.RfbEventAttendanceRepository;
import com.rfb.service.EntityManager;
import com.rfb.service.dto.RfbEventAttendanceDTO;
import com.rfb.service.mapper.RfbEventAttendanceMapper;
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
 * Integration tests for the {@link RfbEventAttendanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RfbEventAttendanceResourceIT {

    private static final LocalDate DEFAULT_ATTENDANCE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ATTENDANCE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/rfb-event-attendances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RfbEventAttendanceRepository rfbEventAttendanceRepository;

    @Autowired
    private RfbEventAttendanceMapper rfbEventAttendanceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RfbEventAttendance rfbEventAttendance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbEventAttendance createEntity(EntityManager em) {
        RfbEventAttendance rfbEventAttendance = new RfbEventAttendance().attendanceDate(DEFAULT_ATTENDANCE_DATE);
        return rfbEventAttendance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RfbEventAttendance createUpdatedEntity(EntityManager em) {
        RfbEventAttendance rfbEventAttendance = new RfbEventAttendance().attendanceDate(UPDATED_ATTENDANCE_DATE);
        return rfbEventAttendance;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RfbEventAttendance.class).block();
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
        rfbEventAttendance = createEntity(em);
    }

    @Test
    void createRfbEventAttendance() throws Exception {
        int databaseSizeBeforeCreate = rfbEventAttendanceRepository.findAll().collectList().block().size();
        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeCreate + 1);
        RfbEventAttendance testRfbEventAttendance = rfbEventAttendanceList.get(rfbEventAttendanceList.size() - 1);
        assertThat(testRfbEventAttendance.getAttendanceDate()).isEqualTo(DEFAULT_ATTENDANCE_DATE);
    }

    @Test
    void createRfbEventAttendanceWithExistingId() throws Exception {
        // Create the RfbEventAttendance with an existing ID
        rfbEventAttendance.setId(1L);
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        int databaseSizeBeforeCreate = rfbEventAttendanceRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRfbEventAttendances() {
        // Initialize the database
        rfbEventAttendanceRepository.save(rfbEventAttendance).block();

        // Get all the rfbEventAttendanceList
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
            .value(hasItem(rfbEventAttendance.getId().intValue()))
            .jsonPath("$.[*].attendanceDate")
            .value(hasItem(DEFAULT_ATTENDANCE_DATE.toString()));
    }

    @Test
    void getRfbEventAttendance() {
        // Initialize the database
        rfbEventAttendanceRepository.save(rfbEventAttendance).block();

        // Get the rfbEventAttendance
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rfbEventAttendance.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rfbEventAttendance.getId().intValue()))
            .jsonPath("$.attendanceDate")
            .value(is(DEFAULT_ATTENDANCE_DATE.toString()));
    }

    @Test
    void getNonExistingRfbEventAttendance() {
        // Get the rfbEventAttendance
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRfbEventAttendance() throws Exception {
        // Initialize the database
        rfbEventAttendanceRepository.save(rfbEventAttendance).block();

        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().collectList().block().size();

        // Update the rfbEventAttendance
        RfbEventAttendance updatedRfbEventAttendance = rfbEventAttendanceRepository.findById(rfbEventAttendance.getId()).block();
        updatedRfbEventAttendance.attendanceDate(UPDATED_ATTENDANCE_DATE);
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(updatedRfbEventAttendance);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rfbEventAttendanceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
        RfbEventAttendance testRfbEventAttendance = rfbEventAttendanceList.get(rfbEventAttendanceList.size() - 1);
        assertThat(testRfbEventAttendance.getAttendanceDate()).isEqualTo(UPDATED_ATTENDANCE_DATE);
    }

    @Test
    void putNonExistingRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().collectList().block().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rfbEventAttendanceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().collectList().block().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().collectList().block().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRfbEventAttendanceWithPatch() throws Exception {
        // Initialize the database
        rfbEventAttendanceRepository.save(rfbEventAttendance).block();

        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().collectList().block().size();

        // Update the rfbEventAttendance using partial update
        RfbEventAttendance partialUpdatedRfbEventAttendance = new RfbEventAttendance();
        partialUpdatedRfbEventAttendance.setId(rfbEventAttendance.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRfbEventAttendance.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbEventAttendance))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
        RfbEventAttendance testRfbEventAttendance = rfbEventAttendanceList.get(rfbEventAttendanceList.size() - 1);
        assertThat(testRfbEventAttendance.getAttendanceDate()).isEqualTo(DEFAULT_ATTENDANCE_DATE);
    }

    @Test
    void fullUpdateRfbEventAttendanceWithPatch() throws Exception {
        // Initialize the database
        rfbEventAttendanceRepository.save(rfbEventAttendance).block();

        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().collectList().block().size();

        // Update the rfbEventAttendance using partial update
        RfbEventAttendance partialUpdatedRfbEventAttendance = new RfbEventAttendance();
        partialUpdatedRfbEventAttendance.setId(rfbEventAttendance.getId());

        partialUpdatedRfbEventAttendance.attendanceDate(UPDATED_ATTENDANCE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRfbEventAttendance.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRfbEventAttendance))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
        RfbEventAttendance testRfbEventAttendance = rfbEventAttendanceList.get(rfbEventAttendanceList.size() - 1);
        assertThat(testRfbEventAttendance.getAttendanceDate()).isEqualTo(UPDATED_ATTENDANCE_DATE);
    }

    @Test
    void patchNonExistingRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().collectList().block().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rfbEventAttendanceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().collectList().block().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRfbEventAttendance() throws Exception {
        int databaseSizeBeforeUpdate = rfbEventAttendanceRepository.findAll().collectList().block().size();
        rfbEventAttendance.setId(count.incrementAndGet());

        // Create the RfbEventAttendance
        RfbEventAttendanceDTO rfbEventAttendanceDTO = rfbEventAttendanceMapper.toDto(rfbEventAttendance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rfbEventAttendanceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RfbEventAttendance in the database
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRfbEventAttendance() {
        // Initialize the database
        rfbEventAttendanceRepository.save(rfbEventAttendance).block();

        int databaseSizeBeforeDelete = rfbEventAttendanceRepository.findAll().collectList().block().size();

        // Delete the rfbEventAttendance
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rfbEventAttendance.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RfbEventAttendance> rfbEventAttendanceList = rfbEventAttendanceRepository.findAll().collectList().block();
        assertThat(rfbEventAttendanceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
