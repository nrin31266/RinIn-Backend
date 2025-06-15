package com.linkedin.backend.features.message.mapper;

import com.linkedin.backend.features.message.dto.ParticipantDto;
import com.linkedin.backend.features.message.model.ConversationParticipant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessagingMapper {
    ParticipantDto toParticipantDto(ConversationParticipant participant);
}
