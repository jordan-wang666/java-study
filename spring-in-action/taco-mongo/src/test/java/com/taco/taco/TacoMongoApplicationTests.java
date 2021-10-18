package com.taco.taco;

import com.taco.taco.bean.Ingredient;
import com.taco.taco.dao.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class TacoMongoApplicationTests {

    @Resource
    private IngredientRepository ingredientRepository;

    @Test
    void contextLoads() {

        List<Ingredient> ingredients = Arrays.asList(
                new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP),
                new Ingredient("COTO", "Corn Tortilla", Ingredient.Type.WRAP),
                new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN),
                new Ingredient("CARN", "Carnitas", Ingredient.Type.PROTEIN),
                new Ingredient("TMTO", "Diced Tomatoes", Ingredient.Type.VEGGIES),
                new Ingredient("LETC", "Lettuce", Ingredient.Type.VEGGIES),
                new Ingredient("CHED", "Cheddar", Ingredient.Type.CHEESE),
                new Ingredient("JACK", "Monterrey Jack", Ingredient.Type.CHEESE),
                new Ingredient("SLSA", "Salsa", Ingredient.Type.SAUCE),
                new Ingredient("SRCR", "Sour Cream", Ingredient.Type.SAUCE)
        );
        Flux<Ingredient> flux = Flux.fromIterable(ingredients);
        Flux<Ingredient> ingredientFlux = ingredientRepository.saveAll(flux);

        ingredientFlux.subscribe(ingredient -> {
            System.out.println("ingredient = " + ingredient);
        });
    }

}
