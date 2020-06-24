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

package org.ehrbase.client.aql.record;

import org.ehrbase.client.aql.field.AqlField;

public interface Record2<T1, T2> extends Record {
    /**
     * Get the first field.
     *
     * @return TODO-285
     */
    AqlField<T1> field1();

    /**
     * Get the second field.
     *
     * @return TODO-285
     */
    AqlField<T2> field2();

    /**
     * Get the first value.
     *
     * @return TODO-285
     */
    T1 value1();

    /**
     * Get the second value.
     *
     * @return TODO-285
     */
    T2 value2();
}
