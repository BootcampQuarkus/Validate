package com.quarkus.bootcamp.nttdata.domain.services;

import com.quarkus.bootcamp.nttdata.infraestruture.entity.Request;
import com.quarkus.bootcamp.nttdata.infraestruture.entity.customerWallet.CustomerD;
import com.quarkus.bootcamp.nttdata.infraestruture.resources.ICardBankApi;
import com.quarkus.bootcamp.nttdata.infraestruture.resources.ICardMultiChannelApi;
import com.quarkus.bootcamp.nttdata.infraestruture.resources.ICustomerWalletApi;
import com.quarkus.bootcamp.nttdata.infraestruture.resources.ITokenApi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ValidateService {
  @RestClient
  ICustomerWalletApi customerWalletApi;
  @RestClient
  ICardBankApi cardBankApi;
  @RestClient
  ICardMultiChannelApi cardMultiChannelApi;
  @RestClient
  ITokenApi tokenApi;

  public Uni<CustomerD> validate(Request request) {
    // Valida el token
    return tokenApi.getById(request.getToken()).flatMap(rt -> {
      // Si no hay token devuelve not found
      if (rt == null) {
        throw new NotFoundException("Token not found");
      }
      // Si al usuario no le pertenece el token devuelve not found
      if (!(rt.getValue().getUserId().equals(request.getUserId()))) {
        throw new NotFoundException("User not found");
      }
      // Busca la tarjeta en el multicanal
      Uni<Long> ProductId = cardMultiChannelApi.getAll().flatMap(cmc ->
            Uni.createFrom().item(cmc.stream()
                  // Filtra las tarjetas por el userId
                  .filter(p -> p.getUserId().equals(request.getUserId()))
                  .findFirst()
                  .get())
      ).flatMap(p ->
            // Busca la tarjeta en el banco
            cardBankApi.getAll(2L)
                  .flatMap(r ->
                        Uni.createFrom().item(r.stream()
                              // Filtra la tarjeta por serial
                              .filter(s -> s.getSerial().equals(p.getSerial()))
                              .findFirst()
                              .get()
                              // retorna el id del producto
                              .getProductId())
                  )
      );
      return ProductId.flatMap(p ->
            customerWalletApi.getById(rt.getValue().getCustomerWalletId())
                  .flatMap(q -> {
                    q.setCardId(p);
                    return customerWalletApi.update(rt.getValue().getCustomerWalletId(), q);
                  })
      );
    });
  }
}