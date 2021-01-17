package com.example.demo.web;

import com.example.demo.entity.Ingredient;
import com.example.demo.entity.Order;
import com.example.demo.entity.Taco;
import com.example.demo.entity.User;
import com.example.demo.jpa.repository.OrderRepository;
import com.example.demo.jpa.repository.TacoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
//produces属性，指明所有处理方法只会处理accect头信息包含"application/json"的请求，限制api只会生成jaon结果，还允许其他的控制器处理具有相同路径的请求
@RequestMapping(value = "/design", produces = "application/json")
@CrossOrigin(origins = "*")//允许跨域请求
public class DesignTacoController {
    private final OrderRepository orderRepository;
    private TacoRepository tacoRepository;

    @GetMapping
    public String showDesignForm(Model model) {
        List<Ingredient> ingredients = Arrays.asList(
                new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP),
                new Ingredient("COTO", "Corn Tortilla", Ingredient.Type.WRAP),
                new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN),
                new Ingredient("CARN", "Carnitas", Ingredient.Type.PROTEIN),
                new Ingredient("TMTO", "Deiced Tomatoes", Ingredient.Type.VEGGIES),
                new Ingredient("LETC", "Lettuce", Ingredient.Type.VEGGIES),
                new Ingredient("CHED", "Cheddar", Ingredient.Type.CHEESE),
                new Ingredient("JACK", "MONTERREY JACK", Ingredient.Type.CHEESE),
                new Ingredient("SLSA", "Salsa", Ingredient.Type.SAUCE),
                new Ingredient("SRCR", "Sour Cream", Ingredient.Type.SAUCE)
        );

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
    public String processDesign(@Valid @ModelAttribute("design") Taco design, Errors errors) {
        if (errors.hasErrors()) {
            return "design";
        }
        log.info("Processing design: " + design);
        return "redirect:/orders/current";
    }

    @PostMapping("/order")
    public String processOrder(@Valid Order order, Errors errors,
                               @AuthenticationPrincipal User user, SessionStatus sessionStatus) {
        if (errors.hasErrors()) {
            return "orderForm";
        }
        log.info("Order submitted:" + order);

        order.setUser(user);
        orderRepository.save(order);
        sessionStatus.setComplete();
        return "redirect:/";
    }

    @GetMapping("/recent")
    public Iterable<Taco> recentTacos() {
        PageRequest page = PageRequest.of(
                0, 12, Sort.by("createdAt").descending());
        return tacoRepository.findAll(page);
    }

    //ResponseEntity设置返回时侯的状态值
    @GetMapping("/id")
    public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
        Optional<Taco> optTaco = tacoRepository.findById(id);
        return optTaco.map(taco -> new ResponseEntity<>(taco, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    //consumes值用于指定输入
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Taco postTaco(@RequestBody Taco taco) {
        return tacoRepository.save(taco);
    }

    //put请求真正目的是执行大规模的替换操作，而不是更新，如果订单的某个属性被省略，则替换后数据库中存储的该属性为null
    @PutMapping
    public Order putOrder(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    //patch为局部更新，但@PatchMapping和@PutMapping只能指定这个方法实现什么请求，真正的实现需要自己手动实现
    @PatchMapping(path = "/{orderId}", consumes = "application/json")
    public Order patchOrder(@PathVariable("orderId") Long orderId,
                            @RequestBody Order order) {
        Order oldOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException("order is not exist"));
        if (oldOrder.getCcExpiration() != null) {
            oldOrder.setCcExpiration(order.getCcExpiration());
        }
        if (oldOrder.getCcCVV() != null) {
            oldOrder.setCcCVV(order.getCcCVV());
        }
        return orderRepository.save(oldOrder);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        try {
            orderRepository.deleteById(orderId);
        } catch (EmptyResultDataAccessException e) {
        }//捕获EmptyResultDataAccessException异常，但什么都不做
    }
}
