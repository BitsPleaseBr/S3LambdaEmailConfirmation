package com.amazonaws.lambda.email.confirmation;

import org.mindrot.jbcrypt.BCrypt;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<Input, Output> {

    @Override
    public Output handleRequest(Input input, Context context) {
        final String key = System.getenv("secretKey");
        Output out = new Output();
        context.getLogger().log("Input token: " + input.getToken());

        TokenParser parser = new TokenParser(input.getToken());
        
        // Confere se o email existe no BD
        String userEmail = parser.getUserEmail();
        DBHandler handler = new DBHandler(userEmail);
        if(!handler.existe()) {
          System.out.println("Email não confirmado por não existir no BD");
          System.out.println("Email: " + parser.getUserEmail());
          throw new RuntimeException("403");
        }
        // Confere se o hash do token é válido
        Boolean confirmado;
        String secret = parser.getUserEmail() + key;
        System.out.println(secret);
        try {
          confirmado = BCrypt.checkpw(secret, parser.getHash());
        } catch (Exception e) {
          System.out.println("Hash mal formatado na confirmação de e-mail");
          e.printStackTrace();
          confirmado = false;
        }
        if(!confirmado) {
          System.out.println("Email não confirmado por falha na conferência do token");
          System.out.println("Secret: " + secret);
          System.out.println("Hash: " + parser.getHash());
          throw new RuntimeException("403");
        }
        
        // Atualiza status no BD
        handler.confirmarEmail();
        
        // Monta a resposta do programa
        out.setMessage("Usuário confirmado");
        
        return out;
    }

}
