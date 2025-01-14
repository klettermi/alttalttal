package com.alttalttal.mini_project.domain.recipe.controller;

import com.alttalttal.mini_project.global.dto.MessageResponseDto;
import com.alttalttal.mini_project.domain.recipe.dto.RecipeResponseDto;
import com.alttalttal.mini_project.domain.recipe.dto.simpleRecipesResponseDto;
import com.alttalttal.mini_project.global.jwt.JwtUtil;
import com.alttalttal.mini_project.domain.recipe.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = "*")
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping()
    private List<simpleRecipesResponseDto> getAllRecipes(){
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public RecipeResponseDto getRecipe(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response){
        return recipeService.getRecipe(id, request, response);
    }
    @PostMapping("/{id}")
    public ResponseEntity<MessageResponseDto> createZzim(@PathVariable Long id, @RequestHeader(JwtUtil.ACCESS_HEADER) String accessToken, @RequestHeader(JwtUtil.REFRESH_HEADER) String refreshToken, HttpServletResponse response){
        return recipeService.createZzim(id, accessToken, refreshToken, response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDto> deleteZzim(@PathVariable Long id, @RequestHeader(JwtUtil.ACCESS_HEADER) String accessToken, @RequestHeader(JwtUtil.REFRESH_HEADER) String refreshToken, HttpServletResponse response){
        return recipeService.deleteZzim(id, accessToken, refreshToken, response);
    }

}
