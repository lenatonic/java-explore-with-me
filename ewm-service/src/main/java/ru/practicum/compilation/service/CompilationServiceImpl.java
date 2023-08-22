package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = newCompilationDto.getEvents() == null ?
                new ArrayList<>() : eventRepository.findAllById(newCompilationDto.getEvents());
        return CompilationMapper
                .toCompilationDto(compilationRepository.save(CompilationMapper
                        .toCompilation(newCompilationDto, events)));
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Нет данных подборки событий с id = " + compId + "."));

        List<Event> events = updateCompilationDto.getEvents() == null ?
                new ArrayList<>() : eventRepository.findAllById(updateCompilationDto.getEvents());

        return CompilationMapper.toCompilationDto(compilationRepository.save(CompilationMapper
                .toCompilationForUpdate(compilation, updateCompilationDto, events)));
    }

    @Override
    public List<CompilationDto> findCompilations(int from, int size) {
        return compilationRepository.findAll(PageRequest.of(from / size, size))
                .stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto findCompilation(Long compId) {
        return CompilationMapper.toCompilationDto(compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка событий с id = " + compId + ".")));
    }
}