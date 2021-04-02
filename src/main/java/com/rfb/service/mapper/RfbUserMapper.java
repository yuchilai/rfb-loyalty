package com.rfb.service.mapper;

import com.rfb.domain.*;
import com.rfb.service.dto.RfbUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RfbUser} and its DTO {@link RfbUserDTO}.
 */
@Mapper(componentModel = "spring", uses = { RfbLocationMapper.class })
public interface RfbUserMapper extends EntityMapper<RfbUserDTO, RfbUser> {
    @Mapping(target = "homeLocation", source = "homeLocation", qualifiedByName = "id")
    RfbUserDTO toDto(RfbUser s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RfbUserDTO toDtoId(RfbUser rfbUser);
}
