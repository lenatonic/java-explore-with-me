package ru.practicum.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdministrationCategoryController {

    private final CategoryService categoryService;

    @Autowired
    public AdministrationCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/categories")
    public CategoryDto addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto ans = categoryService.addCategory(categoryDto);
        log.debug("Админ добавил категорию {}", categoryDto.getName());
        return ans;
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto patchCategory(@RequestBody @Valid CategoryDto categoryDto,
                                     @PathVariable Long catId) {
        CategoryDto ans = categoryService.patchCategory(catId, categoryDto);
        log.debug("Админ изменил категорию с id = {} на {}.", catId, categoryDto.getName());
        return ans;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
        log.debug("Админ удаляет категорию с id = {}.", catId);
    }
}