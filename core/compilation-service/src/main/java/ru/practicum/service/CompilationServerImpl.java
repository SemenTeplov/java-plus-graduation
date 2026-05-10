package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.dto.CategoryDto;
import main.java.ru.practicum.dto.EventShortDto;
import main.java.ru.practicum.dto.UserShortDto;
import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.dto.ResponseCompilationDto;
import main.java.ru.practicum.dto.RequestCompilationDto;
import main.java.ru.practicum.dto.RequestUpdateCompilationDto;
import main.java.ru.practicum.exception.NotFoundCompletion;
import main.java.ru.practicum.external.CategoryClient;
import main.java.ru.practicum.external.EventClient;
import main.java.ru.practicum.external.UserClient;
import main.java.ru.practicum.mapper.CompilationMapper;
import main.java.ru.practicum.persistence.entity.Compilation;
import main.java.ru.practicum.persistence.repository.CompilationRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServerImpl implements CompilationServer {

    private final CompilationRepository compilationRepository;

    private final CompilationMapper compilationMapper;

    private final EventClient eventClient;

    private final CategoryClient categoryClient;

    private final UserClient userClient;

    @Override
    public ResponseEntity<List<ResponseCompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size) {

        log.info(Messages.GET_COMPILATIONS);

        List<Compilation> compilations = compilationRepository.getCompilations(pinned, from, size).stream().toList();

        Set<EventShortDto> events = new HashSet<>(Objects.requireNonNull(eventClient.getAllById(compilations.stream()
                .flatMap(c -> c.getEvents().stream()).toList()).getBody()));

        List<ResponseCompilationDto> list = compilations.stream()
                .map(c -> compilationMapper
                            .compilationAndEventsToCompilationDto(c, completionCompilationDto(events.stream()
                                    .filter(e -> c.getEvents().stream()
                                            .anyMatch(i -> i.equals(e.getId())))
                                    .toList())))
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @Override
    public ResponseEntity<ResponseCompilationDto> getCompilation(Long compId) {

        log.info(Messages.GET_COMPILATION, compId);

        Compilation compilation = compilationRepository.getCompilation(compId)
                .orElseThrow(() -> new NotFoundCompletion(Exceptions.NOT_FOUND_COMPLETION));

        ResponseCompilationDto compilationDto =
                compilationMapper.compilationAndEventsToCompilationDto(compilation,
                        completionCompilationDto(Objects.requireNonNull(
                                eventClient.getAllById(compilation.getEvents()).getBody())));

        return ResponseEntity.status(HttpStatus.OK).body(compilationDto);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseCompilationDto> saveCompilation(RequestCompilationDto newCompilationDto) {

        log.info(Messages.SAVE_COMPILATION, newCompilationDto);

        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto,
                Objects.requireNonNull(eventClient.getAllById(newCompilationDto.events()).getBody()).stream()
                        .map(EventShortDto::getId).collect(Collectors.toList()));

        compilation = compilationRepository.save(compilation);

        ResponseCompilationDto compilationDto = compilationMapper.compilationAndEventsToCompilationDto(compilation,
                completionCompilationDto(Objects.requireNonNull(eventClient
                    .getAllById(compilation.getEvents()).getBody())));

        return ResponseEntity.status(HttpStatus.CREATED).body(compilationDto);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteCompilation(Long compId) {

        log.info(Messages.DELETE_COMPILATION, compId);

        compilationRepository.deleteById(compId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<ResponseCompilationDto> updateCompilation(
            Long compId, RequestUpdateCompilationDto updateCompilationRequest) {

        log.info(Messages.UPDATE_COMPILATION, compId);

        Compilation compilation = compilationRepository.getCompilation(compId)
                .orElseThrow(() -> new NotFoundCompletion(Exceptions.NOT_FOUND_COMPLETION));

        compilationMapper.updateCompilationRequestToCompilation(compilation, updateCompilationRequest);

        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(Objects.requireNonNull(eventClient
                    .getAllById(updateCompilationRequest.getEvents())
                    .getBody())
                    .stream().map(EventShortDto::getId)
                    .collect(Collectors.toList()));
        }

        compilation = compilationRepository.save(compilation);

        ResponseCompilationDto compilationDto = compilationMapper.compilationAndEventsToCompilationDto(compilation,
                completionCompilationDto(Objects.requireNonNull(
                        eventClient.getAllById(compilation.getEvents()).getBody())));

        return ResponseEntity.status(HttpStatus.OK).body(compilationDto);
    }

    private List<CategoryDto> getCategories(Long[] categoryIds) {

        return categoryClient.getCategoriesByIds(Arrays.stream(categoryIds).toList()).getBody();
    }

    private List<UserShortDto> getUsers(Long[] initiatorIds) {

        return userClient.getUsersById(Arrays.stream(initiatorIds).toList()).getBody();
    }

    private List<EventShortDto> completionCompilationDto(Collection<EventShortDto> events) {

        List<CategoryDto> categories = getCategories(events.stream()
                .map(EventShortDto::getCategory).distinct().toArray(Long[]::new));

        List<UserShortDto> users = getUsers(events.stream()
                .map(EventShortDto::getInitiator).distinct().toArray(Long[]::new));

        return events.stream().peek(e -> {
            e.setCategory(categories.stream().filter(ct -> ct.id()
                    .equals(e.getCategory().id())).findFirst().get());
            e.setInitiator(users.stream().filter(u -> u.id()
                    .equals(e.getInitiator().id())).findFirst().get());
        }).toList();
    }
}
