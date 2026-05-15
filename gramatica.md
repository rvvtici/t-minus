
# Gramática Completa do Analisador Sintático
## Estrutura Global
* Ponto de entrada: declaração de classe (nave) */
prog → mod_acesso 'nave' id_nave abre_transmissao bloco fecha_transmissao
/* Bloco de comandos */
bloco → cmd bloco | cmd
/* Tipos de comando */
cmd → cmdTransmitir | cmdCapturar | cmdDeclarar | cmdCondicao | cmdPercorrer | cmdOrbita | cmdFuncaoMain | cmdFuncao | cmdChamada | cmdRetorno

## Entrada e Saída de Dados
/* transmitir <expr> → System.out.println(...) */
cmdTransmitir → 'transmitir' abre_comando conteudo fecha_comando
conteudo → MENSAGEM | expr | MENSAGEM '+' expr
/* capturar → Scanner.next*() — tipo inferido da variável */
cmdCapturar → tipo id_var op_atrib 'capturar' | id_var op_atrib 'capturar

## Declaração e Atribuição de Variáveis
cmdDeclarar → ‘Unidade’ id_var op_atrib expr | 
‘Precisao’ id_var op_atrib expr | 
‘Estimativa’ id_var op_atrib expr | 
‘Mensagem’ id_var op_atrib MENSAGEM | 
‘Sinal’ id_var op_atrib lit_bool  |
‘Distancia’ id_var op_atrib expr |
‘Pulso’ id_var op_atrib MENSAGEM | 
‘Carga’ id_var op_atrib expr | 
‘Eco’ id_var op_atrib expr | 
tipo id_var 
tipo → 'Unidade' | 'Precisao' | 'Estimativa' | 'Distancia' | 'Sinal' | 'Mensagem' | 'Pulso' | 'Carga' | 'Eco'

## Controle de Fluxo
/* Condicional (if / else if / else) */
cmdCondicao → 'trajeto' abre_comando exprRel fecha_comando
abre_transmissao bloco fecha_transmissao cmdCondicaoElse
cmdCondicaoElse → 'recalcular' cmdCondicao | 'abortar' abre_transmissao
bloco fecha_transmissao | ε

/* Laço For (percorrer) */
cmdPercorrer → 'percorrer' abre_comando tipo ‘de’ id_var expr 'ate'
expr 'com passo' expr fecha_comando abre_transmissao bloco
fecha_transmissao

/* Laço While (orbita) */
cmdOrbita → 'orbita' abre_comando tipo id_var exprRel fecha_comando
abre_transmissao bloco fecha_transmissao


## Funções (missao)
/* Declaração: mod_acesso tipo missao id_var <params> << bloco >> */
cmdFuncaoMain → mod_acesso 'iniciar_missao' abre_comando params
fecha_comando
cmdFuncao → tipo id_var abre_comando params fecha_comando
abre_transmissao bloco fecha_transmissao
params → tipo id_var params_aux | ε
params_aux → ',' tipo id_var params_aux | ε

/* Chamada de função */
cmdChamada → id_var abre_comando args fecha_comando
args → expr args_aux | ε
args_aux → ',' expr args_aux | ε

/* Retorno */
cmdRetorno → 'retornar' expr | 'retornar' id_var | 'retornar'
lit_bool | 'retornar' MENSAGEM | 'retornar'


## Expressões Aritméticas
/* Precedência: ( ) e ** > * / % > + - */
expr → termo expr_linha
expr_linha → op_soma termo expr_linha | ε
termo → fator termo_linha
termo_linha → op_mult fator termo_linha | ε
fator → fator_base fator_pot
fator_pot → '**' fator | ε
fator_base → tipo_num | id_var | abre_comando expr
fecha_comando | '-' fator
tipo_num → NUM_INT | NUM_FLOAT | NUM_DOUBLE | NUM_SHORT |
NUM_BYTE | NUM_LONG


## Expressões Relacionais e Lógicas
/* Precedência: ! > && > || */
exprRel → termoLogico exprRel_linha
exprRel_linha → op_log_ou termoLogico exprRel_linha | ε
termoLogico → fatorLogico termoLogico_linha
termoLogico_linha → op_log_e fatorLogico termoLogico_linha | ε
fatorLogico → '!' exprComp | exprComp
exprComp → expr op_rel expr | expr
