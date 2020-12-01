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

package org.ehrbase.webtemplate.model;

import net.minidev.json.annotate.JsonIgnore;

import java.util.Deque;
import java.util.Map;

public class FilteredWebTemplate extends WebTemplate{

    public FilteredWebTemplate(WebTemplate other) {
        super(other);
    }

    @JsonIgnore
    private Map<WebTemplateNode, Deque<WebTemplateNode>> filteredNodeMap;

    public void setFilteredNodeMap(Map<WebTemplateNode, Deque<WebTemplateNode>> filteredNodeMap) {
        this.filteredNodeMap = filteredNodeMap;
    }

    public Deque<WebTemplateNode> findFiltersNodes(WebTemplateNode  node){
        return filteredNodeMap.get(node);
    }
}
