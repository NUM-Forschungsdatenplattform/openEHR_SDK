/*
 * Copyright (c) 2020 vitasystems GmbH and Hannover Medical School.
 *
 * This file is part of project openEHR_SDK
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehrbase.aql.dto;

public class EhrDto {

    private int containmentId;
    private String identifier;

    public int getContainmentId() {
        return this.containmentId;
    }

    public void setContainmentId(int containmentId) {
        this.containmentId = containmentId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof EhrDto)) return false;
        final EhrDto other = (EhrDto) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getContainmentId() != other.getContainmentId()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EhrDto;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getContainmentId();
        return result;
    }

    public String toString() {
        return "EhrDto(containmentId=" + this.getContainmentId() + ")";
    }
}
