package com.alttalttal.mini_project.dto;

import com.alttalttal.mini_project.recipe_component.Ingredient;
import lombok.Getter;

import java.util.List;

@Getter
public class RecipeRequestDto {
    private Long id;
    private String name;
    private String base;
    private String explanation;
    private List<Ingredient> ingredient;
    private String recipe;
}
