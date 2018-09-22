package com.amazonaws.lambda.email.confirmation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBHandler {
  private static Connection connection = ConnectionFactory.getConnection();
  private static String campoID = System.getenv("campoIDBD");
  private static String campoSituacao = System.getenv("campoSituacaoBD");
  private static String tabelaUserBD = System.getenv("tabelaUserBD");
  private static String campoEmail = System.getenv("campoEmailBD");
  private static Integer situacaoEmailConfirmado =
      Integer.valueOf(System.getenv("situacaoEmailConfirmado"));
  private String id;
  private String email;
  private Integer situacao;
  private boolean usuarioExiste;

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public boolean existe() {
    return usuarioExiste;
  }

  public void confirmarEmail() {
    if (situacao < situacaoEmailConfirmado) {
      String sqlQuery = String.format("UPDATE %s SET %s = %s WHERE %s = %s;", tabelaUserBD,
          campoSituacao, situacaoEmailConfirmado, campoID, id);
      try {
        PreparedStatement statement = connection.prepareStatement(sqlQuery);
        statement.execute();
      } catch (Exception e) {
        System.out.println("Erro de conexão com BD ao atualizar status do usuário");
        System.out.println("Query: " + sqlQuery);
        e.printStackTrace();
        throw new RuntimeException("500");
      }
    } else {
      // Se o usuário já estiver confirmado, então retorna um erro de conflito
      throw new RuntimeException("409");
    }
  }

  DBHandler(String userEmail) {
    this.email = userEmail;

    // Busca os dados no banco de dados
    String sqlQuery = String.format("SELECT %s FROM %s WHERE %s = '%s';",
        campoID + ", " + campoSituacao, tabelaUserBD, campoEmail, userEmail);

    try {
      PreparedStatement statement = connection.prepareStatement(sqlQuery);
      ResultSet results = statement.executeQuery();
      usuarioExiste = results.next();
      if (usuarioExiste) {
        id = results.getString(campoID);
        situacao = results.getInt(campoSituacao);
      }
    } catch (Exception e) {
      System.out.println("Erro ao accesar banco de dados para verificar email");
      System.out.println("Query enviado: ");
      System.out.println(sqlQuery);
      e.printStackTrace();
    }
  }
}
