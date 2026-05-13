package compilador;

import java.util.List;

public class Sintatico {
    // TESTE: Script completo da T-minus para testar o sistema inteiro (happy path)
    static void testar(String nome, String codigo) {
        System.out.println("──────────────────────────────────────────");
        System.out.println("TESTE: " + nome);
        System.out.println("──────────────────────────────────────────");
        try {
            List<Token> tokens = new Lexer(codigo).getTokens();
            Node raiz = new Parser(tokens).parseProg();
            raiz.imprimir();
            System.out.println("✓ OK\n");
        } catch (Exception e) {
            System.out.println("✗ ERRO: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {

        // 1. Nave vazia
        testar("Nave vazia",
            "acesso livre nave MISSAO << >>"
        );

        // 2. Transmitir string
        testar("Transmitir string",
            "acesso livre nave MISSAO <<\n" +
            "  transmitir<\"Ignicao iniciada\">\n" +
            ">>"
        );

        // 3. Declaração com atribuição
        testar("Declaração inteira",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade combustivel => 100\n" +
            ">>"
        );

        // 4. Declaração sem atribuição
        testar("Declaração sem valor",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade combustivel\n" +
            ">>"
        );

        // 5. Capturar com tipo
        testar("Capturar com tipo",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade combustivel => capturar\n" +
            ">>"
        );

        // 6. Capturar sem tipo
        testar("Capturar sem tipo",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade combustivel\n" +
            "  combustivel => capturar\n" +
            ">>"
        );

        // 7. Condicional simples
        testar("Trajeto simples",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade nivel => 30\n" +
            "  trajeto<nivel menor_que 50> <<\n" +
            "    transmitir<\"Nivel critico\">\n" +
            "  >>\n" +
            ">>"
        );

        // 8. Condicional com else (abortar)
        testar("Trajeto com abortar",
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

        // 9. Condicional com else if (recalcular)
        testar("Trajeto com recalcular",
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

        // 10. Expressão aritmética
        testar("Expressão aritmética",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade resultado => 2 + 3 * 4\n" +
            ">>"
        );

        // 11. Potência
        testar("Potência",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade resultado => 2 ** 8\n" +
            ">>"
        );

        // 12. Função com retorno
        testar("Função com retorno",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade dobrar<Unidade x> <<\n" +
            "    retornar x + x\n" +
            "  >>\n" +
            ">>"
        );

        // 13. Chamada de função
        testar("Chamada de função",
            "acesso livre nave MISSAO <<\n" +
            "  Unidade dobrar<Unidade x> <<\n" +
            "    retornar x + x\n" +
            "  >>\n" +
            "  Unidade resultado => dobrar<10>\n" +
            ">>"
        );

        // 14. Orbita (while)
        testar("Orbita",
            "acesso livre nave MISSAO <<\n" +
            "  orbita<Unidade i onde i menor_que 10> <<\n" +
            "    transmitir<i>\n" +
            "  >>\n" +
            ">>"
        );

        // 15. Percorrer (for)
        testar("Percorrer",
            "acesso livre nave MISSAO <<\n" +
            "  percorrer<Unidade de i 0 ate 10 com passo 1> <<\n" +
            "    transmitir<i>\n" +
            "  >>\n" +
            ">>"
        );
    }
}