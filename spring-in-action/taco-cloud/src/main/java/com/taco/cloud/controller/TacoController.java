//package com.taco.cloud.controller;
//
//import com.taco.cloud.dao.TacoRepository;
//import com.taco.cloud.entity.Taco;
//import com.taco.cloud.web.api.TacoResource;
//import com.taco.cloud.web.api.TacoResourceAssembler;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.hateoas.CollectionModel;
//import org.springframework.hateoas.EntityModel;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.List;
//
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
//
//@RestController
//@RequestMapping("/taco/model")
//public class TacoController {
//
//    @Resource
//    private TacoRepository tacoRepository;
//
//
//    @GetMapping("/recent")
//    public CollectionModel<TacoResource> recentTacos() {
//        PageRequest page = PageRequest.of(
//                0, 12, Sort.by("createdAt").descending());
//
//        List<Taco> tacos = tacoRepository.findAll(page).getContent();
//        CollectionModel<TacoResource> recentResources = new TacoResourceAssembler(TacoController.class, TacoResource.class).toCollectionModel(tacos);
//
//        recentResources.add(
//                linkTo(methodOn(TacoController.class).recentTacos())
//                        .withRel("recents"));
//        return recentResources;
//    }
//}
