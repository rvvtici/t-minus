package compilador;

import java.util.ArrayList;
import java.util.List;

/** GERADOR DE CÓDIGO: T-MINUS --> PASCAL
 * Uso:
 *   Gerador gen = new GeradorPascal();
 *   String pascal = gen.gerar(raiz);   // raiz = parser.parseProg()
 *   System.out.println(pascal);
 *
 * ── MAPEAMENTO T-MINUS → PASCAL ──────────────────────────────────
 *  T-minus          │ Pascal
 *  ─────────────────┼──────────────────────────────────────────────
 *  nave NOME { }    │ program NOME; begin end.
 *  Unidade          │ integer
 *  Precisao         │ real
 *  Estimativa       │ real
 *  Distancia        │ real
 *  Eco              │ real
 *  Carga            │ integer
 *  Pulso            │ string
 *  Sinal            │ boolean
 *  Mensagem         │ string
 *  transmitir(x)    │ writeln(x)
 *  capturar         │ readln(x)
 *  trajeto          │ if
 *  recalcular       │ else if
 *  abortar          │ else
 *  percorrer        │ for
 *  orbita           │ while
 *  iniciar_missao   │ begin (bloco principal)
 *  retornar         │ (result :=  ou  Exit)
 *  ativo / inativo  │ true / false
 *  **               │ Power(base, exp)   [usa unit Math]
 * ─────────────────────────────────────────────────────────────────
 *
 * Variáveis declaradas dentro de blocos são coletadas e emitidas
 * numa seção var..begin única por escopo (program ou function/procedure).
 */

public class Gerador {

    // Indentação
    private int nivel = 0;
    private static final String INDENT = "  "; // 2 espaços por nível

    // Controle de variáveis locais
    // Cada entrada da pilha representa as declarações do escopo atual.
    private final List<List<String>> pilhaVars = new ArrayList<>();

    // Flag: o programa usa Power()?
    private boolean usaPower = false;

    // ══════════════════════════════════════════════════════════════
    //  PONTO DE ENTRADA
    // ══════════════════════════════════════════════════════════════

    public String gerar(Node raiz) {
        if (!raiz.tipo.equals("prog")) {
            throw new RuntimeException("[Gerador] Raiz esperada: 'prog', encontrada: '" + raiz.tipo + "'");
        }
        return gerarProg(raiz);
    }

    // ══════════════════════════════════════════════════════════════
    //  UTILITÁRIOS DE TEXTO
    // ══════════════════════════════════════════════════════════════

    // Gera indentação para o nível atual
    private String ind() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nivel; i++) sb.append(INDENT);
        return sb.toString();
    }

    // Controle de nível de blocos para indentação
    private void entraNivel() { nivel++; }
    private void saiNivel()   { nivel--; }

    // Mapeia tipo T-minus → tipo Pascal.
    private String tipoPascal(String tipoTminus) {
        return switch (tipoTminus) {
            case "Unidade" -> "integer";
            case "Carga" -> "integer";
            case "Precisao" -> "real";
            case "Estimativa" -> "real";
            case "Distancia" -> "real";
            case "Eco" -> "real";
            case "Pulso" -> "string";
            case "Mensagem" -> "string";
            case "Sinal" -> "boolean";
            default -> tipoTminus;
        }; // fallback
    }

    // Converte literais booleanos T-minus → Pascal.
    private String litBool(String lex) {
        return switch (lex) {
            case "ativo" -> "true";
            case "inativo" -> "false";
            default -> lex;
        };
    }

    // Converte operadores relacionais textuais → Pascal.
    private String opRel(String lex) {
        return switch (lex) {
            case "maior_que" -> ">";
            case "menor_que" -> "<";
            case "maior_igual_que" -> ">=";
            case "menor_igual_que" -> "<=";
            case "==>" -> "=";
            case "!=" -> "<>";
            default -> lex;
        };
    }

    // Encontra o primeiro filho com o tipo dado, ou null.
    private Node filhoTipo(Node no, String tipo) {
        for (Node f : no.filhos) if (f.tipo.equals(tipo)) return f;
        return null;
    }

    // Pilha de escopos de variáveis
    private void entraEscopo() { pilhaVars.add(new ArrayList<>()); }
    private void saiEscopo()   { pilhaVars.remove(pilhaVars.size() - 1); }
    private void registraVar(String decl) {
        if (!pilhaVars.isEmpty())
            pilhaVars.get(pilhaVars.size() - 1).add(decl);
    }

    // Devolve as declarações do escopo atual, ou lista vazia se fora de escopo.
    private List<String> varsDoEscopo() {
        return pilhaVars.isEmpty()
                ? new ArrayList<>()
                : pilhaVars.get(pilhaVars.size() - 1);
    }

    // ══════════════════════════════════════════════════════════════
    //  PROGRAMA  (prog)
    // ══════════════════════════════════════════════════════════════

    // prog → mod_acesso 'nave' id_nave '{' bloco '}'
    //   Emite:
    //   program NOME;
    //   uses Math;          (se houver **)
    //   var ...;            (vars do escopo global)
    //   begin
    //     ...
    //   end.

    // O nó prog tem uma estrutura fixa, então acessamos os filhos por índice:
    private String gerarProg(Node no) {
        // id_nave é o 3º filho (índice 2)
        String nome = no.filhos.get(2).valor;
        Node bloco = no.filhos.get(4); // bloco é o 5º filho (índice 4)

        entraEscopo();

        // Separa funções dos demais comandos
        StringBuilder funcoes = new StringBuilder();
        StringBuilder corpo = new StringBuilder();

        entraNivel();
        for (Node cmd : bloco.filhos) {
            if (cmd.tipo.equals("cmdFuncao")) {
                funcoes.append(gerarFuncao(cmd));
            } else {
                corpo.append(gerarCmd(cmd));
            }
        }
        saiNivel();

        List<String> vars = new ArrayList<>(varsDoEscopo());
        saiEscopo();

        // Emite o programa completo
        StringBuilder sb = new StringBuilder();
        sb.append("program ").append(nome).append(";\n");
        if (usaPower) sb.append("uses Math;\n");
        sb.append("\n");
        if (!vars.isEmpty()) {
            sb.append("var\n");
            for (String v : vars) sb.append(INDENT).append(v).append(";\n");
            sb.append("\n");
        } if (funcoes.length() > 0) {
        sb.append(funcoes);
        }
        sb.append("begin\n");
        sb.append(corpo);
        sb.append("end.\n");
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════
    //  BLOCO
    // ══════════════════════════════════════════════════════════════

    // Gera todos os comandos do bloco, indentados.
    private String gerarBloco(Node no) {
        StringBuilder sb = new StringBuilder();
        entraNivel();
        for (Node cmd : no.filhos) {
            sb.append(gerarCmd(cmd));
        }
        saiNivel();
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════
    //  DISPATCHER DE COMANDOS
    // ══════════════════════════════════════════════════════════════

    // Cada tipo de comando tem um método gerador específico. O dispatcher identifica o tipo do comando e delega.
    private String gerarCmd(Node no) {
        return switch (no.tipo) {
            case "cmdTransmitir" -> gerarTransmitir(no);
            case "cmdCapturar" -> gerarCapturar(no);
            case "cmdDeclarar" -> gerarDeclarar(no);
            case "cmdAtribuicao" -> gerarAtribuicao(no);
            case "cmdCondicao" -> gerarCondicao(no);
            case "cmdPercorrer" -> gerarPercorrer(no);
            case "cmdOrbita" -> gerarOrbita(no);
            case "cmdFuncaoMain" -> gerarFuncaoMain(no);
            case "cmdFuncao" -> gerarFuncao(no);
            case "cmdChamada" -> gerarChamada(no) + ";\n";
            case "cmdRetorno" -> gerarRetorno(no);
            case "COMENTARIO" -> ind() + "{ " + no.valor.replace("//", "").trim() + " }\n";            
            default -> ind() + "{ ??? " + no.tipo + " }\n";
        };
    }

    // ══════════════════════════════════════════════════════════════
    //  TRANSMITIR  →  writeln(...)
    // ══════════════════════════════════════════════════════════════

    // filhos: [res_transmitir, abre_cmd, conteudo, fecha_cmd]
    private String gerarTransmitir(Node no) {
        Node conteudo = filhoTipo(no, "conteudo");
        return ind() + "writeln(" + gerarConteudo(conteudo) + ");\n";
    }

    // conteudo → MENSAGEM | expr | MENSAGEM '+' expr
    private String gerarConteudo(Node no) {
        StringBuilder sb = new StringBuilder();
        for (Node f : no.filhos) {
            if (f.tipo.equals("op_soma")) {
                sb.append(" + ");
            } else if (f.tipo.equals("MENSAGEM")) {
                sb.append(f.valor.replace("\"", "'")); // já vem com as aspas do lexer
            } else {
                sb.append(gerarExpr(f));
            }
        }
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════
    //  CAPTURAR  →  readln(...)
    // ══════════════════════════════════════════════════════════════

    // O nó cmdCapturar pode ter layouts diferentes dependendo de como foi construído pelo Parser. Buscamos o id_var.
    private String gerarCapturar(Node no) {
        String varName = null;
        String varTipo = null;

        for (Node f : no.filhos) {
            if (f.tipo.equals("id_var")) { varName = f.valor; break; }
            if (f.tipo.equals("tipo"))    varTipo = tipoPascal(f.valor);
        }
        if (varName == null) return ind() + "readln; { capturar sem variável }\n";
        if (varTipo != null) registraVar(varName + " : " + varTipo);

        return ind() + "readln(" + varName + ");\n";
    }

    // ══════════════════════════════════════════════════════════════
    //  DECLARAR  →  var (coletada) + atribuição opcional
    // ══════════════════════════════════════════════════════════════

    // cmdDeclarar → tipo id_var ( '=' valor )?
    // Em Pascal, declarações ficam na seção var antes do begin.
    // Aqui registramos a declaração e emitimos apenas a atribuição.
     
    private String gerarDeclarar(Node no) {
        // filhos[0] = tipo, filhos[1] = id_var, filhos[2?] = op_atrib, filhos[3?] = valor
        String tipo  = tipoPascal(no.filhos.get(0).valor);
        String nome  = no.filhos.get(1).valor;

        registraVar(nome + " : " + tipo);

        // Há atribuição?
        if (no.filhos.size() >= 4) {
            Node valor = no.filhos.get(3); // índice 3: depois de tipo, id, op_atrib
            return ind() + nome + " := " + gerarValor(valor) + ";\n";
        }
        return ""; // só declaração, sem atribuição inicial
    }

    private String gerarAtribuicao(Node no) {
        // filhos: [id_var, op_atrib, valor]
        String nome = no.filhos.get(0).valor;
        Node valor    = no.filhos.get(2);
        return ind() + nome + " := " + gerarValor(valor) + ";\n";
    }

    // Gera o valor de um inicializador (MENSAGEM, lit_bool, expr).
    private String gerarValor(Node no) {
        if (no.tipo.equals("MENSAGEM")) return no.valor.replace("\"", "'");
        if (no.tipo.equals("lit_bool")) return litBool(no.valor);
        if (no.tipo.equals("exprRel"))   return gerarExprRel(no);
        return gerarExpr(no);
    }

    // ══════════════════════════════════════════════════════════════
    //  CONDICIONAL  →  if / else if / else
    // ══════════════════════════════════════════════════════════════

    private String gerarCondicao(Node no) {
        // filhos: [res_cond, abre_cmd, exprRel, fecha_cmd,
        //          abre_transm, bloco, fecha_transm, cmdCondicaoElse?]
        Node exprRel = filhoTipo(no, "exprRel");
        Node bloco   = filhoTipo(no, "bloco");
        Node elseNo  = filhoTipo(no, "cmdCondicaoElse");

        StringBuilder sb = new StringBuilder();
        sb.append(ind()).append("if ").append(gerarExprRel(exprRel)).append(" then\n");
        sb.append(ind()).append("begin\n");
        sb.append(gerarBloco(bloco));
        sb.append(ind()).append("end");

        if (elseNo != null) {
            sb.append(gerarElse(elseNo));
        } else {
            sb.append(";\n");
        }
        return sb.toString();
    }

    private String gerarElse(Node no) {
        // filhos[0] = 'recalcular' ou 'abortar'
        // recalcular → else if (cmdCondicao)
        // abortar    → else (bloco)
        Node primeiroFilho = no.filhos.get(0);

        if (primeiroFilho.tipo.equals("res_recalcular")) {
            Node subCond = filhoTipo(no, "cmdCondicao");
            // Emite como 'else if ...' — reutilizamos gerarCondicao
            // mas precisamos prefixar com "else "
            String subStr = gerarCondicao(subCond);
            // Remove indentação inicial para colar após o end do if
            return "\n" + ind() + "else " + subStr.stripLeading();
        } else {
            // abortar → else begin ... end
            Node bloco = filhoTipo(no, "bloco");
            StringBuilder sb = new StringBuilder();
            sb.append("\n").append(ind()).append("else\n");
            sb.append(ind()).append("begin\n");
            sb.append(gerarBloco(bloco));
            sb.append(ind()).append("end;\n");
            return sb.toString();
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  PERCORRER  →  for
    // ══════════════════════════════════════════════════════════════

    // Pascal só suporta passo +1 ou -1 nativamente. Para passo arbitrário, geramos um while equivalente.
    // Estrutura dos filhos do nó cmdPercorrer (pelo Parser):
    // [res_percorrer, abre_cmd, tipo, id_var, exprInicio, exprFim, exprPasso,
    //  fecha_cmd, abre_transm, bloco, fecha_transm]
     
    // O Parser não adiciona os tokens "de"/"ate"/"com passo" como filhos
    // (consumeLexema não chama no.add). Então mapeamos por índice:
    //  0 = res_percorrer  1 = abre_cmd  2 = tipo  3 = id_var
    //  4 = exprInicio     5 = exprFim   6 = exprPasso
    //  7 = fecha_cmd      8 = abre_transm  9 = bloco  10 = fecha_transm

    private String gerarPercorrer(Node no) {
        Node noTipo     = no.filhos.get(2);
        Node noIdVar    = no.filhos.get(3);
        Node noInicio   = no.filhos.get(4);
        Node noFim      = no.filhos.get(5);
        Node noPasso    = no.filhos.get(6);
        Node bloco      = no.filhos.get(9);

        String tipo   = tipoPascal(noTipo.valor);
        String var    = noIdVar.valor;
        String inicio = gerarExpr(noInicio);
        String fim    = gerarExpr(noFim);
        String passo  = gerarExpr(noPasso);

        // Registra variável de controle
        registraVar(var + " : " + tipo);

        StringBuilder sb = new StringBuilder();

        // Passo simples: literal "1" → usa for nativo
        if (passo.equals("1")) {
            sb.append(ind()).append("for ").append(var)
              .append(" := ").append(inicio)
              .append(" to ").append(fim).append(" do\n");
            sb.append(ind()).append("begin\n");
            sb.append(gerarBloco(bloco));
            sb.append(ind()).append("end;\n");
        } else {
            // Passo arbitrário → while
            registraVar("_passo_" + var + " : " + tipo);
            sb.append(ind()).append("_passo_").append(var)
              .append(" := ").append(passo).append(";\n");
            sb.append(ind()).append(var).append(" := ").append(inicio).append(";\n");
            sb.append(ind()).append("while ").append(var)
              .append(" <= ").append(fim).append(" do\n");
            sb.append(ind()).append("begin\n");
            sb.append(gerarBloco(bloco));
            entraNivel();
            sb.append(ind()).append(var).append(" := ").append(var)
              .append(" + _passo_").append(var).append(";\n");
            saiNivel();
            sb.append(ind()).append("end;\n");
        }
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════
    //  ORBITA  →  while
    // ══════════════════════════════════════════════════════════════

    // filhos: [res_orbita, abre_cmd, tipo, id_var, exprRel, fecha_cmd,
    //          abre_transm, bloco, fecha_transm]
    
    private String gerarOrbita(Node no) {
        Node noTipo  = no.filhos.get(2);
        Node noIdVar = no.filhos.get(3);
        Node exprRel = no.filhos.get(4);
        Node bloco   = no.filhos.get(7);

        registraVar(noIdVar.valor + " : " + tipoPascal(noTipo.valor));

        StringBuilder sb = new StringBuilder();
        sb.append(ind()).append("while ").append(gerarExprRel(exprRel)).append(" do\n");
        sb.append(ind()).append("begin\n");
        sb.append(gerarBloco(bloco));
        sb.append(ind()).append("end;\n");
        return sb.toString();
    }
    // ══════════════════════════════════════════════════════════════
    //  FUNÇÕES
    // ══════════════════════════════════════════════════════════════

    // iniciar_missao → bloco principal (já dentro do begin..end do program). Apenas gera o bloco; não emite begin/end próprio.

    private String gerarFuncaoMain(Node no) {
        Node bloco = filhoTipo(no, "bloco");
        return gerarBloco(bloco);
    }

    // cmdFuncao → tipo id_var '(' params ')' '{' bloco '}'
    
    //Em Pascal:
    //   Se tipo != void → function nome(params) : tipo;
    //   Sempre          → procedure nome(params);  (aqui sempre function pois T-minus tem tipo)
    //
    // Variáveis locais são coletadas num escopo próprio.
    
    // filhos: [tipo, id_var, abre_cmd, params, fecha_cmd,
    //          abre_transm, bloco, fecha_transm]

    private String gerarFuncao(Node no) {
        String tipo   = tipoPascal(no.filhos.get(0).valor);
        String nome   = no.filhos.get(1).valor;
        Node params     = filhoTipo(no, "params");
        Node bloco      = filhoTipo(no, "bloco");

        entraEscopo();
        String corpo = gerarBloco(bloco);
        List<String> vars = new ArrayList<>(varsDoEscopo());
        saiEscopo();

        StringBuilder sb = new StringBuilder();
        sb.append(ind()).append("function ").append(nome)
          .append("(").append(gerarParams(params)).append(")")
          .append(" : ").append(tipo).append(";\n");
        if (!vars.isEmpty()) {
            sb.append(ind()).append("var\n");
            for (String v : vars)
                sb.append(ind()).append(INDENT).append(v).append(";\n");
        }
        sb.append(ind()).append("begin\n");
        sb.append(corpo);
        sb.append(ind()).append("end;\n\n");
        return sb.toString();
    }

    // params → tipo id_var (, tipo id_var)* | ε
    private String gerarParams(Node no) {
        if (no == null || no.filhos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        // filhos: [tipo, id_var, sep, tipo, id_var, ...]
        int i = 0;
        while (i < no.filhos.size()) {
            Node t = no.filhos.get(i);
            Node v = no.filhos.get(i + 1);
            if (sb.length() > 0) sb.append("; ");
            sb.append(v.valor).append(" : ").append(tipoPascal(t.valor));
            i += 2;
            // pula sep se existir
            if (i < no.filhos.size() && no.filhos.get(i).tipo.equals("sep")) i++;
        }
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════
    //  CHAMADA DE FUNÇÃO
    // ══════════════════════════════════════════════════════════════

    // Devolve a chamada como expressão (sem ';').
    private String gerarChamada(Node no) {
        String nome = no.filhos.get(0).valor;
        Node args     = filhoTipo(no, "args");
        return nome + "(" + gerarArgs(args) + ")";
    }

    private String gerarArgs(Node no) {
        if (no == null || no.filhos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Node f : no.filhos) {
            if (f.tipo.equals("sep")) { sb.append(", "); continue; }
            sb.append(gerarExpr(f));
        }
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════
    //  RETORNO
    // ══════════════════════════════════════════════════════════════

    //  Em Pascal, retorno de função → Result := valor; Exit;
    // retornar sem valor → Exit;
    
    private String gerarRetorno(Node no) {
        if (no.filhos.size() <= 1) {
            // só 'retornar' sem valor
            return ind() + "Exit;\n";
        }
        Node valor = no.filhos.get(1);
        String v;
        if (valor.tipo.equals("lit_bool"))  v = litBool(valor.valor);
        else if (valor.tipo.equals("MENSAGEM")) v = valor.valor.replace("\"", "'");
        else v = gerarExpr(valor);

        return ind() + "Result := " + v + ";\n" + ind() + "Exit;\n";
    }

    // ══════════════════════════════════════════════════════════════
    //  EXPRESSÕES ARITMÉTICAS
    // ══════════════════════════════════════════════════════════════

    private String gerarExpr(Node no) {
        if (no == null) return "";
        return switch (no.tipo) {
            case "expr" -> gerarExprInterno(no);
            case "termo" -> gerarTermo(no);
            case "fator" -> gerarFatorNo(no);
            case "fator_base" -> gerarFatorBase(no);
            case "tipo_num" -> no.valor;
            case "id_var" -> no.valor;
            case "MENSAGEM" -> no.valor;
            case "lit_bool" -> litBool(no.valor);
            case "cmdChamada" -> gerarChamada(no);
            default -> "{ expr? " + no.tipo + " }";
        }; // chamada dentro de expressão
    }

    private String gerarExprInterno(Node no) {
        // filhos: [termo, expr_linha?]
        String resultado = gerarTermo(no.filhos.get(0));
        if (no.filhos.size() > 1) {
            resultado += gerarExprLinha(no.filhos.get(1));
        }
        return resultado;
    }

    private String gerarExprLinha(Node no) {
        if (no == null) return "";
        String op    = no.filhos.get(0).valor;
        String termo = gerarTermo(no.filhos.get(1));
        String resto = (no.filhos.size() > 2) ? gerarExprLinha(no.filhos.get(2)) : "";
        return " " + op + " " + termo + resto;
    }

    private String gerarTermo(Node no) {
        String resultado = gerarFatorNo(no.filhos.get(0));
        if (no.filhos.size() > 1) {
            resultado += gerarTermoLinha(no.filhos.get(1));
        }
        return resultado;
    }

    private String gerarTermoLinha(Node no) {
        if (no == null) return "";
        String op    = no.filhos.get(0).valor;
        String fator = gerarFatorNo(no.filhos.get(1));
        String resto = (no.filhos.size() > 2) ? gerarTermoLinha(no.filhos.get(2)) : "";
        return " " + op + " " + fator + resto;
    }

    private String gerarFatorNo(Node no) {
        // filhos: [fator_base, fator_pot?]
        String base = gerarFatorBase(no.filhos.get(0));
        if (no.filhos.size() > 1 && no.filhos.get(1) != null) {
            return gerarFatorPot(base, no.filhos.get(1));
        }
        return base;
    }

    private String gerarFatorPot(String base, Node pot) {
        // fator_pot → '**' fator
        usaPower = true;
        String exp = gerarFatorNo(pot.filhos.get(1));
        return "Power(" + base + ", " + exp + ")";
    }

    private String gerarFatorBase(Node no) {
        if (no.tipo.equals("tipo_num")) return no.valor;
        if (no.tipo.equals("id_var"))   return no.valor;
        if (no.tipo.equals("MENSAGEM")) return no.valor;
        if (no.tipo.equals("lit_bool")) return litBool(no.valor);
        if (no.tipo.equals("cmdChamada")) return gerarChamada(no);

        // fator_base com filhos: '(' expr ')' OU '-' fator
        if (!no.filhos.isEmpty()) {
            Node primeiro = no.filhos.get(0);
            if (primeiro.tipo.equals("op_soma") && primeiro.valor.equals("-")) {
                return "-" + gerarFatorNo(no.filhos.get(1));
            }
            // parentesado
            if (primeiro.tipo.equals("abre_comando")) {
                return "(" + gerarExpr(no.filhos.get(1)) + ")";
            }
            // fallback: delega ao primeiro filho
            return gerarExpr(primeiro);
        }
        return "{ fator_base? }";
    }

    // ══════════════════════════════════════════════════════════════
    //  EXPRESSÕES RELACIONAIS / LÓGICAS
    // ══════════════════════════════════════════════════════════════

    private String gerarExprRel(Node no) {
        if (no == null) return "";
        // exprRel → termoLogico exprRel_linha?
        String resultado = gerarTermoLogico(no.filhos.get(0));
        if (no.filhos.size() > 1)
            resultado += gerarExprRelLinha(no.filhos.get(1));
        return resultado;
    }

    private String gerarExprRelLinha(Node no) {
        if (no == null) return "";
        // filhos: [op_log_ou, termoLogico, exprRel_linha?]
        String op    = "or";
        String termo = gerarTermoLogico(no.filhos.get(1));
        String resto = (no.filhos.size() > 2) ? gerarExprRelLinha(no.filhos.get(2)) : "";
        return " " + op + " " + termo + resto;
    }

    private String gerarTermoLogico(Node no) {
        String resultado = gerarFatorLogico(no.filhos.get(0));
        if (no.filhos.size() > 1)
            resultado += gerarTermoLogicoLinha(no.filhos.get(1));
        return resultado;
    }

    private String gerarTermoLogicoLinha(Node no) {
        if (no == null) return "";
        // filhos: [op_log_e, fatorLogico, termoLogico_linha?]
        String op    = "and";
        String fator = gerarFatorLogico(no.filhos.get(1));
        String resto = (no.filhos.size() > 2) ? gerarTermoLogicoLinha(no.filhos.get(2)) : "";
        return " " + op + " " + fator + resto;
    }

    private String gerarFatorLogico(Node no) {
        // se chegou um fatorLogico com !, tem op_neg + exprComp
        if (no.tipo.equals("fatorLogico")) {
            return "not " + gerarExprComp(no.filhos.get(1));
        }
        // senão, já é um exprComp direto
        return gerarExprComp(no);
    }

    private String gerarExprComp(Node no) {
        // exprComp → expr op_rel expr | expr
        String esq = gerarExpr(no.filhos.get(0));
        if (no.filhos.size() >= 3) {
            String op  = opRel(no.filhos.get(1).valor);
            String dir = gerarExpr(no.filhos.get(2));
            return esq + " " + op + " " + dir;
        }
        return esq;
    }
}
