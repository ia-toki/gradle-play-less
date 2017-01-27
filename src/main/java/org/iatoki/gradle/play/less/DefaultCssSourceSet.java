package org.iatoki.gradle.play.less;

import org.gradle.language.base.sources.BaseLanguageSourceSet;

public class DefaultCssSourceSet extends BaseLanguageSourceSet implements CssSourceSet {
    @Override
    protected String getLanguageName() {
        return "CSS";
    }
}
