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

import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.VersionCalculationException;
import fr.brouillard.oss.jgitver.metadata.MetadataRegistrar;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ConfigurableVersionStrategy extends VersionStrategy {
    private final boolean autoIncrementPatch;
    private final boolean useDistance;
    private final boolean useGitCommitId;
    private final int gitCommitIdLength;
    private final boolean useDirty;

    /**
     * Default constructor
     */
    public ConfigurableVersionStrategy(@Nonnull VersionNamingConfiguration vnc, @Nonnull Repository repository, @Nonnull Git git, @Nonnull MetadataRegistrar metadatas) {
        this(vnc, repository, git, metadatas, false, true, false, 8, false);
    }

    protected ConfigurableVersionStrategy(@Nonnull VersionNamingConfiguration vnc, @Nonnull Repository repository, @Nonnull Git git, @Nonnull MetadataRegistrar registrar, boolean autoIncrementPatch, boolean useDistance, boolean useGitCommitId, int gitCommitIdLength, boolean useDirty) {
        super(vnc, repository, git, registrar);
        this.autoIncrementPatch = autoIncrementPatch;
        this.useDistance = useDistance;
        this.useGitCommitId = useGitCommitId;
        this.gitCommitIdLength = gitCommitIdLength;
        this.useDirty = useDirty;
    }

    @Nonnull
    public ConfigurableVersionStrategy setAutoIncrementPatch(boolean autoIncrementPatch) {
        return new ConfigurableVersionStrategy(getVersionNamingConfiguration(), getRepository(), getGit(), getRegistrar(), autoIncrementPatch, useDistance, useGitCommitId, gitCommitIdLength, useDirty);
    }

    @Nonnull
    public ConfigurableVersionStrategy setUseDistance(boolean useDistance) {
        return new ConfigurableVersionStrategy(getVersionNamingConfiguration(), getRepository(), getGit(), getRegistrar(), autoIncrementPatch, useDistance, useGitCommitId, gitCommitIdLength, useDirty);
    }

    @Nonnull
    public ConfigurableVersionStrategy setUseGitCommitId(boolean useGitCommitId) {
        return new ConfigurableVersionStrategy(getVersionNamingConfiguration(), getRepository(), getGit(), getRegistrar(), autoIncrementPatch, useDistance, useGitCommitId, gitCommitIdLength, useDirty);
    }

    @Nonnull
    public ConfigurableVersionStrategy setGitCommitIdLength(int gitCommitIdLength) {
        return new ConfigurableVersionStrategy(getVersionNamingConfiguration(), getRepository(), getGit(), getRegistrar(), autoIncrementPatch, useDistance, useGitCommitId, gitCommitIdLength, useDirty);
    }

    @Nonnull
    public ConfigurableVersionStrategy setUseDirty(boolean useDirty) {
        return new ConfigurableVersionStrategy(getVersionNamingConfiguration(), getRepository(), getGit(), getRegistrar(), autoIncrementPatch, useDistance, useGitCommitId, gitCommitIdLength, useDirty);
    }

    @Nonnull
    @Override
    public Version build(@Nonnull Commit head, @Nonnull List<Commit> parents) throws VersionCalculationException {
        try {
            Commit base = parents.get(0);
            Ref tagToUse;
            
            if (isBaseCommitOnHead(head, base) && !GitUtils.isDirty(getGit())) {
                // consider first the annotated tags
                tagToUse = base.getAnnotatedTags().stream().findFirst()
                        .orElseGet(() -> base.getLightTags().stream().findFirst().orElse(null));
            } else {
                // consider first the light tags
                tagToUse = base.getLightTags().stream().findFirst()
                        .orElseGet(() -> base.getAnnotatedTags().stream().findFirst().orElse(null));
            }
            
            Version baseVersion;
            
            if (tagToUse == null) {
                // we have reach the initial commit of the repository
                baseVersion = Version.DEFAULT_VERSION;
            } else {
                String tagName = GitUtils.tagNameFromRef(tagToUse);
                getRegistrar().registerMetadata(Metadatas.BASE_TAG, tagName);
                baseVersion = Version
                        .parse(getVersionNamingConfiguration().extractVersionFrom(tagName));
            }
            
            final boolean useSnapshot = baseVersion.isSnapshot();
            
            if (!isBaseCommitOnHead(head, base) && autoIncrementPatch) {
                // we are not on head
                if (GitUtils.isAnnotated(tagToUse)) {
                    // found tag to use was an annotated one, lets' increment the version automatically
                    baseVersion = baseVersion.increasePatch();
                }
            }
            
            if (useDistance && !useSnapshot) {
                if (tagToUse == null) {
                    // no tag was found, let's count from initial commit
                    baseVersion = baseVersion.addQualifier("" + base.getHeadDistance());
                } else {
                    // use distance when not on head
                    // or if on head with a light tag
                    if (!isBaseCommitOnHead(head, base) || !GitUtils.isAnnotated(tagToUse)) {
                        baseVersion = baseVersion.addQualifier("" + base.getHeadDistance());
                    }
                }
            }
            
            if (useGitCommitId && !isBaseCommitOnHead(head, base)) {
                baseVersion = baseVersion.addQualifier(head.getGitObject().getName().substring(0, gitCommitIdLength));
            }
            
            if (!GitUtils.isDetachedHead(getRepository())) {
                getRegistrar().registerMetadata(Metadatas.BRANCH_NAME, getRepository().getBranch());
                
                // let's add a branch qualifier if one is computed
                Optional<String> branchQualifier = getVersionNamingConfiguration().branchQualifier(getRepository().getBranch());
                if (branchQualifier.isPresent()) {
                    baseVersion = baseVersion.addQualifier(branchQualifier.get());
                }
            }
            
            if (useDirty && GitUtils.isDirty(getGit())) {
                baseVersion = baseVersion.addQualifier("dirty");
            }
            
            return useSnapshot ? baseVersion.removeQualifier("SNAPSHOT").addQualifier("SNAPSHOT") : baseVersion;
        } catch (Exception ex) {
            throw new VersionCalculationException("cannot compute version", ex);
        }
    }
}
