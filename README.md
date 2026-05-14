# 🚀 T-minus

## Descrição da Linguagem
T-minus é uma linguagem de programação didática e experimental, inspirada em Java, com temática espacial. Sua narrativa é de modelar a execução de programas como missões espaciais, nas quais as classes representam naves e os métodos representam comandos ou etapas da missão. Desenvolvida como prova de conceito. Permite algoritmos básicos e simulações lógicas.

## Linguagem Base e Tradutora
A linguagem foi desenvolvida em Java e o compilador da linguagem foi pensado para tradução em Pascal.

## Como Executar o Compilador
```
cd src/compilador
javac *.java
cd ..
java compilador.Main
```

## Características da Linguagem Criada

- O programa é declarado como uma nave, que é o equivalente a uma classe em Java.
- O nome da classe deve ser escrito em letra maiúscula e ter no mínimo dois caracteres. Permite números no meio e fim.
acesso_livre serve como modificador de acesso, equivalente ao public do java. É o único modificador existente da linguagem (no momento).
- Blocos são delimitados por << e >> em vez das chaves.
- Parâmetros e condições são delimitados por < e > em vez dos parênteses. Em contrapartida, os operadores lógicos > e < de java são tratados por extenso (maior_que e menor_que).
- O método principal é declarado com iniciar_missao, equivalente a main do Java.
- T-minus não possui delimitador de fim de comando (;), a quebra de linha fica com esse papel
- Os operadores de atribuição e igualdade são tratados como setas (=> e ==>). Não existe = sozinho em T-minus.
- Há algumas palavras-chave no laço for e while (percorrer e orbita) que não são identificadas pela AST (servem apenas esteticamente para os comandos). Os lexemas são: de, ate, com e passo.

## Exemplos de Código e Tradução Equivalente 
#### Exemplo com Main e Print
```
//T-Minus [Entrada]
acesso_livre nave TESTE<<
	acesso_livre iniciar_missao <> <<
    	transmitir<"Missao iniciada">
 >>
>>

//Pascal [Saída]
program TESTE;
begin
    writeln("Missao iniciada");
end.

```

## Autoria
Desenvolvido por **Ana Lima**, **Luana De Almeida** e **Ravi Macedo**.

## Professor Responsável: Charles Henrique P Ferreira
