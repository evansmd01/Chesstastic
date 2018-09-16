package chesstastic.engine.entities

sealed class Move(val from: Coordinate, val to: Coordinate) {
    companion object {
        fun parse(input: String): Move? =
            listOfNotNull(
                    BasicMove.parse(input),
                    EnPassantMove.parse(input),
                    CastleMove.parse(input),
                    PawnPromotionMove.parse(input)
            ).firstOrNull()
    }
}

class BasicMove(from: Coordinate, to: Coordinate): Move(from, to) {
    override fun toString(): String =
            "m${from.file}${from.rank}${to.file}${to.rank}"

    companion object {
        fun parse(input: String): BasicMove? {
            return if (input.startsWith("m"))
                BasicMove(
                    Coordinate(
                        File.valueOf(input[1].toUpperCase().toString()),
                        Rank.fromIndex(input[2].toInt() - 1)!!
                    ),
                    Coordinate(
                        File.valueOf(input[3].toUpperCase().toString()),
                        Rank.fromIndex(input[4].toInt() - 1)!!
                    )
                )
            else null
        }
    }
}

class EnPassantMove(from: Coordinate, to: Coordinate, val captured: Coordinate): Move(from, to) {
    override fun toString(): String =
            "ep${from.file}${from.rank}${to.file}${to.rank}${captured.file}${captured.rank}"

    companion object {
        fun parse(input: String): EnPassantMove? {
            return if (input.startsWith("ep"))
                EnPassantMove(
                    Coordinate(
                        File.valueOf(input[2].toUpperCase().toString()),
                        Rank.fromIndex(input[3].toInt() - 1)!!
                    ),
                    Coordinate(
                        File.valueOf(input[4].toUpperCase().toString()),
                        Rank.fromIndex(input[5].toInt() - 1)!!
                    ),
                    Coordinate(
                        File.valueOf(input[6].toUpperCase().toString()),
                        Rank.fromIndex(input[7].toInt() - 1)!!
                    )
                )
            else null
        }
    }
}

class CastleMove(from: Coordinate, to: Coordinate, val rookMove: BasicMove): Move(from, to) {
    override fun toString(): String =
            "c${from.file}${from.rank}${to.file}${to.rank}${rookMove.from.file}${rookMove.from.rank}${rookMove.to.file}${rookMove.to.rank}"

    companion object {
        fun parse(input: String): CastleMove? {
            return if (input.startsWith("c"))
                CastleMove(
                    Coordinate(
                        File.valueOf(input[1].toUpperCase().toString()),
                        Rank.fromIndex(input[2].toInt() - 1)!!
                    ),
                    Coordinate(
                        File.valueOf(input[3].toUpperCase().toString()),
                        Rank.fromIndex(input[4].toInt() - 1)!!
                    ),
                    BasicMove(
                        Coordinate(
                            File.valueOf(input[5].toUpperCase().toString()),
                            Rank.fromIndex(input[6].toInt() - 1)!!
                        ),
                        Coordinate(
                            File.valueOf(input[7].toUpperCase().toString()),
                            Rank.fromIndex(input[8].toInt() - 1)!!
                        )
                    )
                )
            else null
        }
    }
}

class PawnPromotionMove(from: Coordinate, to: Coordinate, val promotion: Piece): Move(from, to) {
    val withKnight by lazy { PawnPromotionMove(from, to, Knight(promotion.color)) }
    val withQueen by lazy { PawnPromotionMove(from, to, Queen(promotion.color)) }

    override fun toString(): String {
        val pieceCode = if(promotion is Queen) "q" else "k"
        val colorCode = if(promotion.color == Color.Light) "l" else "d"
        return "p$colorCode$pieceCode${from.file}${from.rank}${to.file}${to.rank}"
    }

    companion object {
        fun parse(input: String): PawnPromotionMove? {
            val promotionPiece = when {
                input.startsWith("plq") -> Queen(Color.Light)
                input.startsWith("pdq") -> Queen(Color.Dark)
                input.startsWith("plk") -> Knight(Color.Light)
                input.startsWith("pdk") -> Knight(Color.Dark)
                else -> null
            }

            return promotionPiece?.let { piece ->
                PawnPromotionMove(
                    Coordinate(
                        File.valueOf(input[3].toUpperCase().toString()),
                        Rank.fromIndex(input[4].toInt() - 1)!!
                    ),
                    Coordinate(
                        File.valueOf(input[5].toUpperCase().toString()),
                        Rank.fromIndex(input[6].toInt() - 1)!!
                    ),
                    piece
                )
            }
        }
    }
}

