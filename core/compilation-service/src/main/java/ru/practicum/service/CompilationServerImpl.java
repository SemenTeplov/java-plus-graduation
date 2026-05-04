package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.dto.CategoryDto;
import main.dto.EventShortDto;
import main.dto.UserShortDto;
import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.dto.CompilationDto;
import main.java.ru.practicum.dto.NewCompilationDto;
import main.java.ru.practicum.dto.UpdateCompilationRequest;
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
    public ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size) {

        log.info(Messages.GET_COMPILATIONS);

        List<Compilation> compilations = compilationRepository.getCompilations(pinned, from, size).stream().toList();
        Set<EventShortDto> events = new HashSet<>(Objects.requireNonNull(eventClient.getAllById(compilations.stream()
                .flatMap(c -> c.getEvents().stream()).toList()).getBody()));
        List<CompilationDto> list = compilations.stream()
                .map(c -> {
                    CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(c);

                    compilationDto.setEvents(completionCompilationDto(events.stream()
                                .filter(e -> c.getEvents().stream()
                                        .anyMatch(i -> i.equals(e.getId())))
                                .toList()));

                    return compilationDto;
                }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @Override
    public ResponseEntity<CompilationDto> getCompilation(Long compId) {
        log.info(Messages.GET_COMPILATION, compId);

        Compilation compilation = compilationRepository.getCompilation(compId)
                .orElseThrow(() -> new NotFoundCompletion(Exceptions.NOT_FOUND_COMPLETION));
        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        compilationDto.setEvents(completionCompilationDto(Objects.requireNonNull(eventClient
                .getAllById(compilation.getEvents()).getBody())));

        return ResponseEntity.status(HttpStatus.OK).body(compilationDto);
    }

    @Override
    @Transactional
    public ResponseEntity<CompilationDto> saveCompilation(NewCompilationDto newCompilationDto) {

        log.info(Messages.SAVE_COMPILATION, newCompilationDto);

        if (newCompilationDto.title().isBlank()) {
            throw new IllegalArgumentException();
        }

        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto);
        compilation = compilationRepository.save(compilation);

        Long id = compilation.getId();

        compilation.setEvents(Objects.requireNonNull(eventClient.getAllById(newCompilationDto.events()).getBody())
                .stream().map(EventShortDto::getId).toList());

        compilation = compilationRepository.save(compilation);

        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        compilationDto.setEvents(completionCompilationDto(Objects.requireNonNull(eventClient
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
    public ResponseEntity<CompilationDto> updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {

        log.info(Messages.UPDATE_COMPILATION, compId);

        Compilation compilation = compilationRepository.getCompilation(compId)
                .orElseThrow(() -> new NotFoundCompletion(Exceptions.NOT_FOUND_COMPLETION));
        compilationMapper.updateCompilationRequestToCompilation(compilation, updateCompilationRequest);

        Long id = compilation.getId();

        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(Objects.requireNonNull(eventClient.getAllById(updateCompilationRequest.getEvents()).getBody())
                    .stream().map(EventShortDto::getId).toList());
        }

        compilation = compilationRepository.save(compilation);

        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        compilationDto.setEvents(completionCompilationDto(Objects.requireNonNull(eventClient
                .getAllById(compilation.getEvents()).getBody())));

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
