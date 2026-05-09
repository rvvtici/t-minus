package compilador;

import java.util.List;

/**
 *
 * @author uniflferreira
 */
public class Compilador {

    public static void main(String[] args) {
        // String ajustada com todos os operadores da T-minus e um comentário no final
        String codigoTeste = "<< >> < > => ++ -- += -= *= /= + - * / % == != && || ! // testando tudo";

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