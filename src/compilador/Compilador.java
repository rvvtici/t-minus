package compilador;

import java.util.List;

public class Compilador {

    public static void main(String[] args) {
        // Testando os números e operadores juntos!
        String codigoTeste = "acesso livre nave APOLO abortar_teste iniciar_missao maior_que menor_que maior_igual_que menor_igual_que transmitir capturar retornar trajeto recalcular abortar percorrer orbita iniciar_missao ativo inativo Unidade Precisao Estimativa Distancia Eco Carga Pulso Sinal Mensagem";
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