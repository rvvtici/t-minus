package compilador;

import java.util.List;

public class Lexico {

    public static void main(String[] args) {
    // Script completo da T-minus para testar o sistema inteiro
    String codigoTeste = " acesso livre nave APOLLO marte_destino iniciar_missao << >> < > => ++ -- += -= *= /= + - * / % maior_que menor_que maior_igual_que menor_igual_que == != && || ! transmitir capturar retornar trajeto recalcular abortar percorrer orbita iniciar_missao ativo inativo 9 9.2f 8.6 234567890l 67b 00008s Unidade Precisao Estimativa Distancia Eco Carga Pulso Sinal Mensagem \"Minha string de teste\" // Finalizando o lexico com sucesso";

        System.out.println("Iniciando a bateria final do Lexer T-minus...\n");

        Lexer lexer = new Lexer(codigoTeste);

        try {
            List<Token> tokens = lexer.getTokens();

            System.out.println("--- LISTA DE TOKENS RECONHECIDOS ---");
            for (Token token : tokens) {
                System.out.println(token);
            }
            System.out.println("------------------------------------");
            System.out.println("Lista de Tokens completa!");
            
        } catch (RuntimeException e) {
            System.err.println("Erro Lexico: " + e.getMessage());
        }
    }
}