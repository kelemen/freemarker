/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package freemarker.build

import java.nio.file.Files
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CompileClasspath
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.withGroovyBuilder

open class RmicTask @Inject constructor(
    layout: ProjectLayout,
    objects: ObjectFactory
) : DefaultTask() {

    @Input
    val rmiPackage: Property<String>

    @Input
    val rmiClassPattern: Property<String>

    @InputFiles
    @CompileClasspath
    val classpath: ConfigurableFileCollection

    @InputFiles
    @SkipWhenEmpty
    @IgnoreEmptyDirectories
    @PathSensitive(PathSensitivity.RELATIVE)
    val rmiInputClasses: ConfigurableFileCollection

    @OutputDirectory
    val destinationDirectory: DirectoryProperty

    init {
        this.destinationDirectory = objects.directoryProperty().value(layout.buildDirectory.dir(name))
        this.classpath = objects.fileCollection()
        this.rmiInputClasses = objects.fileCollection()
        this.rmiPackage = objects.property<String>().value("")
        this.rmiClassPattern = objects.property<String>().value("*")
    }

    @TaskAction
    fun compileRmi() {
        val rmicRelSrcPath = rmiPackage.get().split(".")
        val rmicSrcPattern = "${rmiClassPattern.get()}.class"

        val classesDir = rmiInputClasses
            .find { candidateDir ->
                val candidatePackageDir = candidateDir.toPath().withChildren(rmicRelSrcPath)
                if (Files.isDirectory(candidatePackageDir)) {
                    Files.newDirectoryStream(candidatePackageDir, rmicSrcPattern).use { files ->
                        val firstFile = files.first { Files.isRegularFile(it) }
                        firstFile != null
                    }
                } else {
                    false
                }
            }
            ?: throw IllegalStateException("Couldn't find classes dir in ${rmiInputClasses.asPath}")

        ant.withGroovyBuilder {
            "rmic"(
                "classpath" to classpath.asPath,
                "base" to classesDir.toString(),
                "destDir" to destinationDirectory.get().toString(),
                "includes" to "${rmicRelSrcPath.joinToString("/")}/$rmicSrcPattern",
                "verify" to "yes",
                "stubversion" to "1.2"
            )
        }
    }
}
