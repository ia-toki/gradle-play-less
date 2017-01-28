package org.iatoki.gradle.play.less;

import java.io.File;

import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.language.base.internal.tasks.SimpleStaleClassCleaner;
import org.gradle.language.base.internal.tasks.StaleClassCleaner;

public class LessCompile extends SourceTask {
    private File outputDirectory;

    @OutputDirectory
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @TaskAction
    public void doCompile() {
        StaleClassCleaner cleaner = new SimpleStaleClassCleaner(getOutputs());
        cleaner.setDestinationDir(getOutputDirectory());
        cleaner.execute();

        PlayLessCompiler lessCompiler = new PlayLessCompiler();
        LessCompileSpec spec = new LessCompileSpec(getSource(), outputDirectory);
        setDidWork(lessCompiler.compile(spec).getDidWork());
    }
}

