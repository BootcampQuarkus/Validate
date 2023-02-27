package com.quarkus.bootcamp.nttdata.infraestruture.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
  protected String token;
  protected String userId;
}