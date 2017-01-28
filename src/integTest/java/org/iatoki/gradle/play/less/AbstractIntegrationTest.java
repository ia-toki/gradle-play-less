package org.iatoki.gradle.play.less;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.jar.JarFile;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.util.GFileUtils;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractIntegrationTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File projectDir;

    protected void setUpProject() throws IOException {
        projectDir = temporaryFolder.newFolder();

        writeToBuildFile(
                "plugins {",
                "    id 'play'",
                "    id 'org.iatoki.play-less'",
                "}");

        writeToFile(
                "settings.gradle",
                "rootProject.name = 'play-test'");
    }

    protected void writeToBuildFile(String... lines) throws IOException {
        writeToFile("build.gradle", lines);
    }

    protected void writeToFile(String pathString, String... lines) throws IOException {
        File file = new File(projectDir, pathString);
        file.getParentFile().mkdirs();
        file.createNewFile();

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append('\n');
        }

        Files.write(file.toPath(), sb.toString().getBytes(), StandardOpenOption.APPEND);
    }

    protected String readFromFile(String pathString) throws IOException {
        return GFileUtils.readFile(new File(projectDir, pathString));
    }

    protected void deleteFile(String pathString) throws IOException {
        GFileUtils.forceDelete(new File(projectDir, pathString));
    }

    protected BuildResult runTask(String... task) {
        return GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(task)
                .build();
    }

    protected JarFile getAssetsJar() throws IOException {
        return new JarFile(new File(projectDir, "build/playBinary/lib/play-test-assets.jar"));
    }
}
