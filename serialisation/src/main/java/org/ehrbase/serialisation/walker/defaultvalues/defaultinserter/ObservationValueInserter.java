/*
 *
 *  *  Copyright (c) 2021  Stefan Spiska (Vitasystems GmbH) and Hannover Medical School
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

package org.ehrbase.serialisation.walker.defaultvalues.defaultinserter;

import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rm.datastructures.Event;
import com.nedap.archie.rm.datastructures.History;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.stream.Stream;
import org.ehrbase.serialisation.walker.defaultvalues.DefaultValuePath;
import org.ehrbase.serialisation.walker.defaultvalues.DefaultValues;

public class ObservationValueInserter extends AbstractValueInserter<Observation> {
  @Override
  public void insert(Observation rmObject, DefaultValues defaultValues) {

    if (rmObject.getData() != null) {
      insert(rmObject.getData(), defaultValues);
      if (rmObject.getData().getEvents() != null) {
        rmObject.getData().getEvents().forEach(e -> insert(e, defaultValues));
      }
    }

    if (rmObject.getState() != null) {
      insert(rmObject.getState(), defaultValues);
      if (rmObject.getState().getEvents() != null) {
        rmObject.getState().getEvents().forEach(e -> insert(e, defaultValues));
      }
    }
  }

  private void insert(History<?> rmObject, DefaultValues defaultValues) {

    if (isEmpty(rmObject.getOrigin())
        && (defaultValues.containsDefaultValue(DefaultValuePath.TIME)
            || defaultValues.containsDefaultValue(DefaultValuePath.HISTORY_ORIGIN))) {
      TemporalAccessor defaultTemporalAccessor =
          Stream.of(DefaultValuePath.HISTORY_ORIGIN, DefaultValuePath.TIME)
              .map(defaultValues::getDefaultValue)
              .filter(Objects::nonNull)
              .findFirst()
              .orElseThrow();
      rmObject.setOrigin(new DvDateTime(defaultTemporalAccessor));
    }
  }

  private void insert(Event<?> rmObject, DefaultValues defaultValues) {
    if (isEmpty(rmObject.getTime()) && defaultValues.containsDefaultValue(DefaultValuePath.TIME)) {

      rmObject.setTime(new DvDateTime(defaultValues.getDefaultValue(DefaultValuePath.TIME)));
    }
  }

  @Override
  public Class<Observation> getAssociatedClass() {
    return Observation.class;
  }
}