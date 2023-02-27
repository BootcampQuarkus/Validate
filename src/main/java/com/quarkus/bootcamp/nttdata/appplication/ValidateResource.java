package com.quarkus.bootcamp.nttdata.appplication;

import com.quarkus.bootcamp.nttdata.domain.services.ValidateService;
import com.quarkus.bootcamp.nttdata.infraestruture.entity.Request;
import com.quarkus.bootcamp.nttdata.infraestruture.entity.customerWallet.CustomerD;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/validate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ValidateResource {
  @Inject
  ValidateService service;

  @POST
  public Uni<CustomerD> validate(Request request) {
    return service.validate(request);
  }
}
