/**
 * Copyright (C) 2016 Matthieu Brouillard [http://oss.brouillard.fr/jgitver] (matthieu@brouillard.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.brouillard.oss.jgitver.impl;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Immutable
public class VersionNamingConfiguration {
    @Nonnull
    private final Pattern searchPattern;
    @Nonnull
    private final String replaceVersionRegex;
    @Nonnull
    private final List<String> noQualifierForBranches;

    /**
     * Builds a Configuration object holding information to use while building version.
     * @param searchVersionRegex a regex pattern that will be applied to the repository tag list 
     *      to filter only the tags that represent a version
     * @param replaceVersionRegex a replacement regex string that will be applied on searchVersionRegex to extract the tag string to use
     * @param noQualifierForBranches comma separated string of branches name for which no qualifier will be built
     */
    public VersionNamingConfiguration(@Nonnull String searchVersionRegex, @Nonnull String replaceVersionRegex, @Nonnull List<String> noQualifierForBranches) {
        this.noQualifierForBranches = ImmutableList.copyOf(noQualifierForBranches);
        this.searchPattern = Pattern.compile(searchVersionRegex);
        this.replaceVersionRegex = replaceVersionRegex;
    }

    @Nonnull
    protected Pattern getSearchPattern() {
        return searchPattern;
    }

    @Nonnull
    protected String getReplaceVersionRegex() {
        return replaceVersionRegex;
    }

    @Nonnull
    public String extractVersionFrom(@Nonnull String tagName) {
        return searchPattern.matcher(tagName).replaceAll(replaceVersionRegex);
    }
    
    /**
     * Builds an optional qualifier from the given branch name.
     * Depending on the settings given {@link #VersionNamingConfiguration(String, String, List)} during construction,
     * a qualifier will or not be built.
     * @param branch the branch name for which a qualifier should be built
     * @return a non null optional object containing or not a qualifier 
     */
    @Nonnull
    public Optional<String> branchQualifier(@Nonnull String branch) {
        if (noQualifierForBranches.contains(branch)) {
            return Optional.empty();
        }
        
        return Optional.of(GitUtils.sanitizeBranchName(branch));
    }
}
