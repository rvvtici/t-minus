package compilador;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um nó da Árvore Sintática Abstrata (AST) da linguagem T-minus.
 *
 * Cada nó possui:
 *  - tipo    : identifica a regra gramatical ou terminal (ex: "prog", "cmdTransmitir", "id_var")
 *  - valor   : lexema concreto, preenchido apenas em nós folha (ex: "x", "42", "\"ola\"")
 *  - filhos  : lista de nós filhos, em ordem, representando os constituintes da regra
 */
public class Node {

    public final String tipo;
    public final String valor;           // null em nós internos
    public final List<Node> filhos;

    // ── Construtor para nós INTERNOS (regras gramaticais) ──────────────────────
    public Node(String tipo) {
        this.tipo   = tipo;
        this.valor  = null;
        this.filhos = new ArrayList<>();
    }

    // ── Construtor para nós FOLHA (terminais) ─────────────────────────────────
    public Node(String tipo, String valor) {
        this.tipo   = tipo;
        this.valor  = valor;
        this.filhos = new ArrayList<>();
    }

    // ── Adiciona um filho (ignora nulos – ε-produções não geram nós) ──────────
    public void add(Node filho) {
        if (filho != null) filhos.add(filho);
    }

    // ── Impressão identada da árvore (útil para debug) ────────────────────────
    public void imprimir(String prefixo, boolean ultimo) {
        String conector = ultimo ? "└── " : "├── ";
        String descricao = valor != null
                ? "[" + tipo + "] \"" + valor + "\""
                : "<" + tipo + ">";
        System.out.println(prefixo + conector + descricao);

        String novoPrefixo = prefixo + (ultimo ? "    " : "│   ");
        for (int i = 0; i < filhos.size(); i++) {
            filhos.get(i).imprimir(novoPrefixo, i == filhos.size() - 1);
        }
    }

    /** Atalho: imprime a árvore a partir da raiz. */
    public void imprimir() {
        System.out.println("<" + tipo + ">");
        for (int i = 0; i < filhos.size(); i++) {
            filhos.get(i).imprimir("", i == filhos.size() - 1);
        }
    }
}