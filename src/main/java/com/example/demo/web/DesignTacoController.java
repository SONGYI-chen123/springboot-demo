package com.example.demo.web;

import com.example.demo.entity.Ingredient;
import com.example.demo.entity.Order;
import com.example.demo.entity.Taco;
import com.example.demo.jdbc.repository.IngredientRepository;
import com.example.demo.jdbc.repository.TacoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@SessionAttributes("order")
@RequestMapping("/design")
@AllArgsConstructor
public class DesignTacoController {
    private final IngredientRepository ingredientRepository;

    private final TacoRepository tacoRepository;

    @ModelAttribute(name = "order")
    public Order order(){
        return new Order();
    }

    @ModelAttribute(name = "otaco")
    public Taco taco(){
        return new Taco();
    }

    @GetMapping
    public String showDesignForm(Model model) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepository.findAll().forEach(ingredients::add);

        Ingredient.Type[] types = Ingredient.Type.values();
        for (Ingredient.Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }

        model.addAttribute("design", new Taco());
        return "design";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Ingredient.Type type) {
        return ingredients.stream().filter(x -> type.equals(x.getType())).collect(Collectors.toList());
    }

    @PostMapping
    public String processDesign(@Valid Taco design,Errors errors,@ModelAttribute Order order){
        if (errors.hasErrors()){
            return "design";
        }
        Taco saved = tacoRepository.save(design);
        return "redirect:/orders/current";
    }

    @PostMapping("/order")
    public String processOrder(@Valid Order order, Errors errors) {
        if (errors.hasErrors()) {
            return "orderForm";
        }
        log.info("Order submitted:" + order);
        return "redirect:/";
    }
}
