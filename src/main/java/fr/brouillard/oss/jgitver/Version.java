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
package fr.brouillard.oss.jgitver;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Immutable
public class Version {
    public static final Version DEFAULT_VERSION = new Version(0, 0, 0);
    public static final Version EMPTY_REPOSITORY_VERSION = DEFAULT_VERSION.addQualifier("EMPTY_GIT_REPOSITORY");
    public static final Version NOT_GIT_VERSION = DEFAULT_VERSION.addQualifier("NOT_A_GIT_REPOSITORY");
    
    private final int major;
    private final int minor;
    private final int patch;
    private final String stringRepresentation;
    private final List<String> qualifiers;
    
    public Version(int major, int minor, int patch, @Nonnull String...qualifiers) {
        this(major, minor, patch, ImmutableList.copyOf(qualifiers));
    }
    
    private Version(int major, int minor, int patch, @Nonnull List<String> qualifiers) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.qualifiers = ImmutableList.copyOf(qualifiers);
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d.%d.%d", major, minor, patch));
        
        this.qualifiers.forEach(q -> sb.append('-').append(q));
        this.stringRepresentation = sb.toString();
    }
    
    
    @Override
    public String toString() {
        return stringRepresentation;
    }
    
    /**
     * Creates a new Version object from the current one, but append a new qualifier to it.
     * @param qualifier the qualifier to be added
     * @return a new Version object with exact same major/minor/patch numbers, previous qualifiers and the new added one
     */
    @Nonnull
    public Version addQualifier(@Nonnull String qualifier) {
        List<String> newQualifiers = new ArrayList<>(this.qualifiers);
        newQualifiers.add(qualifier);
        return new Version(major, minor, patch, newQualifiers.toArray(new String[newQualifiers.size()]));
    }
    
    /**
     * Creates a new Version object from the current one, but removes the given qualifier from it if it exists.
     * @param qualifier the qualifier to be removed
     * @return a new Version object with exact same major/minor/patch numbers, previous qualifiers without the given one
     */
    @Nonnull
    public Version removeQualifier(@Nonnull String qualifier) {
        List<String> newQualifiers = new ArrayList<>(this.qualifiers);
        newQualifiers.remove(qualifier);
        return new Version(major, minor, patch, newQualifiers.toArray(new String[newQualifiers.size()]));
    }
    
    /**
     * Creates a new Version object from the current one, but removes all qualifiers from it.
     * @return a new Version object with exact same major/minor/patch numbers, but without any qualifier
     */
    @Nonnull
    public Version noQualifier() {
        return new Version(major, minor, patch, Collections.emptyList());
    }

    @Nonnull
    private static final Pattern globalVersionPattern = Pattern.compile("^([0-9]+)(?:\\.([0-9]+))?(?:\\.([0-9]+))?(\\-[a-zA-Z0-9][a-zA-Z0-9\\-_]*)?$");
    
    /**
     * Creates a {@link Version} object by parsing the given string.
     * @param versionAsString the string to parse
     * @return a Version object built from the information of the given representation
     * @throws IllegalStateException if the given string doesn't match the version
     */
    @Nonnull
    public static Version parse(@Nonnull String versionAsString) {
        Matcher globalVersionMatcher = globalVersionPattern.matcher(versionAsString);
        
        if (globalVersionMatcher.matches()) {
            int major = Integer.parseInt(globalVersionMatcher.group(1));
            int minor = Integer.parseInt(Optional.ofNullable(globalVersionMatcher.group(2)).orElse("0"));
            int patch = Integer.parseInt(Optional.ofNullable(globalVersionMatcher.group(3)).orElse("0"));
            
            String qualifiersAsString = globalVersionMatcher.group(4);
            
            String[] qualifiers = new String[0];
            if (qualifiersAsString != null) {
                qualifiers = qualifiersAsString.replaceFirst("-", "").split("\\-");
            }
            
            return new Version(major, minor, patch, qualifiers);
        }
        
        throw new IllegalStateException("cannot parse " + versionAsString + " as a semver compatible version");
    }

    @Nonnull
    public Version increasePatch() {
        return new Version(major, minor, patch + 1, qualifiers);
    }
    
    public boolean isSnapshot() {
        return qualifiers.stream().anyMatch("SNAPSHOT"::equals);
    }
    
    public boolean isQualified() {
        return !qualifiers.isEmpty();
    }
}
