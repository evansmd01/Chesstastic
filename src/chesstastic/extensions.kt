package chesstastic

fun <T> T.transformIf(predicate: (T) -> Boolean, map: (T) -> T) = if(predicate(this)) map(this) else this
