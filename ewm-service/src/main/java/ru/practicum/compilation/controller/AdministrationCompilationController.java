package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin/compilations")
public class AdministrationCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        CompilationDto ans = compilationService.addCompilation(newCompilationDto);
        log.info("Админ добавляет новую подборку событий");
        return ans;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
        log.info("Админ удаляет подборку событий id = {}", compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody @Valid UpdateCompilationDto updateCompilationDto) {
        CompilationDto ans = compilationService.updateCompilation(compId, updateCompilationDto);
        log.info("Админ внёс изменения в подборку событий id = {}", compId);
        return ans;
    }
}