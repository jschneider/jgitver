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
package fr.brouillard.oss.jgitver.metadata;

import fr.brouillard.oss.jgitver.GitVersionCalculator;

/**
 * Enumeration of all the possible metadata that {@link GitVersionCalculator} can provide for a repository.
 * 
 * @author Matthieu Brouillard
 */
public enum Metadatas {
    /**
     * Is the repository dirty. 
     */
    DIRTY, 
    /**
     * Name of the commiter of HEAD commit. 
     */
    HEAD_COMMITTER_NAME, 
    /**
     * Email of the commiter of HEAD commit. 
     */
    HEAD_COMMITER_EMAIL, 
    /**
     * Datetime of the commit.
     */
    HEAD_COMMIT_DATETIME, 
    /**
     * Corresponds to then the full git identifier of the HEAD. 
     */
    GIT_SHA1_FULL, 
    /**
     * Corresponds to a substring of the git identifier of the HEAD. 
     */
    GIT_SHA1_8, 
    /**
     * Corresponds to the current branch name if any. 
     */
    BRANCH_NAME,
    /**
     * Corresponds to the list of tags, associated with the current HEAD.
     */
    HEAD_TAGS, 
    /**
     * Corresponds to the list of annotated tags, associated with the current HEAD.
     */
    HEAD_ANNOTATED_TAGS, 
    /**
     * Corresponds to the list of light tags, associated with the current HEAD.
     */
    HEAD_LIGHTWEIGHT_TAGS, 
    /**
     * Corresponds to the base tag that was used for the version calculation.
     */
    BASE_TAG,
    /**
     * Corresponds to the whole list of tags of the current repository.
     */
    ALL_TAGS, 
    /**
     * Corresponds to the whole list of annotated tags of the current repository. 
     */
    ALL_ANNOTATED_TAGS, 
    /**
     * Corresponds to the whole list of light tags of the current repository.
     */
    ALL_LIGHTWEIGHT_TAGS, 
    /**
     * Corresponds to the whole list of tags that can serve for version calculation.
     */
    ALL_VERSION_TAGS, 
    /**
     * Corresponds to the whole list of annotated tags of the current repository that can serve for version calculation. 
     */
    ALL_VERSION_ANNOTATED_TAGS, 
    /**
     * Corresponds to the whole list of light tags of the current repository that can serve for version calculation.
     */
    ALL_VERSION_LIGHTWEIGHT_TAGS, 
    ;
}
