import org.ensime.EnsimeKeys._
import org.ensime.EnsimePlugin

ensimeIgnoreMissingDirectories := true

ensimeIgnoreScalaMismatch in ThisBuild := true
ensimeScalaVersion in ThisBuild := "2.12.6"

lazy val serviceLocatorProject = ProjectRef(uri("."), "lagom-internal-meta-project-service-locator")
ensimeUnmanagedSourceArchives in serviceLocatorProject := Nil
ensimeUnmanagedJavadocArchives in serviceLocatorProject := Nil
ensimeScalacTransformer in serviceLocatorProject := identity
ensimeScalacOptions in serviceLocatorProject := EnsimePlugin.ensimeSuggestedScalacOptions(scalaVersion.value)
ensimeJavacOptions in serviceLocatorProject := Nil

lazy val cassandraProject = ProjectRef(uri("."), "lagom-internal-meta-project-cassandra")
ensimeUnmanagedSourceArchives in cassandraProject := Nil
ensimeUnmanagedJavadocArchives in cassandraProject := Nil
ensimeScalacTransformer in cassandraProject := identity
ensimeScalacOptions in cassandraProject := EnsimePlugin.ensimeSuggestedScalacOptions(scalaVersion.value)
ensimeJavacOptions in cassandraProject := Nil

lazy val kafkaProject = ProjectRef(uri("."), "lagom-internal-meta-project-kafka")
ensimeUnmanagedSourceArchives in kafkaProject := Nil
ensimeUnmanagedJavadocArchives in kafkaProject := Nil
ensimeScalacTransformer in kafkaProject := identity
ensimeScalacOptions in kafkaProject := EnsimePlugin.ensimeSuggestedScalacOptions(scalaVersion.value)
ensimeJavacOptions in kafkaProject := Nil
