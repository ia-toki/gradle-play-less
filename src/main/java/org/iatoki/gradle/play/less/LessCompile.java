package org.iatoki.gradle.play.less;

import java.io.File;

import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.GFileUtils;

public class LessCompile extends SourceTask {
    private File outputDirectory;
    private LessCompileOptions options;

    public LessCompile() {
        this.options = new LessCompileOptions();
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public LessCompileOptions getOptions() {
        return options;
    }

    @TaskAction
    public void doCompile() {
        GFileUtils.forceDelete(outputDirectory);

        PlayLessCompiler lessCompiler = new PlayLessCompiler();
        LessCompileSpec spec = new LessCompileSpec(getSource(), outputDirectory, options);
        setDidWork(lessCompiler.compile(spec).getDidWork());
    }
}

