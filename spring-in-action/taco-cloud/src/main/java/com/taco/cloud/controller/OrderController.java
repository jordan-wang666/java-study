//package com.taco.cloud.controller;
//
//import com.taco.cloud.dao.OrderRepository;
//import com.taco.cloud.entity.TacoOrder;
//import com.taco.cloud.entity.security.User;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.SessionAttributes;
//import org.springframework.web.bind.support.SessionStatus;
//
//import javax.validation.Valid;
//
//@Slf4j
//@Controller
//@RequestMapping("/orders")
//@SessionAttributes("tacoOrder")
//public class OrderController {
//
//    private OrderRepository orderRepo;
//
//    public OrderController(OrderRepository orderRepo) {
//        this.orderRepo = orderRepo;
//    }
//
//    @GetMapping("/current")
//    public String orderForm(Model model) {
//        model.addAttribute("tacoOrder", new TacoOrder());
//        return "orderForm";
//    }
//
//    @PostMapping
//    public String processOrder(@Valid TacoOrder order, Errors errors, SessionStatus sessionStatus,
//                               @AuthenticationPrincipal User user) {
//        if (errors.hasErrors()) {
//            log.info("Errors:{}", errors);
//            return "orderForm";
//        }
//
//        order.setUser(user);
//        orderRepo.save(order);
//        sessionStatus.setComplete();
//
//        log.info("Order submitted: " + order);
//        return "redirect:/";
//    }
//}
