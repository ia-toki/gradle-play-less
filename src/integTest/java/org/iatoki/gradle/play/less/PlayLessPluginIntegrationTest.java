package org.iatoki.gradle.play.less;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.gradle.testkit.runner.BuildResult;
import org.junit.Before;
import org.junit.Test;

public class PlayLessPluginIntegrationTest extends AbstractIntegrationTest {
    @Before
    public void setUp() throws IOException {
        setUpProject();

        writeToBuildFile(
                "model {",
                "    components {",
                "        play {",
                "            sources {",
                "                extraLess(org.iatoki.gradle.play.less.LessSourceSet)",
                "            }",
                "        }",
                "    }",
                "}");
    }

    @Test
    public void lessSourceSetsListed() throws IOException {
        BuildResult result = runTask("components");

        assertThat(result.getOutput()).contains(
                "Less source 'play:less'",
                "Less source 'play:extraLess'");
    }

    @Test
    public void defaultLessCompileTaskListed() throws IOException {
        BuildResult result = runTask("tasks", "--all");
        assertThat(result.getOutput()).doesNotContain(
                "compilePlayBinaryPlayLess");

        writeToFile("app/assets/stylesheet/main.less");

        result = runTask("tasks", "--all");
        assertThat(result.getOutput()).contains(
                "compilePlayBinaryPlayLess");
    }
    @Test
    public void extraLessCompileTaskListed() throws IOException {
        BuildResult result = runTask("tasks", "--all");
        assertThat(result.getOutput()).doesNotContain(
                "compilePlayBinaryPlayExtraLess");

        writeToFile("src/play/extraLess/main.less");

        result = runTask("tasks", "--all");
        assertThat(result.getOutput()).contains(
                "compilePlayBinaryPlayExtraLess");
    }
}
