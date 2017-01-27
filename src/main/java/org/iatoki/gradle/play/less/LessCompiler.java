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

class LessCompiler {
    WorkResult compile(LessCompileSpec spec) {
        for (RelativeFile sourceFile : toRelativeFiles(spec.getSourceFiles())) {
            String input = GFileUtils.readFile(sourceFile.getFile());
            String output = input;
            File destinationFile = new File(spec.getDestinationDirectory(), toCss(sourceFile.getRelativePath()).getPathString());
            GFileUtils.writeFile(output, destinationFile);
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
        return path.replaceLastName(path.getLastName() + ".css");
    }
}
