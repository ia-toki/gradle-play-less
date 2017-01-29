package org.iatoki.gradle.play.less;

import java.io.File;

import org.gradle.api.file.FileCollection;

class LessCompileSpec {
    private final FileCollection sourceFiles;
    private final File destinationDirectory;
    private final LessCompileOptions options;

    LessCompileSpec(FileCollection sourceFiles, File destinationDirectory, LessCompileOptions options) {
        this.sourceFiles = sourceFiles;
        this.destinationDirectory = destinationDirectory;
        this.options = options;
    }

    FileCollection getSourceFiles() {
        return sourceFiles;
    }

    File getDestinationDirectory() {
        return destinationDirectory;
    }

    LessCompileOptions getOptions() {
        return options;
    }
}
