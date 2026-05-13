package compilador;

import java.util.List;

public class Main {

    static void compilar(String nome, String codigo) {
        System.out.println("══════════════════════════════════════════");
        System.out.println("TESTE: " + nome);
        System.out.println("──────────────────────────────────────────");
        System.out.println("T-MINUS:");
        System.out.println(codigo.trim());
        System.out.println("──────────────────────────────────────────");
        System.out.println("PASCAL:");
        try {
            List<Token> tokens = new Lexer(codigo).getTokens();
            Node raiz = new Parser(tokens).parseProg();
            String pascal = new Gerador().gerar(raiz);
            System.out.println(pascal);
        } catch (Exception e) {
            System.out.println("✗ ERRO: " + e.getMessage());
        }
        System.out.println();
    }

    public static void main(String[] args) {

        // 1. Nave vazia
        compilar("Nave vazia",
            "acesso livre nave MISSAO << >>"
        );

        // 2. Transmitir string
        compilar("Transmitir string",
            "acesso livre nave MISSAO <<\n" +
            "  transmitir<\"Ignicao iniciada\">\n" +
            ">>"
        );

        // 3. Declaração com atribuição
        compilar("Declaração com atribuição",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade combustivel => 100\n" +
            ">>"
        );

        // 4. Declaração de string e booleano
        compilar("Declaração string e booleano",
            "acesso livre nave MISSAO <<\n" +
            "  Mensagem msg => \"Decolagem\"\n" +
            "  Sinal pronto => ativo\n" +
            ">>"
        );

        // 5. Capturar com tipo
        compilar("Capturar com tipo",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade combustivel => capturar\n" +
            ">>"
        );

        // 6. Transmitir expressão aritmética
        compilar("Expressão aritmética",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade resultado => 2 + 3 * 4\n" +
            "  transmitir<resultado>\n" +
            ">>"
        );

        // 7. Potência
        compilar("Potência **",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade resultado => 2 ** 8\n" +
            "  transmitir<resultado>\n" +
            ">>"
        );

        // 8. Condicional simples
        compilar("Trajeto simples",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade nivel => 30\n" +
            "  trajeto<nivel menor_que 50> <<\n" +
            "    transmitir<\"Nivel critico\">\n" +
            "  >>\n" +
            ">>"
        );

        // 9. Condicional com abortar
        compilar("Trajeto com abortar",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade nivel => 30\n" +
            "  trajeto<nivel menor_que 50> <<\n" +
            "    transmitir<\"Nivel critico\">\n" +
            "  >>\n" +
            "  abortar <<\n" +
            "    transmitir<\"Nivel OK\">\n" +
            "  >>\n" +
            ">>"
        );

        // 10. Condicional com recalcular
        compilar("Trajeto com recalcular",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade nivel => 30\n" +
            "  trajeto<nivel menor_que 20> <<\n" +
            "    transmitir<\"Critico\">\n" +
            "  >>\n" +
            "  recalcular trajeto<nivel menor_que 50> <<\n" +
            "    transmitir<\"Baixo\">\n" +
            "  >>\n" +
            "  abortar <<\n" +
            "    transmitir<\"OK\">\n" +
            "  >>\n" +
            ">>"
        );

        // 11. Orbita (while)
        compilar("Orbita",
            "acesso livre nave MISSAO <<\n" +
            "  orbita<Unidade i onde i menor_que 10> <<\n" +
            "    transmitir<i>\n" +
            "  >>\n" +
            ">>"
        );

        // 12. Percorrer passo 1 (for nativo)
        compilar("Percorrer passo 1",
            "acesso livre nave MISSAO <<\n" +
            "  percorrer<Unidade de i 0 ate 10 com passo 1> <<\n" +
            "    transmitir<i>\n" +
            "  >>\n" +
            ">>"
        );

        // 13. Percorrer passo arbitrário (while gerado)
        compilar("Percorrer passo 2",
            "acesso livre nave MISSAO <<\n" +
            "  percorrer<Unidade de i 0 ate 10 com passo 2> <<\n" +
            "    transmitir<i>\n" +
            "  >>\n" +
            ">>"
        );

        // 14. Função com retorno
        compilar("Função com retorno",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade dobrar<Unidade x> <<\n" +
            "    retornar x + x\n" +
            "  >>\n" +
            ">>"
        );

        // 15. Chamada de função
        compilar("Chamada de função",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade dobrar<Unidade x> <<\n" +
            "    retornar x + x\n" +
            "  >>\n" +
            "  Unidade resultado => dobrar<10>\n" +
            "  transmitir<resultado>\n" +
            ">>"
        );
    }
}