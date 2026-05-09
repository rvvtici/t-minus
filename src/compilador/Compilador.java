package compilador;

import java.util.List;

public class Compilador {

    public static void main(String[] args) {
        // Testando os números e operadores juntos!
        String codigoTeste = "100 27.5 9.8f 9460730472580800L 127b 360s == 20.0";

        System.out.println("Iniciando a bateria de validação do Lexer...\n");

        Lexer lexer = new Lexer(codigoTeste);

        try {
            List<Token> tokens = lexer.getTokens();

            System.out.println("--- LISTA DE TOKENS RECONHECIDOS ---");
            for (Token token : tokens) {
                System.out.println(token);
            }
            System.out.println("------------------------------------");
            
        } catch (RuntimeException e) {
            System.err.println("Erro Léxico: " + e.getMessage());
        }
    }
}