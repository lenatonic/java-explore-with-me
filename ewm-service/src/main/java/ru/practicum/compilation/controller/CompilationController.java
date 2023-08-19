package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    List<CompilationDto> findCompilations(@RequestParam(name = "from") int from,
                                          @RequestParam(name = "size") int size) {
        List<CompilationDto> ans = compilationService.findCompilations(from, size);
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