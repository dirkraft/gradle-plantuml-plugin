/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jason Dunkelberger (a.k.a dirkraft)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.dirkraft.gradle

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path

class PlantUmlPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.task('renderPlantUml', type: RenderPlantUmlTask)
        project.task('cleanPlantUml', type: CleanPlantUmlTask)
    }
}

class RenderPlantUmlTask extends DefaultTask {

    def Path assetsPath = project.projectDir.toPath().resolve('assets/')

    RenderPlantUmlTask() {
        for (Path puml : Files.newDirectoryStream(assetsPath, '*.puml')) {
            inputs.file puml.toFile()
        }

        for (Path puml : Files.newDirectoryStream(assetsPath, '*.puml')) {
            outputs.file getDestination(puml.toFile(), '.svg').toFile()
            outputs.file getDestination(puml.toFile(), '.png').toFile()
        }
    }

    Path getDestination(File puml, String extension) {
        String baseName = FilenameUtils.getBaseName(puml.name)
        String destName = "${baseName}"
        assetsPath.resolve(destName + extension)
    }


    @TaskAction
    def render() {

        Path projectPath = project.projectDir.toPath()
        for (File puml : inputs.files) {
            String relPumlPath = projectPath.relativize(puml.toPath()).toString()
            String pumlContent = new String(Files.readAllBytes(puml.toPath()), 'UTF-8')

            SourceStringReader reader = new SourceStringReader(pumlContent)
            Path destPathSvg = getDestination(puml, '.svg')
            println "Rendering ${relPumlPath} to ${projectPath.relativize(destPathSvg)}"
            reader.generateImage(new FileOutputStream(destPathSvg.toFile()), new FileFormatOption(FileFormat.SVG))

            reader = new SourceStringReader(pumlContent)
            Path destPathPng = getDestination(puml, '.png')
            println "Rendering ${relPumlPath} to ${projectPath.relativize(destPathPng)}"
            reader.generateImage(new FileOutputStream(destPathPng.toFile()), new FileFormatOption(FileFormat.PNG))
        }


    }
}

class CleanPlantUmlTask extends DefaultTask {

    CleanPlantUmlTask() {

    }

    @TaskAction
    def clean() {

    }
}