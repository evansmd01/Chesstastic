package chesstastic.tests.framework

import chesstastic.engine.entities.Board
import chesstastic.util.Snapshot

interface AssertionHelpers {
    fun <T> T.shouldBe(expected: T) {
        if (this != expected) throw AssertionError("$this did not equal $expected")
    }

    fun <T> Collection<T>.shouldBe(expected: Collection<T>) {
        if (this != expected) throw AssertionError("$this did not equal $expected")
    }

    fun <T> Collection<T>.shouldBeEquivalentTo(expected: Collection<T>) {
        if (this.count() != expected.count()) throw AssertionError("$this was not equivalent to $expected")
        this.shouldContainAll(expected)
    }

    fun <T> Collection<T>.shouldContainAll(expected: Collection<T>) {
        if (!expected.all{ this.contains(it) }) throw AssertionError("$this did not contain all of $expected")
    }

    fun <T> Collection<T>.shouldContain(expected: T) {
        if (!this.contains(expected)) throw AssertionError("$this did not contain $expected")
    }

    fun <T> Collection<T>.shouldNotContain(unexpected: T) {
        if(this.contains(unexpected)) throw AssertionError("$this contained $unexpected")
    }

    fun <T> Collection<T>.shouldNotContain(unexpected: (T) -> Boolean) {
        if(this.any(unexpected)) throw AssertionError("$this contained $unexpected")
    }

    fun Board.shouldMatch(snapshot: String) {
        val other = Snapshot.parse(snapshot, this.historyMetadata.currentTurn)
        val positionEqual = this.positionEquals(other)
        if(!positionEqual) throw AssertionError("\nBoard with state:\n\n${Snapshot.from(this)}\n\ndid not equal:\n\n${Snapshot.from(other)}\n".prependIndent("       "))
    }

    fun <T: Comparable<T>> T.shouldBeGreaterThan(other: T) {
        if (this <= other) throw java.lang.AssertionError("$this was not greater than $other")
    }
}
