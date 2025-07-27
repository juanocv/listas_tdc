"""
 Implementação de algoritmos de programacao dinamica
 para o problema da mochila 0-1 e guloso para o
 problema da mochila relaxado.
 Claudio N Meneses, 03/09/24 
 
 Pode-se escrever o problema da mochila 0-1 em programacao  
 linear binaria da seguinte maneira:  
   Definicao das variaveis:
      x_i = 1 se objeto i eh posto na mochila,
      x_i = 0 se objeto i nao eh posto na mochila.
   para i=1,2,...,n

   Modelo:         
   max   sum_{j=1}^{n} v_i x_i
   s.a.: sum_{j=1}^{n} w_i x_i <= C
         x_i eh {0,1}  para i=1,2,...,n

 Formula recursiva para computar o valor de uma solucao otima:  
   Casos base:
      f_0(w) = 0  para w=0,1,...,C
      f_i(0) = 0  para i=1,...,n
   Calculo de f_i(w),   para w=0,1,...,C e i=1,2,...,n:
      f_i(w) = max{ f_{i-1}(w), v_r + f_{i-1}(w - w_i) }
  
 Antes de calcular f_{i-1}(w - w_i), verifica-se primeiro
 se w >= w_i (isto eh, se ha espaco na mochila para o objeto i),
 Caso tenha espaco, se o objeto i for posto na mochila tem-se 
 um retorno no valor v_i.

 Abaixo voce verah duas versoes de implementacoes do algoritmo
 em programacao dinamica acima, a saber knapsack_matrix  
 knapsack_array. Na primeira, usa-se uma matriz de dimensao
 C x n para armazenar todos os calculos feitos durante uma 
 execucao do algoritmo. Na segunda, armazena-se apenas os maiores 
 valores computados em cada linha da matriz C x n. Ou seja, a 
 os valores da ultima coluna naquela matriz. Observe que para 
 calcular **apenas** o valor de uma solucao otima de uma dada instancia 
 do problema, os calculos feitos na segunda implementacao sao 
 suficientes.   
"""

#import time, os
import time

class Item:
    def __init__(self, index, value, weight):
        self.index = index
        self.value = value
        self.weight = weight
        self.ratio = value / weight

def fractional_knapsack(items, capacity):
    items.sort(key=lambda obj: obj.ratio, reverse=True)
    x = [0 for _ in range(len(items))]

    total_value = 0
    for i in items:
        if capacity >= i.weight:
            capacity -= i.weight
            total_value += i.value
            x[i.index]=1
        else:
            x[i.index]= capacity / i.weight
            total_value += i.value * x[i.index]
            break
    return total_value, x

# versao que mantem todos os valores de f_i(w) para w=0,1,...,C
# e i=1,2,...,n.  
def knapsack_matrix(n:int, prof, wt, C:int):
    K = [[0 for _ in range(n+1)] for _ in range(C + 1)]
    # print(K)

    for w in range(C + 1): 
        for i in range(n + 1):
            if i == 0 or w == 0: K[w][i] = 0
            elif wt[i-1] <= w:
                K[w][i] = max(K[w][i-1],
                              prof[i-1] + K[w-wt[i-1]][i-1])
            else: K[w][i] = K[w][i-1]
    
    # print('Implementacao com uma matriz de dimensao C x n.')        
    # print(K)
    return K[C][n]  # valor de uma solucao otima

# versao que mantem os maiores valores de f_i(w) para  
# w=0,1,...,C. Ou seja, esta versao armazena os maiores valores 
# que seriam computados em cada linha da matriz de dimensao 
# C x n, caso esta matriz fosse utilizada.
def knapsack_array(n:int, prof, wt, C:int):
    # K={}
    # for i in range(C+1): K[i] = 0
    K = [0 for _ in range(C+1)]
    
    for i in range(1, n+1):      
        for w in range(C, 0, -1):
            if wt[i-1] <= w:
                K[w] = max(K[w], K[w - wt[i-1]] + prof[i-1])
    
    # print('\nImplementacao com uma matriz de dimensao 1 x n.')
    # print(K)
    return K[C]   # valor de uma solucao otima

# def GetInstance(dir_problem_data:str, instance:str):
def GetInstance(instance_data:str):
    # get the full path to the directory a Python file is contained in
    # dir_path = os.path.dirname(os.path.realpath(__file__))
    # print(dir_path)
    # instance_data = dir_path + dir_problem_data + instance
    
    with open(instance_data, 'r') as f:
        # n = int(f.readline())
        a,b = f.readline().split() 
        n = int(a)
        C = int(b)
        profit = [0 for _ in range(n)]
        weight = [0 for _ in range(n)]
        for i in range(n):
            a,b = f.readline().split()
            profit[i] = int(a)
            weight[i] = int(b)
        # C = int(f.readline())
    return n,C,profit,weight

def main():
    # dir_problem_instances = '\Knapsack_problem_instances\\'
    
    instance = 'lista2/instances/Instancia_n4_C11.txt'
    with open('Results','at') as fp:
        n,C,profit,weight = GetInstance(instance)
        fp.write('\nProblema da Mochila\n{}\n'.format(instance))
        fp.write('n={}, C={}\n'.format(n,C))
        # fp.write('retorno:{}\n'.format(profit))
        # fp.write('peso:   {}\n'.format(weight))

        start_time = time.time()
        opt_sol_val_matrix = knapsack_matrix(n,profit,weight,C)
        end_time = time.time()
        elapsed_time_matrix = end_time - start_time
    
        start_time = time.time()
        opt_sol_val_array = knapsack_array(n,profit,weight,C)
        end_time = time.time()
        elapsed_time_array = end_time - start_time
        fp.write('Experimentos\n')
        fp.write('  Implementacao em PD com matriz: valor otimo = {}, tempo de cpu = {:.5f}s\n'.
            format(opt_sol_val_matrix,elapsed_time_matrix))
        fp.write('  Implementacao em PD com vetor:  valor otimo = {}, tempo de cpu = {:5.5f}s\n'.
            format(opt_sol_val_array,elapsed_time_array))
            
        # Fractional knapsack problem
        items = []
        for i in range(n):
            items.append(Item(i,profit[i],weight[i]))  
            # print(items[i].index, items[i].value,items[i].weight)
        start_time = time.time()
        opt_value_fracKnapsack, x = fractional_knapsack(items, C)  
        end_time = time.time()
        elapsed_time = end_time - start_time
        fp.write('  Mochila fracionaria:            valor otimo = {:.2f}, tempo de cpu = {:.5}s\n'.
            format(opt_value_fracKnapsack,elapsed_time))
        # fp.write('  solucao otima: {}'.format(x))
    
# driver code
if __name__ == '__main__':
    main()