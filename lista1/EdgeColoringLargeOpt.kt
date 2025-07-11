import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.util.BitSet
import kotlin.system.measureTimeMillis

data class Aresta(val u: Int, val v: Int)

fun lerArquivoCol(caminho: String): List<Aresta> {
    val arestas = mutableListOf<Aresta>()
    BufferedReader(FileReader(caminho)).useLines { linhas ->
        linhas.forEach { linha ->
            if (linha.startsWith("e")) {
                val tokens = linha.split(" ")
                if (tokens.size >= 3) {
                    val u = tokens[1].toInt() - 1
                    val v = tokens[2].toInt() - 1
                    arestas.add(Aresta(u, v))
                }
            }
        }
    }
    return arestas
}

fun buildIncidenceList(arestas: List<Aresta>, numVertices: Int): Array<IntArray> {
    val temp = Array(numVertices) { mutableListOf<Int>() }
    arestas.forEachIndexed { idx, e ->
        temp[e.u].add(idx)
        temp[e.v].add(idx)
    }
    return Array(numVertices) { temp[it].toIntArray() }
}

/* constroi lista de arquivos de um determinada extensão a partir de um diretório*/
fun listFiles(diretorio: String, extensao: String): List<String> {
    val dir = File(diretorio)
    if (!dir.isDirectory) {
        throw IllegalArgumentException("O caminho fornecido não é um diretório: $diretorio")
    }
    // Remova o ponto da extensão, se houver
    val ext = extensao.removePrefix(".")
    return dir.listFiles { file ->
        file.isFile && file.extension.equals(ext, ignoreCase = true)
    }?.map { it.absolutePath } ?: emptyList()
}

/* soluciona o problema de coloração de arestas*/
fun runFiles(arquivo: String): Unit {
    val base = File(arquivo).nameWithoutExtension
    val outDir = File("results")
    if (!outDir.exists()) outDir.mkdirs()
    val outFile = File(outDir, "${base}.txt")
    try {
        val arestas = lerArquivoCol(arquivo)
        val m = arestas.size
        val n = arestas.maxOf { maxOf(it.u, it.v) } + 1
        val incidence = buildIncidenceList(arestas, n)

        val cores = IntArray(m)
        val removido = BitSet(m)
        var corAtual = 1

        val tempoMs = measureTimeMillis {
            val grau = IntArray(m)
            val seen = BooleanArray(m)

            while (removido.cardinality() < m) {
                // 1. Calcula graus rapidamente sem HashSet
                for (i in 0 until m) {
                    if (!removido[i]) {
                        val (u, v) = arestas[i]
                        var count = 0
                        for (j in incidence[u]) if (!removido[j] && !seen[j]) { seen[j] = true; count++ }
                        for (j in incidence[v]) if (!removido[j] && !seen[j]) { seen[j] = true; count++ }
                        grau[i] = count
                        for (j in incidence[u]) seen[j] = false
                        for (j in incidence[v]) seen[j] = false
                    } else grau[i] = -1
                }

                val maxDeg = grau.maxOrNull() ?: break
                val candidatos = grau.withIndex().filter { it.value == maxDeg }.map { it.index }

                var bestGroup: List<Int> = emptyList()
                val usado = BooleanArray(n)

                for (seed in candidatos) {
                    val grupo = mutableListOf(seed)
                    usado.fill(false)
                    val (u0, v0) = arestas[seed]
                    usado[u0] = true
                    usado[v0] = true

                    for (j in 0 until m) {
                        if (!removido[j] && j != seed) {
                            val (u, v) = arestas[j]
                            if (!usado[u] && !usado[v]) {
                                grupo += j
                                usado[u] = true
                                usado[v] = true
                            }
                        }
                    }

                    if (grupo.size > bestGroup.size) {
                        bestGroup = grupo
                    }
                }

                for (idx in bestGroup) {
                    cores[idx] = corAtual
                    removido[idx] = true
                }
                corAtual++

                if (corAtual == 1 || corAtual % 10 == 0)
                    println("Cor: $corAtual, Arestas restantes: ${m - removido.cardinality()}")
            }
        }

        val chi = cores.maxOrNull() ?: 0
        val outDir = File("results/edgecoloringlargeopt")
        if (!outDir.exists()) outDir.mkdirs()
        val outFile = File(outDir, "${base}.txt")
        outFile.writeText("Arquivo: $base.col\nArestas: $m\nCores usadas: $chi\nTempo de execução: ${tempoMs} ms\n\n")
        arestas.forEachIndexed { i, a ->
            outFile.appendText("e ${a.u + 1} ${a.v + 1} cor ${cores[i]}\n")
        }
        println("> Resultado salvo em ${outFile.absolutePath}")
    }catch (e: Throwable) {
        PrintWriter(outFile).use { out ->
            out.println("# instancia: $base")
            out.println("# ERRO no processamento do arquivo: $arquivo")
            out.println("# Mensagem: ${e::class.simpleName}: ${e.message}")
        }
        println("> ERRO ao processar $arquivo: ${e::class.simpleName}: ${e.message}")
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Compilação e execução: kotlinc EdgeColoringLargeOpt.kt -include-runtime -d EdgeColoringLargeOpt.jar\n" +
        "Uso: java -jar EdgeColoringLargeOpt.jar <folder_instances>\n" +
        "Onde <folder_instances> é o diretório contendo os arquivos .col")
        return
    }
    val arquivos = listFiles(args[0], ".col")
    arquivos.forEach {
        runFiles(it)

    }
}
