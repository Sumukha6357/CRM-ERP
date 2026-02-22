package com.gully.ggos.api.crm.mapper;

import com.gully.ggos.api.crm.dto.LeadRequest;
import com.gully.ggos.api.crm.dto.LeadResponse;
import com.gully.ggos.domain.entity.CrmLead;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CrmMapper {

    @Mapping(target = "stageId", source = "stage.id")
    @Mapping(target = "ownerId", source = "owner.id")
    LeadResponse toLeadResponse(CrmLead lead);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "org", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "stage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CrmLead toLead(LeadRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "org", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "stage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateLead(LeadRequest request, @MappingTarget CrmLead lead);
}
