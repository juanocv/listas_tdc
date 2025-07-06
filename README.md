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

### Implementação
#### Visão geral
Em resumo, o programa:
1. Lê o grafo  
2. Monta a relação “arestas adjacentes”
3. Itera escolhendo arestas de alto grau, monta um conjunto independente máximo, colore-o
4. Repete até acabar
5. Grava resultado (+ tempo) em [/lista1/results](https://github.com/juanocv/listas_tdc/tree/main/lista1/results).
#### Complexidade
- Memória:
adj = E bitsets de E bits ⇒ O(E²) bits ≈ E²/8 bytes.
Em grafos de até alguns milhares de arestas cabe bem na RAM.
- Tempo:
Cada iteração percorre rem para graus e possivelmente todo rem em maximalNull, então O(E²) no pior caso.
#### Caminho
A implementação encontra-se no seguinte caminho deste repositório: [/lista1/main.kt](https://github.com/juanocv/listas_tdc/blob/main/lista1/main.kt)

### Execução
1. Realize o setup de um ambiente [Kotlin](https://kotlinlang.org/) em seu editor de código de preferência
2. Acesse a pasta onde deseja clonar o repositório atual `(cd ...)`
3. Clone-o via terminal utilizando `git clone https://github.com/juanocv/listas_tdc` ou descompacte o `.zip`
4. Faça o download das instâncias `.col` desejadas e mova-as para a pasta do repositório [listas_tdc/lista1/instances](https://github.com/juanocv/listas_tdc/tree/main/lista1/instances)
5. Compile via `kotlinc lista1/main.kt -include-runtime -d main.jar`
6. Execute via `java -jar lista1/main.jar instances/{nome_da_instancia}.col` substituindo `{nome_da_instancia}` pelo nome da instância `.col` desejada

### Resultados e Conclusões
#### Observações
1. Tempo cresce rapidamente com |E|²  
   O(E²) era a previsão teórica: para ~112 k arestas (dsjc500.9) o tempo já subiu para 2 ½ minutos, mesmo em JVM local otimizada.

2. Uso de memória explode na mesma proporção  

    | Instância                | Bits estimados     | Aproximação em RAM |
    | ------------------------ | ------------------ | ------------------ |
    | dsjc500.9 (112 k)        | ≈ 1,26 × 10¹⁰ bits | ~1,6 GB            |
    | **dsjc1000.5 (≈ 250 k)** | ≈ 6,25 × 10¹⁰ bits | **~7,8 GB**        |
    | **dsjc1000.9 (≈ 450 k)** | ≈ 2,0 × 10¹¹ bits  | **> 25 GB**        |

    **O heap padrão (-Xmx2G | -Xmx4G) não comporta 8–25 GB, por isso para instâncias de maior tamanho o erro _java.lang.OutOfMemoryError: Java heap space_.**

3. Densidade influencia mais que |V|  
    d = 0,1 gera bem menos arestas que d = 0,5 ou 0,9, por isso dsjc500.1 (n = 500) roda em menos de 1 s enquanto dsjc250.5 (n = 250, d = 0,5) já exige 1,3 s.
   
5. Número de cores acompanha a densidade  
    Para os grafos densos (d = 0,9) o algoritmo precisou de ~0,54 |V| cores (608 para n = 500). Para densidade 0,1 foram usadas só 77 cores, corroborando que o heurístico produz soluções     mais “apertadas” quando há menos conflitos.
   
#### Caminho
Após execução, o resultado de cada instância executada estará em [/lista1/results](https://github.com/juanocv/listas_tdc/tree/main/lista1/results) em um arquivo `.txt`
