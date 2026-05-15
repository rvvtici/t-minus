package compilador;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        StringBuilder codigo = new StringBuilder();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       COMPILADOR T-MINUS ==> PASCAL      ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("Digite seu código T-minus linha por linha.");
        System.out.println("Quando terminar, digite FIM em uma linha vazia.");
        System.out.println("──────────────────────────────────────────");

        while (scanner.hasNextLine()) {
          String linha = scanner.nextLine();
          if (linha.trim().equals("FIM") || linha.trim().equals("tree")) {
            System.out.println("──────────────────────────────────────────");
            try {
              List<Token> tokens = new Lexer(codigo.toString()).getTokens();
              Node raiz = new Parser(tokens).parseProg();
              if (linha.trim().equals("tree")) {
                System.out.println("ÁRVORE SINTÁTICA (AST)");
                System.out.println("──────────────────────────────────────────");
                raiz.imprimir();
              } else {
                System.out.println("PASCAL GERADO:");
                System.out.println("──────────────────────────────────────────");
                String pascal = new Gerador().gerar(raiz);
                System.out.println(pascal);
              }
            } catch (Exception e) {
              System.out.println("✗ ERRO: " + e.getMessage());
            }
          break;
        }
        codigo.append(linha).append("\n");
      }
        scanner.close();
    }
}
