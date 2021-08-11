//package com.taco.cloud.controller;
//
//import com.taco.cloud.dao.IngredientRepository;
//import com.taco.cloud.entity.Ingredient;
//import com.taco.cloud.entity.Taco;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.IteratorUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Controller
//@RequestMapping("/design")
//@SessionAttributes("/tacoOrder")
//public class DesignTacoController {
//
//    private final IngredientRepository ingredientRepo;
//
//    @Autowired
//    public DesignTacoController(
//            IngredientRepository ingredientRepo) {
//        this.ingredientRepo = ingredientRepo;
//    }
//
//    @ModelAttribute
//    public void addIngredientsToModel(Model model) {
//        Iterable<Ingredient> all = ingredientRepo.findAll();
//        List<Ingredient> ingredients = IteratorUtils.toList(all.iterator());
//
////        List<Ingredient> ingredients = Arrays.asList(
////                new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP),
////                new Ingredient("COTO", "Corn Tortilla", Ingredient.Type.WRAP),
////                new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN),
////                new Ingredient("CARN", "Carnitas", Ingredient.Type.PROTEIN),
////                new Ingredient("TMTO", "Diced Tomatoes", Ingredient.Type.VEGGIES),
////                new Ingredient("LETC", "Lettuce", Ingredient.Type.VEGGIES),
////                new Ingredient("CHED", "Cheddar", Ingredient.Type.CHEESE),
////                new Ingredient("JACK", "Monterrey Jack", Ingredient.Type.CHEESE),
////                new Ingredient("SLSA", "Salsa", Ingredient.Type.SAUCE),
////                new Ingredient("SRCR", "Sour Cream", Ingredient.Type.SAUCE)
////        );
////
//        Ingredient.Type[] types = Ingredient.Type.values();
//        for (Ingredient.Type type : types) {
//            model.addAttribute(type.toString().toLowerCase(),
//                    filterByType(ingredients, type));
//        }
//    }
//
//    @GetMapping
//    public String showDesignForm(Model model) {
//        model.addAttribute("taco", new Taco());
//        return "design";
//    }
//
//    private Iterable<Ingredient> filterByType(
//            List<Ingredient> ingredients, Ingredient.Type type) {
//        return ingredients
//                .stream()
//                .filter(x -> x.getType().equals(type))
//                .collect(Collectors.toList());
//    }
//
//    @PostMapping
//    public String processTaco(@Valid @ModelAttribute("taco") Taco taco, Errors errors) {
//        if (errors.hasErrors()) {
//            log.info("Errors:{}",errors);
//            return "design";
//        }
//
//        // Save the taco...
//        // We'll do this in chapter 3
//        log.info("Processing taco: " + taco);
//
//        return "redirect:/orders/current";
//    }
//}
