package compilador;

import java.util.List;

/** ANALISADOR SINTÁTICO - Método: Descida Recursiva (LL)  
 *
 * Cada método parse() corresponde a uma regra da GLC e devolve um nó da AST. 
 * Produções vazias (ε) devolvem null — o método add() de Node ignora nulos, mantendo a árvore limpa.
 *
 * ── TOKENS ESPERADOS DO LEXER ────────────────────────────────────
 *  Tipo            | Exemplos de lexema
 *  ────────────────|───────────────────────────────────────────────
 *  mod_acesso      | "acesso livre"
 *  res_nave        | "nave"
 *  id_nave         | "MISSAO", "NAVE1"
 *  id_var          | "x", "velocidade"
 *  tipo            | "Unidade","Precisao","Estimativa","Distancia",
 *                  |  "Eco","Carga","Pulso","Sinal","Mensagem"
 *  res_iniciar     | "iniciar_missao"
 *  res_transmitir  | "transmitir"
 *  res_capturar    | "capturar"
 *  res_retorno     | "retornar"
 *  res_condicional | "trajeto"
 *  res_recalcular  | "recalcular"
 *  res_abortar     | "abortar"
 *  res_percorrer   | "percorrer"
 *  res_orbita      | "orbita"
 *  op_atrib        | "=>"  (gerado por OperadoresSimples)
 *  op_soma         | "+" | "-"
 *  op_mult         | "*" | "/" | "%"
 *  op_pot          | "**"
 *  op_rel          | "==>" | "!=" | "maior_que" | "menor_que" |
 *                  |  "maior_igual_que" | "menor_igual_que"
 *  op_log_e        | "&&"
 *  op_log_ou       | "||"
 *  op_neg          | "!"
 *  abre_transmissao| "{"
 *  fecha_transmissao| "}"
 *  abre_comando    | "("
 *  fecha_comando   | ")"
 *  sep             | ","
 *  MENSAGEM        | "\"texto\""
 *  NUM_INT         | "42"
 *  NUM_FLOAT       | "3.14"   (e variantes)
 *  NUM_DOUBLE      | ...
 *  NUM_SHORT       | ...
 *  NUM_BYTE        | ...
 *  NUM_LONG        | ...
 *  lit_bool        | "ativo" | "inativo"
 *  de              | "de"     (keyword do percorrer — veja nota)
 *  ate             | "ate"
 *  com_passo       | "com passo"
 *  EOF             | ""
 * ─────────────────────────────────────────────────────────────────
 */

public class Parser {

    // Estado interno
    private final List<Token> tokens;
    private int pos;          // índice do token corrente
    private Token corrente;   // token corrente (lookahead)

    // Tipos de token — centralizados para facilitar manutenção 
    private static final String MOD_ACESSO   = "mod_acesso";
    private static final String RES_NAVE     = "res_nave";
    private static final String ID_NAVE      = "id_nave";
    private static final String ID_VAR       = "id_var";
    private static final String TIPO         = "tipo";
    private static final String RES_INICIAR  = "res_iniciar";
    private static final String RES_TRANSM   = "res_transmitir";
    private static final String RES_CAPTURAR = "res_capturar";
    private static final String RES_RETORNO  = "res_retorno";
    private static final String RES_COND     = "res_condicional";
    private static final String RES_RECALC   = "res_recalcular";
    private static final String RES_ABORTAR  = "res_abortar";
    private static final String RES_PERCORR  = "res_percorrer";
    private static final String RES_ORBITA   = "res_orbita";
    private static final String OP_ATRIB     = "op_atrib";
    private static final String OP_SOMA      = "op_soma";
    private static final String OP_MULT      = "op_mult";
    private static final String OP_POT       = "op_pot";
    private static final String OP_REL       = "op_rel";
    private static final String OP_LOG_E     = "op_log_e";
    private static final String OP_LOG_OU    = "op_log_ou";
    private static final String OP_NEG       = "op_neg";
    private static final String ABRE_TRANSM  = "abre_transmissao";
    private static final String FECHA_TRANSM = "fecha_transmissao";
    private static final String ABRE_CMD     = "abre_comando";
    private static final String FECHA_CMD    = "fecha_comando";
    private static final String SEP          = "sep";
    private static final String MENSAGEM     = "MENSAGEM";
    private static final String NUM_INT      = "NUM_INT";
    private static final String NUM_FLOAT    = "NUM_FLOAT";
    private static final String NUM_DOUBLE   = "NUM_DOUBLE";
    private static final String NUM_SHORT    = "NUM_SHORT";
    private static final String NUM_BYTE     = "NUM_BYTE";
    private static final String NUM_LONG     = "NUM_LONG";
    private static final String LIT_BOOL     = "lit_bool";
    private static final String EOF          = "EOF";

    // Palavras-chave do percorrer e orbita
    private static final String KW_DE        = "res_de";   // lexema "de"
    private static final String KW_ATE       = "res_ate";   // lexema "ate"
    private static final String KW_COM = "res_com";   // lexema "com"
    private static final String KW_PASSO = "res_passo";   // lexema "passo"
    private static final String KW_ONDE = "res_onde";

    // Construtor 
    public Parser(List<Token> tokens) {
        this.tokens   = tokens;
        this.pos      = 0;
        this.corrente = tokens.get(0);
    }

    // ══════════════════════════════════════════════════════════════
    //  UTILITÁRIOS BASE
    // ══════════════════════════════════════════════════════════════

    // Avança para o próximo token e devolve o que foi consumido
    private Token avancar() {
        Token t = corrente;
        if (pos + 1 < tokens.size()) corrente = tokens.get(++pos);
        return t;
    }

    // Verifica se o token corrente tem o tipo esperado (sem consumir)
    private boolean verifica(String tipo) {
        return corrente.tipo.equals(tipo);
    }

    // Consome o token se o tipo bate e lança erro caso contrário. Devolve um nó folha com o token consumido
    private Node consome(String tipo) {
        if (!verifica(tipo)) {
            erro("Esperado <" + tipo + ">, encontrado <"
                    + corrente.tipo + ", " + corrente.lexema + ">");
        }
        Token t = avancar();
        return new Node(t.tipo, t.lexema);
    }

    // Lança um erro sintático com a mensagem dada, incluindo o número do token onde ocorreu (para debugging)
    private void erro(String msg) {
        throw new RuntimeException("[Parser] Erro sintático: " + msg
                + "  (token #" + pos + ")");
    }

    // ══════════════════════════════════════════════════════════════
    //  PONTO DE ENTRADA
    // ══════════════════════════════════════════════════════════════

    // prog → mod_acesso 'nave' id_nave '{' bloco '}'
    public Node parseProg() {
        Node no = new Node("prog");
        no.add(consome(MOD_ACESSO));
        no.add(consome(RES_NAVE));
        no.add(consome(ID_NAVE));
        no.add(consome(ABRE_TRANSM));
        no.add(parseBloco());
        no.add(consome(FECHA_TRANSM));
        consome(EOF);
        return no;
    }

    // ══════════════════════════════════════════════════════════════
    //  BLOCO DE COMANDOS
    // ══════════════════════════════════════════════════════════════

    // bloco → cmd bloco | cmd
    // Produz um nó "bloco" com N filhos — um por comando. Para quando encontrar '}' ou EOF.
    
    private Node parseBloco() {
        Node no = new Node("bloco");
        while (!verifica(FECHA_TRANSM) && !verifica(EOF)) {
            no.add(parseCmd());
        }
        return no;
    }

    // ══════════════════════════════════════════════════════════════
    //  COMANDO (dispatcher por lookahead)
    // ══════════════════════════════════════════════════════════════

    // cmd → cmdTransmitir | cmdCapturar | cmdDeclarar | cmdCondicao
    //      | cmdPercorrer | cmdOrbita | cmdFuncaoMain | cmdFuncao
    //      | cmdChamada   | cmdRetorno
     
    private Node parseCmd() {
        // ── retornar ──────────────────────────────────────────────
        if (verifica(RES_RETORNO))     return parseCmdRetorno();

        // ── transmitir ────────────────────────────────────────────
        if (verifica(RES_TRANSM))      return parseCmdTransmitir();

        // ── capturar  (tipo id = capturar  OU  id = capturar) ─────
        if (verifica(RES_CAPTURAR))    return parseCmdCapturar();

        // ── trajeto (if) ──────────────────────────────────────────
        if (verifica(RES_COND))        return parseCmdCondicao();

        // ── percorrer (for) ───────────────────────────────────────
        if (verifica(RES_PERCORR))     return parseCmdPercorrer();

        // ── orbita (while) ────────────────────────────────────────
        if (verifica(RES_ORBITA))      return parseCmdOrbita();

        // ── iniciar_missao (main) ─────────────────────────────────
        if (verifica(RES_INICIAR))     return parseCmdFuncaoMain();

        // ── tipo → declaração OU declaração de função ─────────────
        if (verifica(TIPO))            return parseTipoOuFuncao();

        // ── mod_acesso → iniciar_missao com modificador ───────────
        if (verifica(MOD_ACESSO))      return parseCmdFuncaoMainComMod();

        // ── id_var → chamada OU atribuição por capturar ───────────
        if (verifica(ID_VAR))          return parseCmdChamadaOuCapturar();

        erro("Comando inesperado: <" + corrente.tipo + ", " + corrente.lexema + ">");
        return null; // inalcançável
    }

    // ══════════════════════════════════════════════════════════════
    //  TRANSMITIR  (System.out.println)
    // ══════════════════════════════════════════════════════════════

    // cmdTransmitir → 'transmitir' '(' conteudo ')'   
    private Node parseCmdTransmitir() {
        Node no = new Node("cmdTransmitir");
        no.add(consome(RES_TRANSM));
        no.add(consome(ABRE_CMD));
        no.add(parseConteudo());
        no.add(consome(FECHA_CMD));
        return no;
    }

    // conteudo → MENSAGEM | expr | MENSAGEM '+' expr
    private Node parseConteudo() {
        Node no = new Node("conteudo");
        if (verifica(MENSAGEM)) {
            no.add(consome(MENSAGEM));
            // MENSAGEM '+' expr ?
            if (verifica(OP_SOMA) && corrente.lexema.equals("+")) {
                no.add(consome(OP_SOMA));
                no.add(parseExpr());
            }
        } else {
            no.add(parseExpr());
        }
        return no;
    }

    // ══════════════════════════════════════════════════════════════
    //  CAPTURAR  (Scanner)
    // ══════════════════════════════════════════════════════════════

    // cmdCapturar → tipo id_var '=' 'capturar'
    //             | id_var '=' 'capturar'
     
    // Esta regra é chamada:
    //  a) diretamente quando o lookahead já é 'capturar'
    //  b) após consumir tipo+id_var dentro de parseTipoOuFuncao
    //  c) após consumir id_var dentro de parseCmdChamadaOuCapturar
    //
    // O parser "por fora" resolve a ambiguidade
    //

    private Node parseCmdCapturar() {
        // chamado quando lookahead == RES_CAPTURAR (sem prefixo)
        Node no = new Node("cmdCapturar");
        no.add(consome(RES_CAPTURAR));
        return no;
    }

    // Monta nó cmdCapturar a partir de peças já consumidas (tipo ou id_var).
    private Node montaCmdCapturar(Node prefixo, Node idVar) {
        Node no = new Node("cmdCapturar");
        no.add(prefixo);     // tipo ou null
        no.add(idVar);       // id_var
        no.add(consome(RES_CAPTURAR));
        return no;
    }

    // ══════════════════════════════════════════════════════════════
    //  DECLARAR / FUNÇÃO  (começa com 'tipo')
    // ══════════════════════════════════════════════════════════════

    // Após ver 'tipo' no lookahead, decide entre:
    //   cmdDeclarar   →  tipo id_var '=' expr|MENSAGEM|lit_bool
    //   cmdDeclarar   →  tipo id_var  (sem atribuição)
    //   cmdCapturar   →  tipo id_var '=' 'capturar'
    //   cmdFuncao     →  tipo id_var '(' params ')' '{' bloco '}'
   
   private Node parseTipoOuFuncao() {
        Node noTipo  = consome(TIPO);
        Node noIdVar = consome(ID_VAR);

        if (verifica(ABRE_CMD)) {
            return montaCmdFuncao(noTipo, noIdVar);
        }

        if (verifica(OP_ATRIB)) {
            // capturar? — checa ANTES de consumir o =>
            Node noAtrib = consome(OP_ATRIB);
            if (verifica(RES_CAPTURAR)) {
                return montaCmdCapturar(noTipo, noIdVar); // montaCmdCapturar consome o capturar
            }
            Node no = new Node("cmdDeclarar");
            no.add(noTipo);
            no.add(noIdVar);
            no.add(noAtrib);
            no.add(parseValorInicializador(noTipo.valor));
            return no;
        }

        // sem atribuição
        Node no = new Node("cmdDeclarar");
        no.add(noTipo);
        no.add(noIdVar);
        return no;
    }

    // Valor após '=>' numa declaração:
    // MENSAGEM | lit_bool | expr    
    private Node parseValorInicializador(String tipo) {
        if (verifica(MENSAGEM))  return consome(MENSAGEM);
        if (verifica(LIT_BOOL))  return consome(LIT_BOOL);
        if (tipo.equals("Sinal")) return parseExprRel();
        return parseExpr();
    }

    // ══════════════════════════════════════════════════════════════
    //  CONDICIONAL  (trajeto / recalcular / abortar)
    // ══════════════════════════════════════════════════════════════

    // cmdCondicao → 'trajeto' '(' exprRel ')' '{' bloco '}' cmdCondicaoElse
    private Node parseCmdCondicao() {
        Node no = new Node("cmdCondicao");
        no.add(consome(RES_COND));
        no.add(consome(ABRE_CMD));
        no.add(parseExprRel());
        no.add(consome(FECHA_CMD));
        no.add(consome(ABRE_TRANSM));
        no.add(parseBloco());
        no.add(consome(FECHA_TRANSM));
        no.add(parseCmdCondicaoElse());
        return no;
    }

    // cmdCondicaoElse → 'recalcular' cmdCondicao
    //                 | 'abortar' '{' bloco '}'
    //                 | ε
    private Node parseCmdCondicaoElse() {
        if (verifica(RES_RECALC)) {
            Node no = new Node("cmdCondicaoElse");
            no.add(consome(RES_RECALC));
            no.add(parseCmdCondicao());
            return no;
        }
        if (verifica(RES_ABORTAR)) {
            Node no = new Node("cmdCondicaoElse");
            no.add(consome(RES_ABORTAR));
            no.add(consome(ABRE_TRANSM));
            no.add(parseBloco());
            no.add(consome(FECHA_TRANSM));
            return no;
        }
        return null; // ε
    }

    // ══════════════════════════════════════════════════════════════
    //  PERCORRER  (for)
    // ══════════════════════════════════════════════════════════════

    // cmdPercorrer → 'percorrer' '(' tipo 'de' id_var expr 'ate' expr 'com passo' expr ')' '{' bloco '}'
    // PONTO DE ATENÇÃO: "de", "ate" e "com" são lexemas do tipo id_var no Lexer atual.
    // Se o Lexer vier a emitir um tipo dedicado, trocar KW_DE/KW_ATE/KW_COM_PASSO.
    
    private Node parseCmdPercorrer() {
        Node no = new Node("cmdPercorrer");
        no.add(consome(RES_PERCORR));
        no.add(consome(ABRE_CMD));
        no.add(consome(TIPO));
        consome(KW_DE); 
        no.add(consome(ID_VAR));
        no.add(parseExpr());                  // valor inicial
        consome(KW_ATE);         
        no.add(parseExpr());                  // valor final
        consome(KW_COM);   
        consome(KW_PASSO); 
        no.add(parseExpr());                  // passo
        no.add(consome(FECHA_CMD));
        no.add(consome(ABRE_TRANSM));
        no.add(parseBloco());
        no.add(consome(FECHA_TRANSM));
        return no;
    }

    // ══════════════════════════════════════════════════════════════
    //  ORBITA  (while)
    // ══════════════════════════════════════════════════════════════

    // cmdOrbita → 'orbita' '(' tipo id_var exprRel ')' '{' bloco '}'
    private Node parseCmdOrbita() {
        Node no = new Node("cmdOrbita");
        no.add(consome(RES_ORBITA));
        no.add(consome(ABRE_CMD));
        no.add(consome(TIPO));
        no.add(consome(ID_VAR));
        consome(KW_ONDE);             
        no.add(parseExprRel());
        no.add(consome(FECHA_CMD));
        no.add(consome(ABRE_TRANSM));
        no.add(parseBloco());
        no.add(consome(FECHA_TRANSM));
        return no;
    }

    // ══════════════════════════════════════════════════════════════
    //  FUNÇÕES
    // ══════════════════════════════════════════════════════════════

    // cmdFuncaoMain → mod_acesso 'iniciar_missao' '(' params ')'
    // Versão com mod_acesso explícito no fluxo de parseCmd.
    
    private Node parseCmdFuncaoMainComMod() {
        Node no = new Node("cmdFuncaoMain");
        no.add(consome(MOD_ACESSO));
        no.add(consome(RES_INICIAR));
        no.add(consome(ABRE_CMD));
        no.add(parseParams());
        no.add(consome(FECHA_CMD));
        no.add(consome(ABRE_TRANSM));
        no.add(parseBloco());
        no.add(consome(FECHA_TRANSM));
        return no;
    }

    // versão sem mod_acesso (lookahead já é RES_INICIAR).
    private Node parseCmdFuncaoMain() {
        Node no = new Node("cmdFuncaoMain");
        no.add(consome(RES_INICIAR));
        no.add(consome(ABRE_CMD));
        no.add(parseParams());
        no.add(consome(FECHA_CMD));
        no.add(consome(ABRE_TRANSM));
        no.add(parseBloco());
        no.add(consome(FECHA_TRANSM));
        return no;
    }

    // cmdFuncao → tipo id_var '(' params ')' '{' bloco '}'
    // (tipo e id_var já foram consumidos por parseTipoOuFuncao)
    private Node montaCmdFuncao(Node noTipo, Node noIdVar) {
        Node no = new Node("cmdFuncao");
        no.add(noTipo);
        no.add(noIdVar);
        no.add(consome(ABRE_CMD));
        no.add(parseParams());
        no.add(consome(FECHA_CMD));
        no.add(consome(ABRE_TRANSM));
        no.add(parseBloco());
        no.add(consome(FECHA_TRANSM));
        return no;
    }

    // params → tipo id_var params_aux | ε
    // params_aux → ',' tipo id_var params_aux | ε
    private Node parseParams() {
        Node no = new Node("params");
        if (!verifica(TIPO)) return no; // ε — lista vazia
        no.add(consome(TIPO));
        no.add(consome(ID_VAR));
        while (verifica(SEP)) {
            no.add(consome(SEP));
            no.add(consome(TIPO));
            no.add(consome(ID_VAR));
        }
        return no;
    }

    // ══════════════════════════════════════════════════════════════
    //  CHAMADA DE FUNÇÃO / CAPTURAR por id_var
    // ══════════════════════════════════════════════════════════════

    // Chamado quando o lookahead é id_var. Pode ser:
    //   id_var '(' args ')'          → cmdChamada
    //   id_var '=' 'capturar'        → cmdCapturar (sem tipo)
    private Node parseCmdChamadaOuCapturar() {
        Node noId = consome(ID_VAR);

        if (verifica(ABRE_CMD)) {
            // Chamada de função
            Node no = new Node("cmdChamada");
            no.add(noId);
            no.add(consome(ABRE_CMD));
            no.add(parseArgs());
            no.add(consome(FECHA_CMD));
            return no;
        }

        if (verifica(OP_ATRIB)) {
            Node noAtrib = consome(OP_ATRIB);
            if (verifica(RES_CAPTURAR)) {
                return montaCmdCapturar(noId, null);
            }
            // Atribuição simples: id_var = expr  (extensão natural, não conflita)
            Node atr = new Node("cmdAtribuicao");
            atr.add(noId);
            atr.add(noAtrib);
            atr.add(parseValorInicializador(""));
            return atr;
        }

        erro("Esperado '(' ou '=' após id_var \"" + noId.valor + "\"");
        return null;
    }

    // args → expr args_aux | ε
    // args_aux → ',' expr args_aux | ε
    private Node parseArgs() {
        Node no = new Node("args");
        if (verifica(FECHA_CMD)) return no; // ε
        no.add(parseExpr());
        while (verifica(SEP)) {
            no.add(consome(SEP));
            no.add(parseExpr());
        }
        return no;
    }

    // ══════════════════════════════════════════════════════════════
    //  RETORNO
    // ══════════════════════════════════════════════════════════════

    // cmdRetorno → 'retornar' expr
    //            | 'retornar' id_var
    //            | 'retornar' lit_bool
    //            | 'retornar' MENSAGEM
    //            | 'retornar'
    
    private Node parseCmdRetorno() {
        Node no = new Node("cmdRetorno");
        no.add(consome(RES_RETORNO));

        if (verifica(LIT_BOOL)) {
            no.add(consome(LIT_BOOL));
        } else if (verifica(MENSAGEM)) {
            no.add(consome(MENSAGEM));
        } else if (!verifica(FECHA_TRANSM) && !verifica(EOF)) {
            // 'retornar expr' — expr já cobre id_var
            no.add(parseExpr());
        }
        return no;
    }

    // ══════════════════════════════════════════════════════════════
    //  EXPRESSÕES ARITMÉTICAS
    //  Precedência (menor → maior): + - → * / % → unário - → **
    // ══════════════════════════════════════════════════════════════

    // expr → termo expr_linha
    private Node parseExpr() {
        Node no = new Node("expr");
        no.add(parseTermo());
        no.add(parseExprLinha());
        return no;
    }

    // expr_linha → op_soma termo expr_linha | ε
    private Node parseExprLinha() {
        if (verifica(OP_SOMA)) {
            Node no = new Node("expr_linha");
            no.add(consome(OP_SOMA));
            no.add(parseTermo());
            no.add(parseExprLinha());
            return no;
        }
        return null; // ε
    }

    // termo → fator termo_linha
    private Node parseTermo() {
        Node no = new Node("termo");
        no.add(parseFator());
        no.add(parseTermoLinha());
        return no;
    }

    // termo_linha → op_mult fator termo_linha | ε
    private Node parseTermoLinha() {
        if (verifica(OP_MULT)) {
            Node no = new Node("termo_linha");
            no.add(consome(OP_MULT));
            no.add(parseFator());
            no.add(parseTermoLinha());
            return no;
        }
        return null; // ε
    }

    // fator → fator_base fator_pot
    private Node parseFator() {
        Node no = new Node("fator");
        no.add(parseFatorBase());
        no.add(parseFatorPot());
        return no;
    }

    // fator_pot → '**' fator | ε
    private Node parseFatorPot() {
        if (verifica(OP_POT)) {
            Node no = new Node("fator_pot");
            no.add(consome(OP_POT));
            no.add(parseFator());
            return no;
        }
        return null; // ε
    }

    // fator_base → tipo_num | id_var | '(' expr ')' | '-' fator
    private Node parseFatorBase() {
        if (isNumero()) {
            Node no = new Node("tipo_num", corrente.lexema);
            avancar();
            return no;
        }
        if (verifica(ID_VAR)) {
            Node noId = consome(ID_VAR);
            // chamada de função dentro de expressão?
            if (verifica(ABRE_CMD)) {
                Node no = new Node("cmdChamada");
                no.add(noId);
                no.add(consome(ABRE_CMD));
                no.add(parseArgs());
                no.add(consome(FECHA_CMD));
                return no;
            }
            return noId;
        }
        if (verifica(ABRE_CMD)) {
            Node no = new Node("fator_base");
            no.add(consome(ABRE_CMD));
            no.add(parseExpr());
            no.add(consome(FECHA_CMD));
            return no;
        }
        if (verifica(OP_SOMA) && corrente.lexema.equals("-")) {
            Node no = new Node("fator_base");
            no.add(consome(OP_SOMA));
            no.add(parseFator());
            return no;
        }
        erro("Fator inválido: <" + corrente.tipo + ", " + corrente.lexema + ">");
        return null;
    }

    private boolean isNumero() {
        return verifica(NUM_INT)   || verifica(NUM_FLOAT)  ||
               verifica(NUM_DOUBLE)|| verifica(NUM_SHORT)  ||
               verifica(NUM_BYTE)  || verifica(NUM_LONG);
    }

    // ══════════════════════════════════════════════════════════════
    //  EXPRESSÕES RELACIONAIS / LÓGICAS
    //  Precedência: ! > && > ||
    // ══════════════════════════════════════════════════════════════

    // exprRel → termoLogico exprRel_linha
    private Node parseExprRel() {
        Node no = new Node("exprRel");
        no.add(parseTermoLogico());
        no.add(parseExprRelLinha());
        return no;
    }

    // exprRel_linha → op_log_ou termoLogico exprRel_linha | ε
    private Node parseExprRelLinha() {
        if (verifica(OP_LOG_OU)) {
            Node no = new Node("exprRel_linha");
            no.add(consome(OP_LOG_OU));
            no.add(parseTermoLogico());
            no.add(parseExprRelLinha());
            return no;
        }
        return null; // ε
    }

    // termoLogico → fatorLogico termoLogico_linha
    private Node parseTermoLogico() {
        Node no = new Node("termoLogico");
        no.add(parseFatorLogico());
        no.add(parseTermoLogicoLinha());
        return no;
    }

    // termoLogico_linha → op_log_e fatorLogico termoLogico_linha | ε
    private Node parseTermoLogicoLinha() {
        if (verifica(OP_LOG_E)) {
            Node no = new Node("termoLogico_linha");
            no.add(consome(OP_LOG_E));
            no.add(parseFatorLogico());
            no.add(parseTermoLogicoLinha());
            return no;
        }
        return null; // ε
    }

    // fatorLogico → '!' exprComp | exprComp
    private Node parseFatorLogico() {
        if (verifica(OP_NEG)) {
            Node no = new Node("fatorLogico");
            no.add(consome(OP_NEG));
            no.add(parseExprComp());
            return no;
        }
        return parseExprComp();
    }

    // exprComp → expr op_rel expr | expr
    private Node parseExprComp() {
        Node no = new Node("exprComp");
        no.add(parseExpr());
        if (verifica(OP_REL)) {
            no.add(consome(OP_REL));
            no.add(parseExpr());
        }
        return no;
    }
}