package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.error.exceptions.NotValidException;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        var events = newCompilationDto.getEvents() == null ?
                new HashSet<Event>() : eventRepository.findAllById(newCompilationDto.getEvents());
        HashSet<Event> added = new HashSet<>();
        added.addAll(events);

        return CompilationMapper
                .toCompilationDto(compilationRepository.save(CompilationMapper
                        .toCompilation(newCompilationDto, added)));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, NewCompilationDto updateCompilationDto) {
        if (updateCompilationDto.getTitle() != null && (1 > updateCompilationDto.getTitle().length() || 50 < updateCompilationDto.getTitle().length())) {
            throw new NotValidException("Длина названия подборки событий не соответствует параметрам min = 1, max = 50");
        }
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Нет данных подборки событий с id = " + compId + "."));

        var events = updateCompilationDto.getEvents() == null ?
                new HashSet<Event>() : eventRepository.findAllById(updateCompilationDto.getEvents());

        HashSet<Event> up = new HashSet<>();
        up.addAll(events);

        return CompilationMapper.toCompilationDto(compilationRepository.save(CompilationMapper
                .toCompilationForUpdate(compilation, updateCompilationDto, up)));
    }

    @Override
    public List<CompilationDto> findCompilations(boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository.findAllByPinned(pinned, pageable)
                .stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto findCompilation(Long compId) {
        return CompilationMapper.toCompilationDto(compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка событий с id = " + compId + ".")));
    }
}