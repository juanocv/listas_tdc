# Listas de Teoria da Computação
## Integrantes e RAs:
- Giovana Silva Reis Palma (24202510466)
- Juan Oliveira de Carvalho (24202520846)
- Natalia Emelianova (23202410176)
  
## Lista 1 - Problema de Coloração de Arestas (Edge Coloring Problem)
### Descrição do problema:
> Existem muitos algoritmos que resolvem (ou tentam resolver) instâncias do problema de coloração de arestas em grafos simples não dirigidos. No artigo intitulado [_“Edge Coloring of Graph using Adjacency Matrix, by C.Paul Shyni, T.Ramachandran and V.Vijayalakshmi, International Journal of Creative Research Thoughts, volume 11, issue 1, 2023”_](https://ijcrt.org/papers/IJCRT2301140.pdf), você encontrará a descrição de um algoritmo.

### Tarefas:
>Nesta lista de exercícios,<br>
• Implemente aquele algoritmo utilizando a linguagem de programação Kotlin e execute o código resultante sobre as instâncias _dsjc250.5_, _dsjc500.1_, _dsjc500.5_, _dsjc500.9_, _dsjc1000.1_, _sjc1000.5_, _dsjc1000.9_, _r250.5_, _r1000.1c_, _r1000.5_, _dsjr500.1c_, _dsjr500.5_, _le450.25c_, _le450.25d_, _flat300_28_0_, _flat1000_50_0_, _flat1000_60_0_, _flat1000_76_0_, _latin_square_, _C2000.5_ e _C4000.5_, que estão disponíveis em [DIMACS Graphs: Benchmark Instances and Best Upper Bounds](https://cedric.cnam.fr/~porumbed/graphs/).<br>
• Escreva uma análise dos seus experimentos computacionais. O seu texto deve conter no mínimo as seguintes seções:<br>
– definição do problema investigado (edge coloring problem);<br>
– descrição do algoritmo em termos de pseudocódigos;<br>
– descrições dos experimentos computacionais e análises dos resultados.

### Instâncias
Todas as instâncias descritas na subseção anterior podem ser baixadas em [DIMACS Graphs: Benchmark Instances and Best Upper Bounds](https://cedric.cnam.fr/~porumbed/graphs/)

### Implementações
#### Algoritmo 1: [EdgeColoring1.kt](https://github.com/juanocv/listas_tdc/tree/main/lista1/EdgeColoring1.kt) 
Este algoritmo implementa fielmente a heurística apresentada no [artigo](https://ijcrt.org/papers/IJCRT2301140.pdf), utilizando
uma matriz de adjacência entre arestas representada com a estrutura BitSet. Em
cada iteração, ele identifica o maior conjunto de arestas mutuamente não adjacentes
(matriz-nula maximal) com base em heurísticas de grau e soma de graus, e as colore
com a mesma cor. O algoritmo é eficiente para instâncias médias, mas o uso de
matriz quadrática limita sua escalabilidade.
#### Algoritmo 2: [EdgeColoring2.kt](https://github.com/juanocv/listas_tdc/tree/main/lista1/EdgeColoring2.kt) 
Esta versão substitui a matriz de adjacência por listas de incidência por vértice, o
que reduz o uso de memória e melhora a performance em instâncias maiores. O
grau das arestas é estimado dinamicamente com base nas incidências, e o conjunto
independente de arestas é construído de forma iterativa com verificação direta de
conflitos. Essa abordagem apresenta melhor desempenho para grafos esparsos e
médios.
#### Algoritmo 3: [EdgeColoringLargeOpt.kt](https://github.com/juanocv/listas_tdc/tree/main/lista1/EdgeColoringLargeOpt.kt)
O terceiro algoritmo foi projetado especificamente para lidar com instâncias grandes,
otimizando tanto o tempo quanto o consumo de memória. Ele também utiliza listas
de incidência, mas evita estruturas pesadas como HashSet, preferindo vetores simples
(IntArray, BooleanArray) com reuso eficiente. A verificação de adjacência e a
contagem de graus são otimizadas para escala, e o algoritmo imprime progresso
durante a execução.

### Execução
1. Realize o setup de um ambiente [Kotlin](https://kotlinlang.org/) em seu editor de código de preferência
2. Acesse a pasta onde deseja clonar o repositório atual `(cd ...)`
3. Clone-o via terminal utilizando:
   ```cmd
   git clone https://github.com/juanocv/listas_tdc
   ```
   Ou descompacte o `.zip`
4. Acesse o diretório correspondente a esta lista via:
   ```cmd
   cd lista1
   ```
5. Faça o download das instâncias `.col` desejadas e mova-as para a pasta do repositório [listas_tdc/lista1/instances](https://github.com/juanocv/listas_tdc/tree/main/lista1/instances)

6. Compile via: 
   ```cmd
   kotlinc {algoritmo_desejado}.kt -include-runtime -d {algoritmo_desejado}.jar
   ``` 
   Onde _{algoritmo_desejado}_ pode ser **EdgeColoring1**, **EdgeColoring2** ou **EdgeColoringLargeOpt**
7. Execute via: 
   ```cmd
   java -jar main.jar instances/{nome_da_instancia}.col
   ``` 
   Substituindo `{nome_da_instancia}` pelo nome da instância `.col` desejada

### Resultados e Conclusões
#### Ambiente de execução
- **Processador:** AMD Ryzen 9 5950X 16-Core Processor
- **Memória RAM:** 64 GB
- **Sistema operacional:** Linux Mint Mate 22 (64 bits)
- **Placa-mãe:** ASUSTeK COMPUTER INC. ROG STRIX B550-F GAMING WIFI II Rev X.0x

#### Tabela dos resultados
| Instância        | Arestas  | Alg. 1 Cores | Alg. 1 Tempo (ms) | Alg. 2 Cores | Alg. 2 Tempo (ms) | Alg. 3 Cores | Alg. 3 Tempo (ms) |
|------------------|----------|--------------|-------------------|--------------|-------------------|--------------|-------------------|
| dsjc250.5        | 15.668   | 175          | 971               | 177          | 1028              | 177          | 1031              |
| dsjc500.1        | 12.458   | 77           | 434               | 78           | 887               | 78           | 943               |
| dsjc500.5        | 62.624   | 348          | 19572             | 350          | 14680             | 350          | 8788              |
| dsjc500.9        | 112.437  | 608          | 112471            | 609          | 106178            | 609          | 50867             |
| dsjc1000.1       | 49.629   | 147          | 6955              | 147          | 6576              | 147          | 1614              |
| dsjc1000.5       | 249.826  | 693          | 432119            | 694          | 315469            | 694          | 156156            |
| dsjc1000.9       | 449.449  | —            | —                 | —            | —                 | 1268         | 16467909          |
| r250.5           | 14.849   | 192          | 901               | 193          | 1230              | 193          | 1192              |
| r1000.1c         | 485.090  | —            | —                 | —            | —                 | 1346         | 1095391           |
| r1000.5          | 238.267  | 819          | 410010            | 821          | 302625            | 821          | 123661            |
| dsjr500.1c       | 121.275  | 642          | 148603            | 644          | 109946            | 644          | 64236             |
| dsjr500.5        | 58.862   | 388          | 19011             | 388          | 15802             | 388          | 8976              |
| le450_25c        | 17.343   | 179          | 780               | 179          | 1322              | 179          | 902               |
| le450_25d        | 17.425   | 157          | 845               | 157          | 890               | 157          | 915               |
| flat300_28_0     | 21.695   | 205          | 1944              | 205          | 2040              | 205          | 1425              |
| flat1000_50_0    | 245.000  | 672          | 444510            | 672          | 431221            | 672          | 97838             |
| flat1000_60_0    | 245.830  | 692          | 474002            | 694          | 376935            | 694          | 148936            |
| flat1000_76_0    | 246.708  | 684          | 448098            | 686          | 362409            | 686          | 147846            |
| latin_square     | 307.350  | —            | —                 | —            | —                 | 906          | 2686259           |
| C2000.5          | 999.836  | —            | —                 | —            | —                 | 1403         | 24164346          |
| C4000.5          | 4.000.268| —            | —                 | —            | —                 | 2809         | 28300197          |


#### Parecer geral
Os resultados obtidos demonstram que o algoritmo **EdgeColoringLargeOpt.kt** 
foi o mais robusto, conseguindo processar todas as instâncias, inclusive as de
maior porte, como _C4000.5.col_, com mais de 4 milhões de arestas. Por outro
lado, o **EdgeColoring1.kt** se mostrou mais eficiente em termos de tempo
de processamento para instâncias de até ˜ 22 mil arestas, mas enfrentou limitações
em instâncias grandes devido ao alto consumo de memória, não sendo capaz de
processar as instâncias com mais de 30 mil arestas e apresentando tempo maior de
processamento em relação aos outros algoritmos para instâncias grandes.
   
#### Caminho dos resultados
Após execução, o resultado, em arquivo `.txt`, de cada instância executada, estará em:
- EdgeColoring1: [/lista1/results/edgecoloring1](https://github.com/juanocv/listas_tdc/tree/main/lista1/results/edgecoloring1)
- EdgeColoring2: [/lista1/results/edgecoloring2](https://github.com/juanocv/listas_tdc/tree/main/lista1/results/edgecoloring2)
- EdgeColoringLargeOpt: [/lista1/results/edgecoloring2](https://github.com/juanocv/listas_tdc/tree/main/lista1/results/edgecoloringlargeopt) 
