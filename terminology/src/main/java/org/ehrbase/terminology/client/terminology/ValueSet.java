/*
 *
 *  *  Copyright (c) 2020  Stefan Spiska (Vitasystems GmbH) and Hannover Medical School
 *  *  This file is part of Project EHRbase
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.ehrbase.terminology.client.terminology;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class ValueSet {
    public static final String LOCAL = "local";
    public static final ValueSet EMPTY_VALUE_SET = new ValueSet(LOCAL, LOCAL, Collections.emptySet());

    private final String terminologyId;
    private final String id;
    private final Set<TermDefinition> therms;

    public ValueSet(String terminologyId, String id, Set<TermDefinition> therms) {
        this.terminologyId = terminologyId;
        this.id = id;
        this.therms = therms;
    }

    public String getTerminologyId() {
        return terminologyId;
    }

    public Set<TermDefinition> getTherms() {
        return therms;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueSet valueSet = (ValueSet) o;
        return terminologyId.equals(valueSet.terminologyId) &&
                id.equals(valueSet.id) &&
                therms.equals(valueSet.therms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terminologyId, id, therms);
    }
}