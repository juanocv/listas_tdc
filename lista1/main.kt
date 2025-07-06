import java.io.File
import java.io.PrintWriter
import java.util.BitSet
import java.util.StringTokenizer
import kotlin.system.exitProcess
import kotlin.time.measureTime

/* ---------- tipos básicos ---------- */
data class Edge(val id: Int, val u: Int, val v: Int)

/* ---------- parser DIMACS .col ---------- */
fun loadCol(path: String): List<Edge> {
    val edges = mutableListOf<Edge>()
    var idx = 0
    File(path).forEachLine { line ->
        when {
            line.isBlank() || line[0] == 'c' -> Unit                // comentário
            line.startsWith("p")              -> Unit                // cabeçalho
            line.startsWith("e")              -> {
                val st = StringTokenizer(line)
                st.nextToken()                                      // “e”
                val u = st.nextToken().toInt() - 1                  // 0-based
                val v = st.nextToken().toInt() - 1
                edges += Edge(idx++, u, v)
            }
        }
    }
    if (edges.isEmpty())
        { System.err.println("Sem arestas em $path"); exitProcess(1) }
    return edges
}

/* ---------- matriz de adjacência de arestas ---------- */
fun buildEdgeAdjacency(edges: List<Edge>): Array<BitSet> {
    val nE = edges.size
    val a  = Array(nE) { BitSet(nE) }
    for (i in 0 until nE)
        for (j in i + 1 until nE)
            if (edges[i].u == edges[j].u || edges[i].u == edges[j].v ||
                edges[i].v == edges[j].u || edges[i].v == edges[j].v) {
                a[i].set(j)
                a[j].set(i)
            }
    return a
}

/* grau da aresta i dentro do conjunto ainda não colorido ‘rem’ */
fun degree(i: Int, adj: Array<BitSet>, rem: BitSet): Int =
    (adj[i].clone() as BitSet).apply { and(rem) }.cardinality()

/* constrói de forma gulosa uma matriz-nula maximal contendo ‘seed’ */
fun maximalNull(seed: Int, adj: Array<BitSet>, rem: BitSet): Set<Int> {
    val s = mutableSetOf(seed)
    var j = rem.nextSetBit(0)
    while (j >= 0) {
        if (j != seed && s.all { !adj[j].get(it) }) s += j
        j = rem.nextSetBit(j + 1)
    }
    return s
}

/* ---------- programa principal ---------- */
fun main(args: Array<String>) {
    if (args.isEmpty()) {
    println("Uso: kotlin EdgeColorKt <instancia.col>")
    return
    }

    val inPath  = args[0]
    val edges   = loadCol(inPath)
    val adj     = buildEdgeAdjacency(edges)
    val nE      = edges.size

    val rem     = BitSet(nE).apply { set(0, nE) }        // ainda não coloridas
    val color   = IntArray(nE) { -1 }
    var nextCol = 0

    val duration = measureTime {
        while (!rem.isEmpty) {
            /* ---- passo 2(a): maior grau ---- */
            var maxDeg = -1
            val cand   = mutableListOf<Int>()
            var i = rem.nextSetBit(0)
            while (i >= 0) {
                val d = degree(i, adj, rem)
                when {
                    d > maxDeg -> { maxDeg = d; cand.clear(); cand += i }
                    d == maxDeg -> cand += i
                }
                i = rem.nextSetBit(i + 1)
            }

            /* ---- passo 2(b) & 3: maior matriz-nula + desempate ---- */
            var best: Set<Int> = emptySet()
            var bestSize = -1
            var bestDegSum = -1
            for (row in cand) {
                val s = maximalNull(row, adj, rem)
                val size   = s.size
                val degSum = s.sumOf { degree(it, adj, rem) }
                if (size > bestSize || (size == bestSize && degSum > bestDegSum)) {
                    best = s; bestSize = size; bestDegSum = degSum
                }
            }

            /* ---- passos 4 & 5: colorir e remover ---- */
            for (e in best) {
                color[e] = nextCol
                rem.clear(e)
            }
            nextCol++
        }
    }
        /* ---------- saída ---------- */
    val base   = File(inPath).nameWithoutExtension
    val outDir = File("results")
    if (!outDir.exists()) outDir.mkdirs()
    val outFile = File(outDir, "colouring_${base}.txt")                  
    PrintWriter(outFile).use { out ->
        out.println("# instancia: $base")
        out.println("# arestas: ${edges.size}")
        out.println("# cores usadas: $nextCol")
        out.println("# tempo total: ${duration.inWholeSeconds}s (${duration.inWholeMilliseconds}ms)")
        out.println("# formato: id_aresta  vertice_u  vertice_v  cor")
        for (e in edges)
            out.println("${e.id + 1} ${e.u + 1} ${e.v + 1} ${color[e.id]}")
    }
    println("> Resultado gravado em ${outFile.absolutePath}")
}
