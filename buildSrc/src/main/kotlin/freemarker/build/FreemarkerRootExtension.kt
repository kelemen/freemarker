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

import org.gradle.api.provider.ProviderFactory

class FreemarkerRootExtension constructor(val versionDef: FreemarkerVersionDef, providers: ProviderFactory) {
    val freemarkerCompilerVersionOverrideRef = providers.gradleProperty("freemarkerCompilerVersionOverride")

    val defaultJavaVersion = freemarkerCompilerVersionOverrideRef
        .orElse(providers.gradleProperty("freemarkerDefaultJavaVersion"))
        .getOrElse("8")

    val testRunnerJavaVersion = providers.gradleProperty("freeMarkerTestRunnerJavaVersion")
        .getOrElse(defaultJavaVersion)

    val javadocJavaVersion = providers.gradleProperty("freeMarkerJavadocJavaVersion")
        .getOrElse(defaultJavaVersion)

    val doSignPackages = providers.gradleProperty("signPublication").map { it.toBoolean() }.orElse(true)
}
