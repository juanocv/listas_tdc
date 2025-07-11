import java.io.File
import java.io.PrintWriter
import java.util.StringTokenizer
import kotlin.system.measureNanoTime

data class Aresta(val u: Int, val v: Int)

fun lerArquivoCol(caminho: String): List<Aresta> {
    val arestas = mutableListOf<Aresta>()
    File(caminho).forEachLine { linha ->
        val st = StringTokenizer(linha)
        if (!st.hasMoreTokens()) return@forEachLine
        if (st.nextToken() == "e" && st.countTokens() >= 2) {
            val u = st.nextToken().toInt() - 1
            val v = st.nextToken().toInt() - 1
            arestas += Aresta(u, v)
        }
    }
    return arestas
}

fun buildIncidenceList(arestas: List<Aresta>): Array<IntArray> {
    val numVertices = arestas.maxOf { maxOf(it.u, it.v) } + 1
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
    val outDir = File("results/edgecoloring2")
    if (!outDir.exists()) outDir.mkdirs()
    val outFile = File(outDir, "${base}.txt")
    println("> Processando arquivo ${base}")
    try {
        val arestas = lerArquivoCol(arquivo)
        val incidence = buildIncidenceList(arestas)
        val m = arestas.size

        val rem = BooleanArray(m) { true }
        val cores = IntArray(m) { 0 }
        var corAtual = 1

        val tNs = measureNanoTime {
            while (rem.any { it }) {
                // 1) Selecionar aresta com maior grau
                var maxDeg = -1
                val cand = mutableListOf<Int>()

                val seen = BooleanArray(m)
                for (i in 0 until m) if (rem[i]) {
                    val e = arestas[i]
                    var deg = 0
                    for (j in incidence[e.u]) if (rem[j] && !seen[j]) {
                        seen[j] = true; deg++
                    }
                    for (j in incidence[e.v]) if (rem[j] && !seen[j]) {
                        seen[j] = true; deg++
                    }

                    seen.fill(false) // reset visto

                    if (deg > maxDeg) {
                        maxDeg = deg
                        cand.clear(); cand += i
                    } else if (deg == maxDeg) cand += i
                }

                // 2) Montar conjunto independente maximamente desconectado
                val bestGroup = mutableListOf<Int>()
                var bestSumDeg = -1
                for (seed in cand) {
                    val grupo = mutableListOf(seed)
                    val marcados = BooleanArray(m)

                    for (j in 0 until m) if (rem[j] && j != seed) {
                        val ej = arestas[j]
                        val conflitante = grupo.any { k ->
                            val ek = arestas[k]
                            ek.u == ej.u || ek.u == ej.v || ek.v == ej.u || ek.v == ej.v
                        }
                        if (!conflitante) grupo += j
                    }

                    var sumDeg = 0
                    for (idx in grupo) {
                        val e = arestas[idx]
                        for (j in incidence[e.u]) if (rem[j] && !marcados[j]) {
                            marcados[j] = true; sumDeg++
                        }
                        for (j in incidence[e.v]) if (rem[j] && !marcados[j]) {
                            marcados[j] = true; sumDeg++
                        }
                    }

                    if (grupo.size > bestGroup.size || (grupo.size == bestGroup.size && sumDeg > bestSumDeg)) {
                        bestGroup.clear(); bestGroup += grupo
                        bestSumDeg = sumDeg
                    }
                }

                // 3) Colore o conjunto com a cor atual
                for (idx in bestGroup) {
                    cores[idx] = corAtual
                    rem[idx] = false
                }
                corAtual++
            }
        }

        val tempoMs = tNs / 1_000_000
        val chi = cores.max()
        val outDir = File("results/edgecoloring2")
        if (!outDir.exists()) outDir.mkdirs()
        val outFile = File(outDir, "${base}.txt")
        outFile.writeText("Instância: $base\nArestas: $m\nCores: $chi\nTempo: ${tempoMs}ms\n\n")
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
    }catch (e: Exception) {
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
        println("Compilação e execução: kotlinc EdgeColoring2.kt -include-runtime -d EdgeColoring2.jar\n" +
            "Uso: java -jar EdgeColoring2.jar <folder_instances>\n" +
            "Onde <folder_instances> é o diretório contendo os arquivos .col")
        return
    }

    /*val caminho = args[0]*/
    val arquivos = listFiles(args[0], ".col")
    arquivos.forEach {
        runFiles(it)
    }
}