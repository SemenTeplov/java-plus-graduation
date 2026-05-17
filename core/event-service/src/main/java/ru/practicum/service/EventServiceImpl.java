package main.java.ru.practicum.service;

import com.google.protobuf.Timestamp;

import jakarta.validation.ValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.dto.CategoryDto;
import main.java.ru.practicum.dto.EndpointHitDto;
import main.java.ru.practicum.dto.EventRequestStatusRequest;
import main.java.ru.practicum.dto.EventRequestStatusUpdateResult;
import main.java.ru.practicum.dto.EventShortDto;
import main.java.ru.practicum.dto.ParticipationRequestDto;
import main.java.ru.practicum.dto.UserShortDto;
import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.dto.ResponseEventFullDto;
import main.java.ru.practicum.dto.GetEventsForAdminRequest;
import main.java.ru.practicum.dto.GetEventsRequest;
import main.java.ru.practicum.dto.RequestEventDto;
import main.java.ru.practicum.dto.UpdateEventAdminRequest;
import main.java.ru.practicum.dto.UpdateEventUserRequest;
import main.java.ru.practicum.exception.ForbiddenException;
import main.java.ru.practicum.exception.LimitRequestsExceededException;
import main.java.ru.practicum.exception.MismatchDateException;
import main.java.ru.practicum.exception.NotFoundException;
import main.java.ru.practicum.exception.NotMeetRulesEditionException;
import main.java.ru.practicum.exception.NotRespondStatusException;
import main.java.ru.practicum.externel.RequestClient;
import main.java.ru.practicum.externel.StatsClient;
import main.java.ru.practicum.externel.UserClient;
import main.java.ru.practicum.mapper.CategoryMapper;
import main.java.ru.practicum.mapper.EventMapper;
import main.java.ru.practicum.mapper.LocationMapper;
import main.java.ru.practicum.persistence.entity.Event;
import main.java.ru.practicum.persistence.entity.Location;
import main.java.ru.practicum.persistence.repository.CategoryRepository;
import main.java.ru.practicum.persistence.repository.EventRepository;
import main.java.ru.practicum.persistence.repository.LocationRepository;
import main.java.ru.practicum.persistence.status.State;
import main.java.ru.practicum.persistence.status.StateEvent;
import main.java.ru.practicum.specification.EventSpecification;
import main.java.ru.practicum.persistence.status.StatusRequest;
import main.java.ru.practicum.persistence.status.Status;

import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import stats.messages.ActionTypeProto;
import stats.messages.RecommendedEventProto;
import stats.messages.UserActionProto;
import stats.messages.UserPredictionsRequestProto;
import stats.service.collector.RecommendationsControllerGrpc;
import stats.service.collector.UserActionControllerGrpc;

import java.time.Duration;
import java.time.Instant;
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

    private final CategoryRepository categoryRepository;

    private final EventMapper eventMapper;

    private final LocationMapper locationMapper;

    private final CategoryMapper categoryMapper;

    private final UserClient userClient;

    private final RequestClient requestClient;

    private final StatsClient statsClient;

    private final EventSpecification eventSpecification;

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub clientAnalyzer;

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub clientController;

    @Override
    public ResponseEventFullDto addEvent(Long userId, RequestEventDto newEventDto) {

        log.info(Messages.MESSAGE_ADD_EVENT, userId, newEventDto);

        checkDate(newEventDto.eventDate());

        Location location = locationRepository
                .save(locationMapper.locationDtoToLocation(newEventDto.location()));

        Event event = eventRepository
                .save(eventMapper.newEventDtoToEvent(newEventDto, userId, location.getId()));

        return getEventFullDto(event, location);
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusRequest eventRequestStatusRequest) {

        log.info(Messages.MESSAGE_CHANGE_STATUS);

        if (userClient.getUserById(userId).getBody() == null) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId));
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        List<ParticipationRequestDto> requests = requestClient
                .getRequestsByIds(eventRequestStatusRequest.requestIds()).getBody();

        EventRequestStatusUpdateResult result;

        if (eventRequestStatusRequest.status().equals(StatusRequest.CONFIRMED)) {

            Long confirmedRequests = requestClient
                    .countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString()).getBody();

            long newConfirmedCount = (confirmedRequests == null ? 0 : confirmedRequests) + requests.size();

            if (event.getParticipantLimit() > 0 && newConfirmedCount > event.getParticipantLimit()) {
                throw new LimitRequestsExceededException(Exceptions.EXCEPTION_LIMIT_EXCEEDED);
            }

            List<ParticipationRequestDto> participationRequestDto = getParticipationRequestDto(
                    requests, eventRequestStatusRequest.status().toString());

            event.setConfirmedRequests(event.getConfirmedRequests() + participationRequestDto.size());

            result = EventRequestStatusUpdateResult.builder().confirmedRequests(participationRequestDto).build();
        } else {

            List<ParticipationRequestDto> participationRequestDto = getParticipationRequestDto(
                    requests, eventRequestStatusRequest.status().toString());

            result = EventRequestStatusUpdateResult.builder().rejectedRequests(participationRequestDto).build();
        }

        eventRepository.save(event);

        return result;
    }

    @Override
    public ResponseEventFullDto getEvent(Long id, Long userId) {

        log.info(Messages.MESSAGE_GET_EVENT_BY_ID, id);

        clientController.collectUserAction(UserActionProto.newBuilder()
                .setEventId(Math.toIntExact(id))
                .setUserId(Math.toIntExact(userId))
                .setActionType(ActionTypeProto.ACTION_VIEW)
                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .build());
  
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        if (event.getRating() == 0) {
            event.setRating(1.0);
            eventRepository.save(event);
        }

        if (!event.getState().equals(State.PUBLISHED)) {
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
    public ResponseEventFullDto getEventUser(Long userId, Long eventId) {

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
    public List<ResponseEventFullDto> getEventsAdmin(GetEventsForAdminRequest request) {

        log.info(Messages.MESSAGE_GET_EVENTS_FOR_ADMIN);

        Page<Event> events = eventSpecification.getPagesFromGetEventsForAdminRequest(request, eventRepository);

        Map<Long, UserShortDto> users = userClient.getUsersById(events.map(Event::getInitiator).toList()).getBody()
                .stream().collect(Collectors.toMap(UserShortDto::id, u -> u));

        Map<Long, CategoryDto> category = categoryRepository
                .getCategoriesByIds(events.map(Event::getCategory).toList().toArray(Long[]::new)).stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toMap(CategoryDto::id, c -> c));

        Map<Long, Location> location = locationRepository.findAllById(events.map(Event::getLocation).toList())
                .stream().collect(Collectors.toMap(Location::getId, l -> l));

        return events.stream()
                .map(event -> eventMapper.eventToEventFullDto(
                        event,
                        users.get(event.getInitiator()),
                        category.get(event.getCategory()),
                        locationMapper.locationToLocationDto(location.get(event.getLocation()))))
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
    public ResponseEventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {

        log.info(Messages.MESSAGE_UPDATE_EVENT);

        checkDate(updateEventUserRequest.getEventDate());

        Event event = eventRepository.getEventByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException(Exceptions.EXCEPTION_CANT_UPDATE_PUBLISHED);
        }

        Location location;

        if (updateEventUserRequest.getLocation() != null) {
            location = locationRepository
                    .save(locationMapper.locationDtoToLocation(updateEventUserRequest.getLocation()));
        } else {
            location = locationRepository.findById(event.getLocation())
                    .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));
        }

        eventMapper.updateEventUserRequestToEvent(event, location.getId(), updateEventUserRequest);

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case CANCEL_REVIEW -> {
                    if (event.getState().equals(State.PENDING) ||
                            event.getState().equals(State.CANCELED)) {
                        event.setState(State.CANCELED);
                    } else {
                        throw new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_MEET_RULES);
                    }}
                case SEND_TO_REVIEW -> {
                    if (event.getState().equals(State.CANCELED)) {
                        event.setState(State.PENDING);
                    } else {
                        event.setState(State.CANCELED);
                    }}
                default ->
                    throw new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_MEET_RULES);
            }
        }

        eventRepository.save(event);

        return getEventFullDto(event, location);
    }

    @Override
    public ResponseEventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        log.info(Messages.MESSAGE_UPDATE_EVENT);

        checkDate(updateEventAdminRequest.getEventDate());

        Location location;

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

        if (updateEventAdminRequest.getLocation() != null) {
            location = locationRepository
                    .save(locationMapper.locationDtoToLocation(updateEventAdminRequest.getLocation()));
            event.setLocation(location.getId());
        } else {
            location = locationRepository.findById(event.getLocation())
                    .orElseThrow(() -> new NotFoundException(Exceptions.EXCEPTION_NOT_FOUND));

            updateEventAdminRequest.setLocation(locationMapper.locationToLocationDto(location));
        }

        eventMapper.updateEventAdminRequestToEvent(event, location.getId(), updateEventAdminRequest);

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(State.PUBLISHED)
                    && updateEventAdminRequest.getStateAction()
                    .equals(StateEvent.REJECT_EVENT)) {
                event.setState(State.CANCELED);
            } else if (event.getState().equals(State.PENDING)
                    && updateEventAdminRequest.getStateAction()
                    .equals(StateEvent.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
            } else {
                throw new NotMeetRulesEditionException(Exceptions.EXCEPTION_NOT_MEET_RULES);
            }
        }

        return getEventFullDto(eventRepository.save(event), location);
    }

    @Override
    public String getStatus(Long eventId, Long userId, String status, Long count) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_EVENT_NOT_FOUND, eventId)));

        if (userId.equals(event.getInitiator())) {
            throw new ForbiddenException(Exceptions.EXCEPTION_REQUEST_INITIATOR_OWN);
        }

        if (!event.getState().equals(State.PUBLISHED)) {
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

    @Override
    public List<EventShortDto> getRecommendations(Long userId) {

        RecommendedEventProto proto = clientAnalyzer.getRecommendationsForUser(UserPredictionsRequestProto
                .newBuilder()
                .setUserId(Math.toIntExact(userId))
                .setMaxResults(10)
                .build());

        List<Event> list = eventRepository.getEventByUserId(userId);

        return list.stream()
                .filter(i -> i.getId() == proto.getEventId())
                .map(eventMapper::eventToEventShortDto)
                .toList();
    }

    @Override
    public void sendLike(Long userId, Long eventId) {

        clientController.collectUserAction(UserActionProto
                .newBuilder()
                .setEventId(Math.toIntExact(userId))
                .setUserId(Math.toIntExact(userId))
                .setActionType(ActionTypeProto.ACTION_LIKE)
                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .build());
    }
  
    private ResponseEventFullDto getEventFullDto(Event event, Location location) {

        UserShortDto user = userClient.getUserById(event.getInitiator()).getBody();

        if (user == null) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, event.getInitiator()));
        }

        CategoryDto category = categoryMapper.toCategoryDto(categoryRepository.findById(event.getCategory()).orElseThrow(() ->
                new NotFoundException(String.format(Messages.MESSAGE_CATEGORY_NOT_FOUND, event.getCategory()))));

        if (category == null) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND));
        }

        return eventMapper.eventToEventFullDto(
                event,
                user,
                category,
                locationMapper.locationToLocationDto(location),
                OffsetDateTime.now().format(DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)));
    }

    private EventShortDto getEventShortDto(Event event) {
        EventShortDto eventShortDto = eventMapper.eventToEventShortDto(event);

        UserShortDto user = userClient.getUserById(event.getInitiator()).getBody();

        if (user == null) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, event.getInitiator()));
        }

        CategoryDto category = categoryMapper.toCategoryDto(categoryRepository.findById(event.getCategory()).orElseThrow(() ->
                new NotFoundException(String.format(Messages.MESSAGE_CATEGORY_NOT_FOUND, event.getCategory()))));

        if (category == null) {
            throw new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND));
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

    private List<ParticipationRequestDto> getParticipationRequestDto(
            List<ParticipationRequestDto> requests, String status) {

        if (requests == null) {
            return null;
        }

        return requestClient.addParticipationRequests(requests.stream().peek(r -> {
                    if (!r.getStatus().equals(Status.PENDING.toString())) {
                        throw new NotRespondStatusException(Messages.MESSAGE_NOT_RESPOND_STATUS);
                    } else {
                        r.setStatus(status);
                    }
                }).toList()).getBody();
    }
}
