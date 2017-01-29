package org.iatoki.gradle.play.less;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.file.RelativePath;
import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.tasks.WorkResult;
import org.gradle.util.GFileUtils;

import com.github.sommeri.less4j.Less4jException;
import com.github.sommeri.less4j.LessCompiler;
import com.github.sommeri.less4j.LessCompiler.CompilationResult;
import com.github.sommeri.less4j.LessCompiler.Configuration;
import com.github.sommeri.less4j.LessSource;
import com.github.sommeri.less4j.MultiPathFileSource;
import com.github.sommeri.less4j.core.ThreadUnsafeLessCompiler;

class PlayLessCompiler {
    private static final String LESS_EXT = ".less";
    private static final String CSS_EXT = ".css";

    WorkResult compile(LessCompileSpec spec) {
        LessCompiler compiler = new ThreadUnsafeLessCompiler();

        for (RelativeFile lessFile : toRelativeFiles(spec.getSourceFiles())) {
            File cssFile = new File(
                    spec.getDestinationDirectory(),
                    toCss(lessFile.getRelativePath()).getPathString());

            LessSource lessSource = new MultiPathFileSource(
                    lessFile.getFile(),
                    spec.getOptions().getIncludePaths().toArray(new File[0]));

            Configuration options = new Configuration();
            options.setCssResultLocation(cssFile);
            options.setCompressing(true);

            try {
                CompilationResult result = compiler.compile(lessSource, options);
                GFileUtils.writeFile(result.getCss(), cssFile);
            } catch (Less4jException e) {
                throw new RuntimeException(e);
            }
        }
        return () -> true;
    }

    private static List<RelativeFile> toRelativeFiles(FileCollection files) {
        ArrayList<RelativeFile> relativeFiles = new ArrayList<>();
        files.getAsFileTree().visit(new FileVisitor() {
            @Override
            public void visitDir(FileVisitDetails dirDetails) {}

            @Override
            public void visitFile(FileVisitDetails fileDetails) {
                relativeFiles.add(new RelativeFile(fileDetails.getFile(), fileDetails.getRelativePath()));
            }
        });
        return relativeFiles;
    }

    private static RelativePath toCss(RelativePath path) {
        String lessFilename = path.getLastName();
        String cssFilename;
        if (lessFilename.endsWith(LESS_EXT)) {
            cssFilename = lessFilename.substring(0, lessFilename.length() - LESS_EXT.length()) + CSS_EXT;
        } else {
            cssFilename = lessFilename + CSS_EXT;
        }
        return path.replaceLastName(cssFilename);
    }
}
