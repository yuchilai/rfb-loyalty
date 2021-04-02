package com.rfb.service.impl;

import com.rfb.domain.RfbEventAttendance;
import com.rfb.repository.RfbEventAttendanceRepository;
import com.rfb.service.RfbEventAttendanceService;
import com.rfb.service.dto.RfbEventAttendanceDTO;
import com.rfb.service.mapper.RfbEventAttendanceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RfbEventAttendance}.
 */
@Service
@Transactional
public class RfbEventAttendanceServiceImpl implements RfbEventAttendanceService {

    private final Logger log = LoggerFactory.getLogger(RfbEventAttendanceServiceImpl.class);

    private final RfbEventAttendanceRepository rfbEventAttendanceRepository;

    private final RfbEventAttendanceMapper rfbEventAttendanceMapper;

    public RfbEventAttendanceServiceImpl(
        RfbEventAttendanceRepository rfbEventAttendanceRepository,
        RfbEventAttendanceMapper rfbEventAttendanceMapper
    ) {
        this.rfbEventAttendanceRepository = rfbEventAttendanceRepository;
        this.rfbEventAttendanceMapper = rfbEventAttendanceMapper;
    }

    @Override
    public Mono<RfbEventAttendanceDTO> save(RfbEventAttendanceDTO rfbEventAttendanceDTO) {
        log.debug("Request to save RfbEventAttendance : {}", rfbEventAttendanceDTO);
        return rfbEventAttendanceRepository
            .save(rfbEventAttendanceMapper.toEntity(rfbEventAttendanceDTO))
            .map(rfbEventAttendanceMapper::toDto);
    }

    @Override
    public Mono<RfbEventAttendanceDTO> partialUpdate(RfbEventAttendanceDTO rfbEventAttendanceDTO) {
        log.debug("Request to partially update RfbEventAttendance : {}", rfbEventAttendanceDTO);

        return rfbEventAttendanceRepository
            .findById(rfbEventAttendanceDTO.getId())
            .map(
                existingRfbEventAttendance -> {
                    rfbEventAttendanceMapper.partialUpdate(existingRfbEventAttendance, rfbEventAttendanceDTO);
                    return existingRfbEventAttendance;
                }
            )
            .flatMap(rfbEventAttendanceRepository::save)
            .map(rfbEventAttendanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RfbEventAttendanceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RfbEventAttendances");
        return rfbEventAttendanceRepository.findAllBy(pageable).map(rfbEventAttendanceMapper::toDto);
    }

    public Mono<Long> countAll() {
        return rfbEventAttendanceRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RfbEventAttendanceDTO> findOne(Long id) {
        log.debug("Request to get RfbEventAttendance : {}", id);
        return rfbEventAttendanceRepository.findById(id).map(rfbEventAttendanceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RfbEventAttendance : {}", id);
        return rfbEventAttendanceRepository.deleteById(id);
    }
}
