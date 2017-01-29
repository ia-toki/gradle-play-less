package org.iatoki.gradle.play.less;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LessCompileOptions {
    private List<File> includePaths;

    public LessCompileOptions() {
        this.includePaths = new ArrayList<>();
    }

    public List<File> getIncludePaths() {
        return includePaths;
    }

    public void addIncludePath(File includePath) {
        this.includePaths.add(includePath);
    }
}
