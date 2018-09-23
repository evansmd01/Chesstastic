package chesstastic.test.framework

import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

object TestReflection {
    val testSuiteFactories: List<() -> ChessTestSuite> by lazy {
        findTestClasses().map { kClass ->
            { kClass.createInstance() as ChessTestSuite }
        }
    }

    private val classLoader: URLClassLoader by lazy { ClassLoader.getSystemClassLoader() as URLClassLoader }

    private fun findTestClasses(): List<KClass<*>> {
        val packageFilter = "chesstastic"
        val testFilter = "test"
        val baseClass = ChessTestSuite::class

        val urls = classLoader.urLs.filter { it.file.toLowerCase().contains(packageFilter) }
        return urls.flatMap { url ->
            File(url.path).walkTopDown()
                .map{ it.absolutePath }
                .filter { it.toLowerCase().contains(testFilter) && it.endsWith(".class") }
                .map { it.removePrefix(url.path) }
                .map {
                    Regex("""\$\d+""")
                        .replace(it.replace("/", "."), "")
                        .replace(".class", "")
                }
                .distinct()
                .filterNot { it.contains("$") }
                .map { classLoader.loadClass(it).kotlin }
                .filterNot { it == baseClass }
                .filter { it.isSubclassOf(baseClass) }
                .toList()
        }
    }
}
