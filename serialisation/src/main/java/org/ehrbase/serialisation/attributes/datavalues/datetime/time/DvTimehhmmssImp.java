/*
 * Copyright (c) 2020 Christian Chevalley (Hannover Medical School) and Vitasystems GmbH
 *
 * This file is part of project EHRbase
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

package org.ehrbase.serialisation.attributes.datavalues.datetime.time;

import com.nedap.archie.rm.datavalues.quantity.datetime.DvTime;
import org.ehrbase.serialisation.attributes.datavalues.datetime.TemporalAttributes;

import java.time.temporal.Temporal;

public class DvTimehhmmssImp extends DvTimeAttributesImp {

    public DvTimehhmmssImp(DvTime dvTime) {
        super(dvTime);
    }

    @Override
    public Temporal getValueExtended() {
        return (Temporal)dvTime.getValue();
    }

    @Override
    public Integer getSupportedChronoFields() {
        return supportedChronoFields(TemporalAttributes.HOUR|TemporalAttributes.MINUTE_OF_HOUR|TemporalAttributes.SECOND_OF_MINUTE);
    }
}
