package com.rfb.service.mapper;

import com.rfb.domain.*;
import com.rfb.service.dto.RfbLocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RfbLocation} and its DTO {@link RfbLocationDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RfbLocationMapper extends EntityMapper<RfbLocationDTO, RfbLocation> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RfbLocationDTO toDtoId(RfbLocation rfbLocation);
}
