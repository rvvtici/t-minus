package compilador;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");

        while (true) {

            StringBuilder codigo = new StringBuilder();

            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║       COMPILADOR T-MINUS ==> PASCAL      ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println("Digite seu código T-minus linha por linha.");
            System.out.println("Digite FIM para gerar Pascal.");
            System.out.println("──────────────────────────────────────────");

            while (scanner.hasNextLine()) {

                String linha = scanner.nextLine();

                if (linha.trim().equalsIgnoreCase("FIM")) {
                    break;
                }

                codigo.append(linha).append("\n");
            }

            System.out.println("──────────────────────────────────────────");

            try {
                List<Token> tokens = new Lexer(codigo.toString()).getTokens();
                Node raiz = new Parser(tokens).parseProg();

                System.out.println("PASCAL GERADO:");
                System.out.println("──────────────────────────────────────────");

                String pascal = new Gerador().gerar(raiz);
                System.out.println(pascal);

                System.out.println("\nDeseja visualizar a AST? (S/N)");

                String arvore = scanner.nextLine();

                if (arvore.equalsIgnoreCase("S")) {

                    System.out.println("\nÁRVORE SINTÁTICA (AST)");
                    System.out.println("──────────────────────────────────────────");

                    raiz.imprimir();
                }

            } catch (Exception e) {
                System.out.println("✗ ERRO: " + e.getMessage());
            }

            System.out.println("\nDeseja compilar outro código? (S/N)");

            String resposta = scanner.nextLine();

            if (!resposta.equalsIgnoreCase("S")) {
                System.out.println("Encerrando compilador... Obrigado por usar o T-Minus!");
                break;
            }
        }

        scanner.close();
    }
}