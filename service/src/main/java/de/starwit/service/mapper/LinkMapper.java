package de.starwit.service.mapper;

import de.starwit.persistence.entity.LinkEntity;
import de.starwit.service.dto.LinkDto;

public class LinkMapper implements CustomMapper<LinkEntity, LinkDto> {

    @Override
    public LinkDto convertToDto(LinkEntity entity) {
        if (entity == null) {
            return null;
        }
        LinkDto dto = new LinkDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUrl(entity.getUrl());
        return dto;
    }

    @Override
    public LinkEntity convertToEntity(LinkDto dto) {
        if (dto == null) {
            return null;
        }
        LinkEntity entity = new LinkEntity();
        return convertToEntity(dto, entity);
    }

    public LinkEntity convertToEntity(LinkDto dto, LinkEntity entity) {
        if (dto == null) {
            return null;
        }
        if (entity == null) {
            entity = new LinkEntity();
        }
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setUrl(dto.getUrl());
        return entity;
    }
}
