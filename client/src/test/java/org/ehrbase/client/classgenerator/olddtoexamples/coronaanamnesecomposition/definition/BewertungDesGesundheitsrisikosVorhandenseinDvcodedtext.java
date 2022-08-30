/*
 * Copyright (c) 2022 vitasystems GmbH and Hannover Medical School.
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
package org.ehrbase.client.classgenerator.olddtoexamples.coronaanamnesecomposition.definition;

import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.OptionFor;
import org.ehrbase.client.annotations.Path;

@Entity
@OptionFor("DV_CODED_TEXT")
public class BewertungDesGesundheitsrisikosVorhandenseinDvcodedtext
        implements BewertungDesGesundheitsrisikosVorhandenseinChoice {
    @Path("|defining_code")
    private VorhandenseinDefiningcode vorhandenseinDefiningcode;

    public void setVorhandenseinDefiningcode(VorhandenseinDefiningcode vorhandenseinDefiningcode) {
        this.vorhandenseinDefiningcode = vorhandenseinDefiningcode;
    }

    public VorhandenseinDefiningcode getVorhandenseinDefiningcode() {
        return this.vorhandenseinDefiningcode;
    }
}
