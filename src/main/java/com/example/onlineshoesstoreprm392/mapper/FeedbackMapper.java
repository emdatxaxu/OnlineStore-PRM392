package com.example.onlineshoesstoreprm392.mapper;

import com.example.onlineshoesstoreprm392.entity.Feedback;
import com.example.onlineshoesstoreprm392.payload.FeedbackDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    Feedback toFeedback(FeedbackDto feedbackDto);
    FeedbackDto toFeedbackDto(Feedback feedback);
}
