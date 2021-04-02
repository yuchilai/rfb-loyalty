package com.rfb.service.impl;

import com.rfb.domain.RfbLocation;
import com.rfb.repository.RfbLocationRepository;
import com.rfb.service.RfbLocationService;
import com.rfb.service.dto.RfbLocationDTO;
import com.rfb.service.mapper.RfbLocationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RfbLocation}.
 */
@Service
@Transactional
public class RfbLocationServiceImpl implements RfbLocationService {

    private final Logger log = LoggerFactory.getLogger(RfbLocationServiceImpl.class);

    private final RfbLocationRepository rfbLocationRepository;

    private final RfbLocationMapper rfbLocationMapper;

    public RfbLocationServiceImpl(RfbLocationRepository rfbLocationRepository, RfbLocationMapper rfbLocationMapper) {
        this.rfbLocationRepository = rfbLocationRepository;
        this.rfbLocationMapper = rfbLocationMapper;
    }

    @Override
    public Mono<RfbLocationDTO> save(RfbLocationDTO rfbLocationDTO) {
        log.debug("Request to save RfbLocation : {}", rfbLocationDTO);
        return rfbLocationRepository.save(rfbLocationMapper.toEntity(rfbLocationDTO)).map(rfbLocationMapper::toDto);
    }

    @Override
    public Mono<RfbLocationDTO> partialUpdate(RfbLocationDTO rfbLocationDTO) {
        log.debug("Request to partially update RfbLocation : {}", rfbLocationDTO);

        return rfbLocationRepository
            .findById(rfbLocationDTO.getId())
            .map(
                existingRfbLocation -> {
                    rfbLocationMapper.partialUpdate(existingRfbLocation, rfbLocationDTO);
                    return existingRfbLocation;
                }
            )
            .flatMap(rfbLocationRepository::save)
            .map(rfbLocationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RfbLocationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RfbLocations");
        return rfbLocationRepository.findAllBy(pageable).map(rfbLocationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return rfbLocationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RfbLocationDTO> findOne(Long id) {
        log.debug("Request to get RfbLocation : {}", id);
        return rfbLocationRepository.findById(id).map(rfbLocationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RfbLocation : {}", id);
        return rfbLocationRepository.deleteById(id);
    }
}
