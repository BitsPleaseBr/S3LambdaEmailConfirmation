package com.amazonaws.lambda.email.confirmation;

public class TokenParser {
  private String email;
  private String hash;

  public String getUserEmail() {
    return email;
  }

  public String getHash() {
    return hash;
  }

  TokenParser(String token) {
    email = token.split(":")[0];
    hash = token.split(":")[1];
  }
}
