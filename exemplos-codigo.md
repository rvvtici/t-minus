# Exemplos de Código e Tradução Equivalente 

## Exemplo com Main e Print
#### T-Minus
```
acesso_livre nave TESTE<<
	acesso_livre iniciar_missao <> <<
    	transmitir<"Missao iniciada">
 >>
>>
```
#### Pascal
```
program TESTE;

begin
    writeln("Missao iniciada");
end.
```

## Exemplo com Declaração de tipos e variáveis pt. 1
#### T-Minus
```
acesso_livre nave TESTE<<
Unidade combustivel => 100
Precisao velocidade => 27.5
Sinal Estavel => ativo
Sinal Instavel => inativo
Mensagem planetaDestino => “Marte”
Estimativa gravidade => 9.8
Distancia Estelar => 9460730472580800
Pulso codMissao => “A”
Carga nivelRadiacao => 127           
Eco anguloOrbital => 360
>>
```

#### Pascal
```
program TESTE;

var
  combustivel : integer;
  velocidade : real;
  Estavel : boolean;
  Instavel : boolean;
  planetaDestino : string;
  gravidade : real;
  Estelar : real;
  codMissao : string;
  nivelRadiacao : integer;
  anguloOrbital : real;

begin
  combustivel := 100;
  velocidade := 27.5;
  Estavel := true;
  Instavel := false;
  planetaDestino := "Marte";
  gravidade := 9.8;
  Estelar := 9460730472580800;
  codMissao := "A";
  nivelRadiacao := 127;
  anguloOrbital := 360;
end.
```

## Exemplo com Declaração de tipos e variáveis pt. 2
#### T-Minus
```
acesso_livre nave TESTE<<
Precisao calculo => combustivel * velocidade
Unidade resto => combustivel % 3
Precisao media => <combustivel + velocidade> / 2
Unidade decremento => combustivel - 10

Sinal maisRapido => velocidade maior_que 20.0
Sinal maisLento => velocidade menor_que 20.0
Sinal igualOuMaior => combustivel maior_igual_que 100
Sinal igualOuMenor => combustivel menor_igual_que 100
Sinal igual => combustivel ==> 100
Sinal diferente => combustivel != 100
Sinal missaoOk => sistemaEstavel && combustivel maior_que 50
Sinal alertaGeral => !sistemaEstavel || combustivel menor_que 10
>>
```

#### Pascal
```
program TESTE;

var
  calculo : real;
  resto : integer;
  media : real;
  decremento : integer;
  maisRapido : boolean;
  maisLento : boolean;
  igualOuMaior : boolean;
  igualOuMenor : boolean;
  igual : boolean;
  diferente : boolean;
  missaoOk : boolean;
  alertaGeral : boolean;

begin
  calculo := combustivel * velocidade;
  resto := combustivel % 3;
  media := (combustivel + velocidade) / 2;
  decremento := combustivel - 10;
  maisRapido := velocidade > 20.0;
  maisLento := velocidade < 20.0;
  igualOuMaior := combustivel >= 100;
  igualOuMenor := combustivel <= 100;
  igual := combustivel = 100;
  diferente := combustivel <> 100;
  missaoOk := sistemaEstavel and combustivel > 50;
  alertaGeral := not sistemaEstavel or combustivel < 10;
end.
```

## Exemplo com Leitura (scanf)
#### T-Minus
```
acesso_livre nave HAILMARY <<          
        Unidade combustivel => capturar
>>
```

#### Pascal
```
program HAILMARY;

var
  combustivel : integer;

begin
  readln(combustivel);
end.
```

## Exemplo com Expressão Aritmética
#### T-Minus
```
acesso_livre nave HAILMARY <<          
 Unidade resultado => 2 + 3 * 4
    	 transmitir<resultado>
>>
```

#### Pascal
```
program HAILMARY;

var
  resultado : integer;

begin
  resultado := 2 + 3 * 4;
  writeln(resultado);
end.
```

## Exemplo com Potência
#### T-Minus
```
acesso_livre nave HAILMARY <<
        Unidade resultado => 2 ** 8
        transmitir<resultado>
>>
```

#### Pascal
```
program HAILMARY;
uses Math;

var
  resultado : integer;

begin
  resultado := Power(2, 8);
  writeln(resultado);
end.
```

## Exemplo com Condicional (if / else if / else)
#### T-Minus
```
acesso_livre nave HAILMARY <<
            Unidade nivel => 30
            trajeto<nivel menor_que 20> <<
                 transmitir<"Critico">
           >>  recalcular trajeto<nivel menor_que 50> <<
            	transmitir<"Baixo">
            >> abortar <<
           		transmitir<"OK">
            >>
>>
```

#### Pascal

```
program HAILMARY;

var
  nivel : integer;

begin
  nivel := 30;
  if nivel < 20 then
  begin
    writeln("Critico");
  end
  else if nivel < 50 then
  begin
    writeln("Baixo");
  end
  else
  begin
    writeln("OK");
  end;
```

## Exemplo com While
#### T-Minus
```
acesso_livre nave HAILMARY <<
           orbita<Unidade i onde i menor_que 10> <<
          	    transmitir<i>
            >>
>>

#### Pascal
program HAILMARY;

var
  i : integer;

begin
  while i < 10 do
  begin
    writeln(i);
  end;
end.
```

## Exemplo com For (passo um)
#### T-Minus
```
acesso_livre nave HAILMARY <<
         	percorrer<Unidade de i 0 ate 10 com passo 1> <<
                 transmitir<i>
            >>
>>
```

#### Pascal
```
program HAILMARY;

var
  i : integer;

begin
  for i := 0 to 10 do
  begin
    writeln(i);
  end;
end.
```

## Exemplo com For (passo maior que um)
#### T-Minus
```
acesso_livre nave HAILMARY <<
         	percorrer<Unidade de i 0 ate 10 com passo 2> <<
                 transmitir<i>
            >>
>>
```

#### Pascal
```
program HAILMARY;

var
  i : integer;
  _passo_i : integer;

begin
  _passo_i := 2;
  i := 0;
  for i <= 10 do
  begin
    writeln(i);
    i := i + _passo_i;
  end;
end.
```


## Exemplo com Declaração de Função

#### T-Minus
```
acesso_livre nave HAILMARY <<
          Unidade dobrar<Unidade x> <<
            	retornar x + x
          >>
>>
```

#### Pascal
```
program HAILMARY;

  function dobrar(x : integer) : integer;
  begin
    Result := x + x;
    Exit;
  end;

begin
end.
```


## Exemplo com Chamada de Função
#### T-Minus
```
acesso_livre nave HAILMARY <<
           Unidade dobrar<Unidade x> <<
            	retornar x + x
            >>
            Unidade resultado => dobrar<10>
            transmitir<resultado>
>>
```

#### Pascal
```
program HAILMARY;

var
  resultado : integer;

  function dobrar(x : integer) : integer;
  begin
    Result := x + x;
    Exit;
  end;

begin
  resultado := dobrar(10);
  writeln(resultado);
end.
```
