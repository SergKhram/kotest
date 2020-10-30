package io.kotest.core.internal.tags

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.annotation.EffectiveTags
import io.kotest.core.annotation.LazyTags
import io.kotest.core.config.Configuration
import io.kotest.core.extensions.TagExtension
import io.kotest.core.test.TestCase
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

/**
 * Returns the current active [Tags] by combining any tags returned from currently
 * registered [TagExtension]s.
 */
fun Configuration.activeTags(): Tags {
   val extensions = extensions().filterIsInstance<TagExtension>()
   return if (extensions.isEmpty()) Tags.Empty else extensions.map { it.tags() }.reduce { a, b -> a.combine(b) }
}

/**
 * Returns the tags specified on the given class from the @Tags/@LazyTags annotation if present.
 */
fun KClass<*>.tags(): Set<Tag> {
   annotation<io.kotest.core.annotation.Tags>()?.let {
      return it.values.map { value -> NamedTag(value) }.toSet()
   } ?: annotation<io.kotest.core.annotation.LazyTags>()?.let {
      return it.values.map { value -> NamedTag(value) }.toSet()
   } ?: return emptySet()
}

/**
 * Returns the tags specified on the given class from the @EffectiveTags annotation if present.
 */
fun KClass<*>.effectiveTags(): Set<Tag> {
   annotation<io.kotest.core.annotation.EffectiveTags>()?.let {
      return it.values.map { value -> NamedTag(value) }.toSet()
   } ?: return emptySet()
}

/**
 * Returns all tags assigned to a [TestCase], taken from the test case config, spec inline function,
 * spec override function, or the spec class.
 */
fun TestCase.allTags(): Set<Tag> = this.config.tags + this.spec.declaredTags() + this.spec::class.tags()
