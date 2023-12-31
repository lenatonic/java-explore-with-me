package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    List<CompilationDto> findCompilations(@RequestParam(required = false) boolean pinned,
                                          @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        List<CompilationDto> ans = compilationService.findCompilations(pinned, from, size);
        log.info("Получение списка подборок событий.");
        return ans;
    }

    @GetMapping("/{compId}")
    CompilationDto findCompilation(@PathVariable Long compId) {
        CompilationDto ans = compilationService.findCompilation(compId);
        log.info("Получение подборки событий id = {}.", compId);
        return ans;
    }
}