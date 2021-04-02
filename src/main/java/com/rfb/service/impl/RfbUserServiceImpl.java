package com.rfb.service.impl;

import com.rfb.domain.RfbUser;
import com.rfb.repository.RfbUserRepository;
import com.rfb.service.RfbUserService;
import com.rfb.service.dto.RfbUserDTO;
import com.rfb.service.mapper.RfbUserMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RfbUser}.
 */
@Service
@Transactional
public class RfbUserServiceImpl implements RfbUserService {

    private final Logger log = LoggerFactory.getLogger(RfbUserServiceImpl.class);

    private final RfbUserRepository rfbUserRepository;

    private final RfbUserMapper rfbUserMapper;

    public RfbUserServiceImpl(RfbUserRepository rfbUserRepository, RfbUserMapper rfbUserMapper) {
        this.rfbUserRepository = rfbUserRepository;
        this.rfbUserMapper = rfbUserMapper;
    }

    @Override
    public Mono<RfbUserDTO> save(RfbUserDTO rfbUserDTO) {
        log.debug("Request to save RfbUser : {}", rfbUserDTO);
        return rfbUserRepository.save(rfbUserMapper.toEntity(rfbUserDTO)).map(rfbUserMapper::toDto);
    }

    @Override
    public Mono<RfbUserDTO> partialUpdate(RfbUserDTO rfbUserDTO) {
        log.debug("Request to partially update RfbUser : {}", rfbUserDTO);

        return rfbUserRepository
            .findById(rfbUserDTO.getId())
            .map(
                existingRfbUser -> {
                    rfbUserMapper.partialUpdate(existingRfbUser, rfbUserDTO);
                    return existingRfbUser;
                }
            )
            .flatMap(rfbUserRepository::save)
            .map(rfbUserMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RfbUserDTO> findAll() {
        log.debug("Request to get all RfbUsers");
        return rfbUserRepository.findAll().map(rfbUserMapper::toDto);
    }

    public Mono<Long> countAll() {
        return rfbUserRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RfbUserDTO> findOne(Long id) {
        log.debug("Request to get RfbUser : {}", id);
        return rfbUserRepository.findById(id).map(rfbUserMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RfbUser : {}", id);
        return rfbUserRepository.deleteById(id);
    }
}
