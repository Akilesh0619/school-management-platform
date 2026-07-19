package com.school.system.service;

import com.school.system.dto.EventDto;
import com.school.system.entity.Event;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventDto createEvent(EventDto dto) {
        Event event = Event.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .type(dto.getType() != null ? dto.getType().toUpperCase() : "EVENT")
                .build();
        return mapToDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventDto> getAllEvents() {
        return eventRepository.findAllByOrderByStartTimeAsc().stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) throw new ResourceNotFoundException("Event not found: " + id);
        eventRepository.deleteById(id);
    }

    private EventDto mapToDto(Event e) {
        return EventDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .type(e.getType())
                .build();
    }
}
