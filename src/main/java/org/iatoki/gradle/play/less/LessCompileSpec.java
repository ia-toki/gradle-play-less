package org.iatoki.gradle.play.less;

import java.io.File;

import org.gradle.api.file.FileCollection;

class LessCompileSpec {
    private final FileCollection sourceFiles;
    private final File destinationDirectory;

    LessCompileSpec(FileCollection sourceFiles, File destinationDirectory) {
        this.sourceFiles = sourceFiles;
        this.destinationDirectory = destinationDirectory;
    }

    FileCollection getSourceFiles() {
        return sourceFiles;
    }

    File getDestinationDirectory() {
        return destinationDirectory;
    }
}
