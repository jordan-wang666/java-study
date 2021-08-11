package com.taco.cloud.web.api;

import com.taco.cloud.entity.Taco;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class TacoResourceAssembler extends RepresentationModelAssemblerSupport<Taco, TacoResource> {
    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     *
     * @param controllerClass DesignTacoController {@literal DesignTacoController}.
     * @param resourceType    TacoResource {@literal TacoResource}.
     */
    public TacoResourceAssembler(Class<?> controllerClass, Class<TacoResource> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    protected TacoResource instantiateModel(Taco taco) {
        return new TacoResource(taco);
    }


    @Override
    public TacoResource toModel(Taco entity) {
        return createModelWithId(entity.getId(), entity);
    }
}
