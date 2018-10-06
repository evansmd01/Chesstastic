package chesstastic.ai.heuristics

import chesstastic.ai.Weights
import chesstastic.ai.Weights.Key.*
import chesstastic.ai.models.Imbalance
import chesstastic.engine.entities.*
import chesstastic.engine.metadata.SquareMetadata

class Exchanges(override val weights: Weights): Heuristic {
    override val key = WINNING_THE_EXCHANGE

    override fun calculateImbalance(board: Board): Imbalance {
        var lightExchange = Exchange(0.0,0.0)
        var darkExchange = Exchange(0.0,0.0)

        board.metadata.attackedPieces.forEach { squareMeta ->
            when (squareMeta.occupant?.color) {
                // attacked piece is Dark, so light would attack
                Color.Dark -> lightExchange += evaluateExchange(squareMeta)
                // attacked piece is Light, so dark would attack
                Color.Light -> darkExchange += evaluateExchange(squareMeta)
            }
        }

        return Imbalance(tallyExchange(lightExchange), tallyExchange(darkExchange))
    }

    private fun tallyExchange(exchange: Exchange): Double =
        if (exchange.totalGain > 0.0) {
            (exchange.totalGain * weights[EXCHANGE_GAIN_BONUS]) +
                (exchange.totalMaterialRemoved * weights[EXCHANGE_SIMPLIFICATION_BONUS])
        } else 0.0 // can't go negative because you just wouldn't kick off the attack if it's losing

    private fun evaluateExchange(squareMetadata: SquareMetadata): Exchange {
        val attacked = squareMetadata.pieceMetadata
            ?: throw Exception("can't evaluate exchange for unoccupied square")
        val attackers = squareMetadata.isAttackedBy.map { weights.pieceValue(it.piece.kind) }.sorted()
        val defenders = squareMetadata.isSupportedBy.map { weights.pieceValue(it.piece.kind) }.sorted()
        return runExchange(
            attackerWeightsSorted = attackers.drop(1),
            defenderWeightsSorted =  defenders,
            attackerWeight =  attackers.first(),
            capturedWeight = weights.pieceValue(attacked.piece.kind))
    }

    private fun runExchange(
        attackerWeightsSorted: List<Double>,
        defenderWeightsSorted: List<Double>,
        attackerWeight: Double,
        capturedWeight: Double
    ): Exchange {
        val nextDefender = defenderWeightsSorted.firstOrNull()
        val nextAttacker = attackerWeightsSorted.firstOrNull()
        return when {
            nextDefender == null ->
                // the piece is free for the taking
                Exchange(losses = 0.0, gains = capturedWeight)
            attackerWeight < capturedWeight ->
                // you immediately gain the upper hand in the exchange
                Exchange(losses = attackerWeight, gains = capturedWeight)
            attackerWeight == capturedWeight && nextAttacker != null ->
                // it starts off even, but is the next round of the exchange winning?
                Exchange(losses = attackerWeight, gains = capturedWeight) +
                    runExchange(attackerWeightsSorted.drop(1), defenderWeightsSorted.drop(1), nextAttacker, nextDefender)
            else ->
                // either it's dead even, or its too well defended, no winning here.
                Exchange(0.0, 0.0)
        }
    }

    private data class Exchange(val losses: Double, val gains: Double) {
        val totalGain = gains - losses
        val totalMaterialRemoved = gains + losses

        operator fun plus(other: Exchange) = Exchange(losses + other.losses, gains + other.gains)
    }
}


