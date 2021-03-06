package chesstastic.engine.entities

sealed class Move(val from: Square, val to: Square) {

    class Basic(from: Square, to: Square): Move(from, to)

    class EnPassant(from: Square, to: Square): Move(from, to) {
        val captured: Square = Square(to.file, from.rank)
    }

    sealed class Castle(from: Square, to: Square): Move(from, to) {
        abstract val rookMove: Basic

        class Kingside(val color: Color) : Castle(from(color), to(color)) {
            override val rookMove: Basic = Basic(
                Square(File.H, from.rank),
                Square(File.F, from.rank)
            )

            companion object {
                fun from(color: Color): Square {
                    val rank = if (color == Color.Light) Rank._1 else Rank._8
                    return Square(File.E, rank)
                }

                fun to(color: Color): Square {
                    val rank = if (color == Color.Light) Rank._1 else Rank._8
                    return Square(File.G, rank)
                }
            }
        }

        class Queenside(val color: Color) : Castle(from(color), to(color)) {
            override val rookMove: Basic = Basic(
                Square(File.A, from.rank),
                Square(File.D, from.rank)
            )

            companion object {
                fun from(color: Color): Square {
                    val rank = if (color == Color.Light) Rank._1 else Rank._8
                    return Square(File.E, rank)
                }

                fun to(color: Color): Square {
                    val rank = if (color == Color.Light) Rank._1 else Rank._8
                    return Square(File.C, rank)
                }
            }
        }
    }

    class Promotion(from: Square, to: Square, val promotion: PieceKind): Move(from, to) {

        override fun toString(): String {
            val kind = if (promotion == PieceKind.Queen) "q" else "n"
            return super.toString() + kind
        }

        override fun equals(other: Any?): Boolean {
            return super.equals(other) || (promotion == PieceKind.Queen && other == Move.Basic(from, to))
        }
    }

    override fun equals(other: Any?): Boolean =
        other is Move && other.toString() == toString()

    override fun hashCode(): Int = toString().hashCode()

    override fun toString(): String =
        "${from.file}${from.rank}${to.file}${to.rank}".toLowerCase()

    companion object {
        fun parseMany(input: String): List<Move> {
            return input.split(",", " ").mapNotNull { parse(it) }
        }

        fun parse(input: String): Move? {
            val regex = Regex("""([a-h])([1-8])([a-h])([1-8])[qn]?""")
            val match = regex.matchEntire(input.toLowerCase().trim())
            return if (match != null) {
                val (fromFileIn, fromRankIn, toFileIn, toRankIn) = match.destructured
                val from = Square(
                    File.valueOf(fromFileIn.toUpperCase()),
                    Rank.valueOf("_$fromRankIn")
                )
                val to = Square(
                    File.valueOf(toFileIn.toUpperCase()),
                    Rank.valueOf("_$toRankIn")
                )
                when {
                    match.value.endsWith("q") -> Promotion(from, to, PieceKind.Queen)
                    match.value.endsWith("n") -> Promotion(from, to, PieceKind.Knight)
                    else -> Basic(from, to)
                }
            } else null
        }
    }
}



