package com.amazonaws.lambda.email.confirmation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Diogo Klein Classe para criar uma conexão com o banco de dados. Não pode ser instanciada.
 */
public class ConnectionFactory {

  // Constantes para acessar o banco de dados
  private static final String connectionURL = System.getenv("connectionURLDB");
  private static final String user = System.getenv("userDB");
  private static final String password = System.getenv("passwordDB");
  private static final String connectionPrefix = "jdbc:mysql://";

  /**
   * Método para retornar a conexão com o banco de dados
   * 
   * @return Uma instância da conexão com o banco de dados se ela foi bem sucedida, caso contrário
   *         retorna null.
   */
  public static Connection getConnection() {

    // Variável para retornar a conexão
    Connection conexao = null;

    // Realizar a conexão
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      conexao = DriverManager.getConnection(connectionPrefix + connectionURL, user, password);
    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return conexao;
  }

  private ConnectionFactory() {

  }
}
