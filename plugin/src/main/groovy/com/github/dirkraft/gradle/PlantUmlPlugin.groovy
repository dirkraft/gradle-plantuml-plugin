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