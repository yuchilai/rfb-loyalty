package com.rfb.service.mapper;

import com.rfb.domain.*;
import com.rfb.service.dto.RfbEventDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RfbEvent} and its DTO {@link RfbEventDTO}.
 */
@Mapper(componentModel = "spring", uses = { RfbLocationMapper.class })
public interface RfbEventMapper extends EntityMapper<RfbEventDTO, RfbEvent> {
    @Mapping(target = "rfbLocation", source = "rfbLocation", qualifiedByName = "id")
    RfbEventDTO toDto(RfbEvent s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RfbEventDTO toDtoId(RfbEvent rfbEvent);
}
