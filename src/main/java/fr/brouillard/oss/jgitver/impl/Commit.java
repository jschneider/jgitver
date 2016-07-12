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

import com.google.common.collect.ImmutableList;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

import javax.annotation.concurrent.Immutable;

/**
 * Container object for a git node involved in version calculation.
 * @author Matthieu Brouillard
 */
@Immutable
public class Commit {
    private final ObjectId gitObject;
    private final List<Ref> annotatedTags;
    private final List<Ref> lightTags;
    private final int headDistance;

    /**
     * Creates commit object using the given informations.
     * @param gitObject the git object corresponding the the git node
     * @param headDistance number of commit between head and this node (0 if node is HEAD)
     * @param annotatedTags list of annotated tags found on the git object
     * @param lightTags list of lightweight tags found on the git object
     */
    public Commit(ObjectId gitObject, int headDistance, List<Ref> annotatedTags, List<Ref> lightTags) {
        super();
        this.gitObject = gitObject;
        this.headDistance = headDistance;
        this.annotatedTags = ImmutableList.copyOf(annotatedTags);
        this.lightTags = ImmutableList.copyOf(lightTags);
    }

    public ObjectId getGitObject() {
        return gitObject;
    }

    public List<Ref> getAnnotatedTags() {
        //noinspection ReturnOfCollectionOrArrayField
        return annotatedTags;
    }

    public List<Ref> getLightTags() {
        //noinspection ReturnOfCollectionOrArrayField
        return lightTags;
    }

    public int getHeadDistance() {
        return headDistance;
    }
}
