package chesstastic.engine.entities

sealed class Move(val from: Coordinate, val to: Coordinate) {
    override fun equals(other: Any?): Boolean =
        other is Move && other.toString() == toString()

    override fun hashCode(): Int = toString().hashCode()

    companion object {
        fun parseMany(input: String, delimiter: String = ","): List<Move> {
            return input.split(delimiter).mapNotNull { parse(it) }
        }

        fun parse(input: String): Move? =
            listOfNotNull(
                    Basic.parse(input),
                    KingsideCastle.parse(input),
                    QueensideCastle.parse(input),
                    Promotion.parse(input),
                    EnPassant.parse(input)
            ).firstOrNull()
    }

    class Basic(from: Coordinate, to: Coordinate): Move(from, to) {
        override fun toString(): String =
                "${from.file}${from.rank}${to.file}${to.rank}"

        companion object {
            fun parse(input: String): Basic? {
                return if (input.length == 4)
                    Basic(
                            Coordinate(
                                    File.valueOf(input[0].toUpperCase().toString()),
                                    Rank.fromIndex(input[1].toInt() - 1) ?: throw Error("could not parse rank from $input")
                            ),
                            Coordinate(
                                    File.valueOf(input[2].toUpperCase().toString()),
                                    Rank.fromIndex(input[3].toInt() - 1) ?: throw Error("could not parse rank from $input")
                            )
                    )
                else null
            }
        }
    }

    class EnPassant(from: Coordinate, to: Coordinate): Move(from, to) {
        override fun toString(): String =
                "ep${from.file}${from.rank}${to.file}${to.rank}"

        val captured: Coordinate = Coordinate(to.file, from.rank)

        companion object {
            fun parse(input: String): EnPassant? {
                return if (input.startsWith("ep"))
                    Basic.parse(input.substring(2))?.let {
                        EnPassant(it.from, it.to)
                    }
                else null
            }
        }
    }

    abstract class Castle(from: Coordinate, to: Coordinate): Move(from, to) {
        abstract val rook: Basic
    }

    class KingsideCastle(val color: Color): Castle(from(color), to(color)) {
        override val rook: Basic = Basic(
                Coordinate(File.H, from.rank),
                Coordinate(File.F, from.rank)
        )

        override fun toString(): String =
                "kc${if (color == Color.Light) "l" else "d"}"

        companion object {
            fun from(color: Color): Coordinate {
                val rank = if (color == Color.Light) Rank._1 else Rank._8
                return Coordinate(File.E, rank)
            }
            fun to(color:Color): Coordinate {
                val rank = if (color == Color.Light) Rank._1 else Rank._8
                return Coordinate(File.G, rank)
            }

            fun parse(input: String): KingsideCastle? {
                return when(input) {
                    "kcl" -> KingsideCastle(Color.Light)
                    "kcd" -> KingsideCastle(Color.Dark)
                    else -> null
                }
            }
        }
    }

    class QueensideCastle(val color: Color): Castle(from(color), to(color)) {
        override val rook: Basic = Basic(
                Coordinate(File.A, from.rank),
                Coordinate(File.D, from.rank)
        )

        override fun toString(): String =
                "qc${if (color == Color.Light) "l" else "d"}"

        companion object {
            fun from(color: Color): Coordinate {
                val rank = if (color == Color.Light) Rank._1 else Rank._8
                return Coordinate(File.E, rank)
            }
            fun to(color:Color): Coordinate {
                val rank = if (color == Color.Light) Rank._1 else Rank._8
                return Coordinate(File.C, rank)
            }

            fun parse(input: String): QueensideCastle? {
                return when(input) {
                    "qcl" -> QueensideCastle(Color.Light)
                    "qcd" -> QueensideCastle(Color.Dark)
                    else -> null
                }
            }
        }
    }

    class Promotion(from: Coordinate, to: Coordinate, val promotion: Piece): Move(from, to) {
        val withKnight by lazy { Promotion(from, to, Knight(promotion.color)) }
        val withQueen by lazy { Promotion(from, to, Queen(promotion.color)) }

        override fun toString(): String {
            val pieceCode = if(promotion is Queen) "q" else "k"
            val colorCode = if(promotion.color == Color.Light) "l" else "d"
            return "p$colorCode$pieceCode${from.file}${from.rank}${to.file}${to.rank}"
        }

        companion object {
            fun parse(input: String): Promotion? {
                val promotionPiece = when {
                    input.startsWith("plq") -> Queen(Color.Light)
                    input.startsWith("pdq") -> Queen(Color.Dark)
                    input.startsWith("plk") -> Knight(Color.Light)
                    input.startsWith("pdk") -> Knight(Color.Dark)
                    else -> null
                }

                return promotionPiece?.let { piece ->
                    Basic.parse(input.substring(2))?.let {
                        Promotion(it.from, it.to, piece)
                    }
                }
            }
        }
    }
}

