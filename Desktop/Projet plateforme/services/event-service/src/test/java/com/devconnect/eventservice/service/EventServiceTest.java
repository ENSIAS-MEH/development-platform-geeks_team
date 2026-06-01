package com.devconnect.eventservice.service;

import com.devconnect.eventservice.dto.CreateEventRequest;
import com.devconnect.eventservice.entity.Event;
import com.devconnect.eventservice.entity.Registration;
import com.devconnect.eventservice.enums.EventStatus;
import com.devconnect.eventservice.enums.EventType;
import com.devconnect.eventservice.enums.RegistrationStatus;
import com.devconnect.eventservice.exception.*;
import com.devconnect.eventservice.kafka.EventProducer;
import com.devconnect.eventservice.repository.EventRepository;
import com.devconnect.eventservice.repository.RegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private RegistrationRepository registrationRepository;
    @Mock private EventProducer eventProducer;

    @InjectMocks private EventService eventService;

    private final UUID eventId = UUID.randomUUID();
    private final UUID organizerId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void registerForEvent_whenEventFull_throwsException() {
        Event event = publishedEvent(2);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);
        when(registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED)).thenReturn(2L);

        assertThatThrownBy(() -> eventService.registerForEvent(eventId, userId))
            .isInstanceOf(EventFullException.class);
    }

    @Test
    void registerForEvent_whenAlreadyRegistered_throwsException() {
        Event event = publishedEvent(null);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(true);

        assertThatThrownBy(() -> eventService.registerForEvent(eventId, userId))
            .isInstanceOf(AlreadyRegisteredException.class);
    }

    @Test
    void publishEvent_whenNotOwner_throwsException() {
        Event event = Event.builder().id(eventId).organizerId(organizerId).status(EventStatus.DRAFT).build();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.publishEvent(eventId, UUID.randomUUID()))
            .isInstanceOf(UnauthorizedActionException.class);
    }

    @Test
    void createEvent_whenEndBeforeStart_throwsException() {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Test Event");
        request.setType(EventType.MEETUP);
        request.setStartDate(LocalDateTime.now().plusDays(5));
        request.setEndDate(LocalDateTime.now().plusDays(3));

        assertThatThrownBy(() -> eventService.createEvent(request, organizerId))
            .isInstanceOf(InvalidEventDatesException.class);
    }

    @Test
    void cancelRegistration_whenNotRegistered_throwsException() {
        when(registrationRepository.findByEventIdAndUserId(eventId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.cancelRegistration(eventId, userId))
            .isInstanceOf(RegistrationNotFoundException.class);
    }

    @Test
    void registerForEvent_whenEventNotPublished_throwsException() {
        Event event = Event.builder().id(eventId).status(EventStatus.DRAFT).build();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.registerForEvent(eventId, userId))
            .isInstanceOf(EventNotPublishedException.class);
    }

    @Test
    void createEvent_whenValid_savesAndReturnsResponse() {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Hackathon 2025");
        request.setType(EventType.HACKATHON);
        request.setStartDate(LocalDateTime.now().plusDays(10));
        request.setEndDate(LocalDateTime.now().plusDays(12));

        Event saved = Event.builder()
            .id(eventId)
            .title(request.getTitle())
            .type(request.getType())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .organizerId(organizerId)
            .status(EventStatus.DRAFT)
            .build();

        when(eventRepository.save(any(Event.class))).thenReturn(saved);

        var response = eventService.createEvent(request, organizerId);

        assertThat(response.getTitle()).isEqualTo("Hackathon 2025");
        assertThat(response.getStatus()).isEqualTo(EventStatus.DRAFT);
        verify(eventRepository).save(any(Event.class));
        verify(eventProducer).publishEventCreated(any());
    }

    @Test
    void publishEvent_whenOrganizer_changesStatus() {
        Event event = Event.builder()
            .id(eventId)
            .title("My Event")
            .organizerId(organizerId)
            .status(EventStatus.DRAFT)
            .build();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));
        when(registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED)).thenReturn(0L);

        var response = eventService.publishEvent(eventId, organizerId);

        assertThat(response.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(EventStatus.PUBLISHED);
        verify(eventProducer).publishEventPublished(any());
    }

    @Test
    void getEventById_whenNotFound_throwsException() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(eventId, userId))
            .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void registerForEvent_noMaxParticipants_allowsRegistration() {
        Event event = publishedEvent(null);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);
        Registration saved = Registration.builder()
            .id(UUID.randomUUID())
            .eventId(eventId)
            .userId(userId)
            .status(RegistrationStatus.CONFIRMED)
            .build();
        when(registrationRepository.save(any(Registration.class))).thenReturn(saved);

        var response = eventService.registerForEvent(eventId, userId);

        assertThat(response.getUserId()).isEqualTo(userId);
        verify(registrationRepository).save(any(Registration.class));
        verify(eventProducer).publishUserRegistered(any());
    }

    private Event publishedEvent(Integer maxParticipants) {
        return Event.builder()
            .id(eventId)
            .title("Published Event")
            .organizerId(organizerId)
            .status(EventStatus.PUBLISHED)
            .maxParticipants(maxParticipants)
            .build();
    }
}
