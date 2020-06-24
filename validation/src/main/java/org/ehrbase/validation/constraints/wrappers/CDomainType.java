/*
 * Modifications copyright (C) 2019 Christian Chevalley, Vitasystems GmbH and Hannover Medical School

 * This file is part of Project EHRbase

 * Copyright (c) 2015 Christian Chevalley
 * This file is part of Project Ethercis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ehrbase.validation.constraints.wrappers;

import org.openehr.schemas.v1.*;

import java.util.Map;

/**
 * Validate a domain type
 *
 * <p>Created by christian on 7/23/2016.
 * @see <a href="https://specifications.openehr.org/releases/AM/latest/AOM1.4.html#_c_domain_type_class">openEHR RM Specification</a>
 */
public class CDomainType extends CConstraint implements I_CArchetypeConstraintValidate {

    CDomainType(Map<String, Map<String, String>> localTerminologyLookup) {
        super(localTerminologyLookup);
    }

    @Override
    public void validate(String path, Object aValue, ARCHETYPECONSTRAINT archetypeconstraint) throws IllegalArgumentException {

        if (archetypeconstraint instanceof CDVORDINAL)
            new CDvOrdinal(localTerminologyLookup).validate(path, aValue, archetypeconstraint);
        else if (archetypeconstraint instanceof CCODEPHRASE)
            new CCodePhrase(localTerminologyLookup).validate(path, aValue, archetypeconstraint);
        else if (archetypeconstraint instanceof CDVQUANTITY)
            new CDvQuantity(localTerminologyLookup).validate(path, aValue, archetypeconstraint);
        else if (archetypeconstraint instanceof CDVSTATE)
            new CDvState(localTerminologyLookup).validate(path, aValue, archetypeconstraint);
        else
            throw new IllegalStateException("INTERNAL: unsupported CDOMAINTYPE:" + archetypeconstraint);

    }
}
