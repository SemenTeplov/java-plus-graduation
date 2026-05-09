package main.java.ru.practicum.service;

import jakarta.validation.ValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.dto.CategoryDto;
import main.dto.EndpointHitDto;
import main.dto.EventRequestStatusRequest;
import main.dto.EventRequestStatusUpdateResult;
import main.dto.EventShortDto;
import main.dto.ParticipationRequestDto;
import main.dto.UserShortDto;
import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.dto.EventFullDto;
import main.java.ru.practicum.dto.GetEventsForAdminRequest;
import main.java.ru.practicum.dto.GetEventsRequest;
import main.java.ru.practicum.dto.NewEventDto;
import main.java.ru.practicum.dto.UpdateEventAdminRequest;
import main.java.ru.practicum.dto.UpdateEventUserRequest;
import main.java.ru.practicum.exception.ForbiddenException;
import main.java.ru.practicum.exception.LimitRequestsExceededException;
import main.java.ru.practicum.exception.MismatchDateException;
import main.java.ru.practicum.exception.NotFoundException;
import main.java.ru.practicum.exception.NotMeetRulesEditionException;
import main.java.ru.practicum.exception.NotRespondStatusException;
import main.java.ru.practicum.externel.CategoryClient;
import main.java.ru.practicum.externel.RequestClient;
import main.java.ru.practicum.externel.StatsClient;
import main.java.ru.practicum.externel.UserClient;
import main.java.ru.practicum.mapper.EventMapper;
import main.java.ru.practicum.mapper.LocationMapper;
import main.java.ru.practicum.persistence.entity.Event;
import main.java.ru.practicum.persistence.entity.Location;
import main.java.ru.practicum.persistence.repository.EventRepository;
import main.java.ru.practicum.persistence.repository.LocationRepository;
import main.java.ru.practicum.persistence.status.State;
import main.java.ru.practicum.persistence.status.StateEvent;
import main.java.ru.practicum.specification.EventSpecification;
import main.status.StatusRequest;
import main.status.Status;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final LocationRepository locationRepository;

    private final EventMapper eventMapper;

    private final LocationMapper locationMapper;

    private final UserClient userClient;

    private final CategoryClient categoryClient;

    private final RequestClient requestClient;

    private final StatsClient statsClient;

    private final EventSpecification eventSpecification;

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {

        log.info(Messages.MESSAGE_ADD_EVENT, userId, newEventDto);

        validateEventFields(newEventDto);
        checkDate(newEventDto.eventDate());

        Location location = locationRepository
                .save(locationMapper.locationDtoToLocation(newEventDto.location()));

        Event event = eventMapper.newEventDtoToEvent(newEventDto);
        event.setInitiator(userId);
        event.setLocation(location.getId());
        event.setState(State.PENDING.toString());
        event.setConfirmedRequests(0);
        event.setViews(0L);
        event = eventRepository.save(event);

        return getEventFullDto(event, location);
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusRequest eventRequestStatusRequest) {

        log.info(Messages.MESSAGE_CHANGE_STATUS);

        UserShortDto user = userClient.getUserById(userId).getBody();

        if (user == null) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId));
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        List<ParticipationRequestDto> requests = requestClient
                .getRequestsByIds(eventRequestStatusRequest.requestIds()).getBody();

        if (eventRequestStatusRequest.status().equals(StatusRequest.CONFIRMED)) {
            Long confirmedRequests =
                    requestClient.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString()).getBody();

            assert requests != null;
            long newConfirmedCount = confirmedRequests + requests.size();

            if (event.getParticipantLimit() > 0 && newConfirmedCount > event.getParticipantLimit()) {
                throw new LimitRequestsExceededException(Exceptions.EXCEPTION_LIMIT_EXCEEDED);
            }
        }

        List<ParticipationRequestDto> participationRequestDto =
                requestClient.addParticipationRequests(requests.stream().peek(r -> {
                    if (!r.getStatus().equals(Status.PENDING.toString())) {
                        throw new NotRespondStatusException(Messages.MESSAGE_NOT_RESPOND_STATUS);
                    } else {
                        r.setStatus(eventRequestStatusRequest.status().toString());
                    }
                }).toList()).getBody();

        EventRequestStatusUpdateResult result;

        if (eventRequestStatusRequest.status().equals(Status.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + participationRequestDto.size());
            result = EventRequestStatusUpdateResult.builder().confirmedRequests(participationRequestDto).build();
        } else {
            result = EventRequestStatusUpdateResult.builder().rejectedRequests(participationRequestDto).build();
        }

        eventRepository.save(event);

        return result;
    }

    @Override
    public EventFullDto getEvent(Long id) {

        log.info(Messages.MESSAGE_GET_EVENT_BY_ID, id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        if (event.getViews() == 0) {
            event.setViews(1L);
            eventRepository.save(event);
        }

        if (!event.getState().equals(State.PUBLISHED.toString())) {
            throw new NotFoundException(Exceptions.EXCEPTION_NOT_PUBLISHED);
        }

        Location location = locationRepository.findById(event.getLocation())
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        statsClient.saveHit(EndpointHitDto.builder()
                .uri(Values.EVENT_GET_URI + id)
                .app(Values.APPLICATION)
                .ip(Values.EWM_IP)
                .timestamp(LocalDateTime.now())
                .build());

        return getEventFullDto(event, location);
    }

    @Override
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {

        log.info(Messages.MESSAGE_GET_PARTICIPANTS, userId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_EVENT_NOT_FOUND));

        if (!event.getInitiator().equals(userId)) {
            throw new ForbiddenException(Exceptions.EXCEPTION_ONLY_INITIATOR);
        }

        return requestClient.getRequestsByEvent(eventId).getBody();
    }

    @Override
    public EventFullDto getEventUser(Long userId, Long eventId) {

        log.info(Messages.MESSAGE_GET_EVENTS_BY_USER_ID_AND_EVENT_ID, eventId, userId);

        Event event = eventRepository.getEventByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));
        Location location = locationRepository.findById(event.getLocation())
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        return getEventFullDto(event, location);
    }

    @Override
    public List<EventShortDto> getEvents(GetEventsRequest request) {

        log.info(Messages.MESSAGE_GET_EVENTS);

        validateDateRange(request.getRangeStart(), request.getRangeEnd());

        List<EventShortDto> list = eventSpecification.getPagesFromGetEventsRequest(request, eventRepository)
                .stream()
                .map(this::getEventShortDto)
                .toList();

        statsClient.saveHit(EndpointHitDto.builder()
                .uri(Values.EVENTS_GET_URI)
                .app(Values.APPLICATION)
                .ip(Values.EWM_IP)
                .timestamp(LocalDateTime.now())
                .build());

        return list;
    }

    @Override
    public List<EventFullDto> getEventsAdmin(GetEventsForAdminRequest request) {

        log.info(Messages.MESSAGE_GET_EVENTS_FOR_ADMIN);

        Page<Event> events = eventSpecification.getPagesFromGetEventsForAdminRequest(request, eventRepository);
        Map<Long, UserShortDto> users = userClient.getUsersById(events.map(Event::getInitiator).toList()).getBody()
                .stream().collect(Collectors.toMap(UserShortDto::id, u -> u));
        Map<Long, CategoryDto> category = categoryClient.getCategoriesByIds(events.map(Event::getCategory).toList()).getBody()
                .stream().collect(Collectors.toMap(CategoryDto::id, c -> c));
        Map<Long, Location> location = locationRepository.findAllById(events.map(Event::getLocation).toList())
                .stream().collect(Collectors.toMap(Location::getId, l -> l));

        return events.stream()
                .map(e -> {
                    EventFullDto eventFullDto = eventMapper.eventToEventFullDto(e);

                    eventFullDto.setInitiator(users.get(e.getInitiator()));
                    eventFullDto.setCategory(category.get(e.getCategory()));
                    eventFullDto.setLocation(locationMapper.locationToLocationDto(location.get(e.getLocation())));

                    return eventFullDto;
                })
                .toList();
    }

    @Override
    public List<EventShortDto> getEventsUser(Long userId, Integer from, Integer size) {

        log.info(Messages.MESSAGE_GET_EVENTS_FOR_USER);

        return eventRepository.getEventsUser(userId, from, size).stream()
                .map(this::getEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {

        log.info(Messages.MESSAGE_UPDATE_EVENT);

        checkDate(updateEventUserRequest.getEventDate());

        Event event = eventRepository.getEventByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        if (event.getState().equals(State.PUBLISHED.toString())) {
            throw new ForbiddenException(Exceptions.EXCEPTION_CANT_UPDATE_PUBLISHED);
        }

        Location location = null;

        if (updateEventUserRequest.getLocation() != null) {
            location = locationRepository
                    .save(locationMapper.locationDtoToLocation(updateEventUserRequest.getLocation()));
        } else {
            location = locationRepository.findById(event.getLocation()).get();
        }

        eventMapper.updateEventUserRequestToEvent(event, location.getId(), updateEventUserRequest);

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    if (event.getState().equals(State.PENDING.toString()) ||
                            event.getState().equals(State.CANCELED.toString())) {
                        event.setState(State.CANCELED.toString());
                    } else {
                        throw new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_MEET_RULES);
                    }
                    break;
                case SEND_TO_REVIEW:
                    if (event.getState().equals(State.CANCELED.toString())) {
                        event.setState(State.PENDING.toString());
                    } else {
                        event.setState(State.CANCELED.toString());
                    }
                    break;
                default:
                    throw new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_MEET_RULES);
            }
        }

        eventRepository.save(event);

        return getEventFullDto(event, location);
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        log.info(Messages.MESSAGE_UPDATE_EVENT);

        checkDate(updateEventAdminRequest.getEventDate());

        Location location = null;

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        if (updateEventAdminRequest.getLocation() != null) {
            location = locationRepository
                    .save(locationMapper.locationDtoToLocation(updateEventAdminRequest.getLocation()));
            event.setLocation(location.getId());
        } else {
            location = locationRepository.findById(event.getLocation()).get();
            updateEventAdminRequest.setLocation(locationMapper.locationToLocationDto(location));
        }

        eventMapper.updateEventAdminRequestToEvent(event, location.getId(), updateEventAdminRequest);

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(State.PUBLISHED.toString())
                    && updateEventAdminRequest.getStateAction()
                    .equals(StateEvent.REJECT_EVENT)) {
                event.setState(State.CANCELED.toString());
            } else if (event.getState().equals(State.PENDING.toString())
                    && updateEventAdminRequest.getStateAction()
                    .equals(StateEvent.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED.toString());
            } else {
                throw new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_MEET_RULES);
            }
        }

        return getEventFullDto(eventRepository.save(event), location);
    }

    @Override
    public Boolean existsByCategoryId(Long eventId) {

        return eventRepository.existsByCategoryId(eventId);
    }

    @Override
    public EventShortDto getEventById(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_EVENT_NOT_FOUND, id)));

        if (!event.getState().equals("PUBLISHED")) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_NOT_PUBLISHED);
        }

        return eventMapper.eventToEventShortDto(event);
    }

    @Override
    public List<EventShortDto> getAllById(List<Long> ids) {

        return eventRepository
                .getEventsByIds(ids.toArray(Long[]::new)).stream().map(eventMapper::eventToEventShortDto).toList();
    }

    @Override
    public String getStatus(Long eventId, Long userId, String status, Long count) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_EVENT_NOT_FOUND, eventId)));

        if (userId.equals(event.getInitiator())) {
            throw new ForbiddenException(Exceptions.EXCEPTION_REQUEST_INITIATOR_OWN);
        }

        if (!event.getState().equals(State.PUBLISHED.toString())) {
            throw new ForbiddenException(Exceptions.EXCEPTION_REQUEST_NOT_PUBLISHED);
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= count) {
            throw new ForbiddenException(Exceptions.EXCEPTION_REQUEST_LIMIT);
        }

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            return String.valueOf(Status.PENDING);
        }

        return String.valueOf(Status.CONFIRMED);
    }

    private EventFullDto getEventFullDto(Event event, Location location) {

        UserShortDto user = userClient.getUserById(event.getInitiator()).getBody();

        if (user == null) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, event.getInitiator()));
        }

        CategoryDto category = categoryClient.getCategory(event.getCategory()).getBody();

        if (category == null) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND, event.getCategory()));
        }

        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);

        eventFullDto.setPublishedOn(OffsetDateTime.now().format(DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)));
        eventFullDto.setInitiator(user);
        eventFullDto.setCategory(category);
        eventFullDto.setLocation(locationMapper.locationToLocationDto(location));

        return eventFullDto;
    }

    private EventShortDto getEventShortDto(Event event) {
        EventShortDto eventShortDto = eventMapper.eventToEventShortDto(event);

        UserShortDto user = userClient.getUserById(event.getInitiator()).getBody();

        if (user == null) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, event.getInitiator()));
        }

        CategoryDto category = categoryClient.getCategory(event.getCategory()).getBody();

        if (category == null) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND, event.getCategory()));
        }

        eventShortDto.setInitiator(user);
        eventShortDto.setCategory(category);

        return eventShortDto;
    }

    private void checkDate(String time) {
        if (time == null) {
            return;
        }

        ZoneId zoneId = ZoneId.of("UTC");
        OffsetDateTime dateTime = ZonedDateTime.of(
                LocalDateTime.parse(time, DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)),
                zoneId).toOffsetDateTime();

        if (Duration.between(OffsetDateTime.now(zoneId), dateTime).toHours() < 2) {
            throw new MismatchDateException(Exceptions.EXCEPTION_DATE_MISMATCH);
        }
    }

    private void validateEventFields(NewEventDto newEventDto) {

        if (newEventDto.annotation() != null && newEventDto.annotation().trim().isEmpty()) {
            throw new ValidationException(Exceptions.EXCEPTION_FIELD_ANNOTATION_NOT_HAS_SPACE);
        }

        if (newEventDto.description() != null && newEventDto.description().trim().isEmpty()) {
            throw new ValidationException(Exceptions.EXCEPTION_FIELD_DESCRIPTION_NOT_HAS_SPACE);
        }

        if (newEventDto.title() != null && newEventDto.title().trim().isEmpty()) {
            throw new ValidationException(Exceptions.EXCEPTION_FIELD_TITLE_NOT_HAS_SPACE);
        }
    }

    private void validateDateRange(String rangeStart, String rangeEnd) {
        if (rangeStart != null && rangeEnd != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN);

            LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

            if (start.isAfter(end)) {
                throw new ValidationException(Exceptions.EXCEPTION_WRONG_DATE_RANGE);
            }
        }
    }
}
