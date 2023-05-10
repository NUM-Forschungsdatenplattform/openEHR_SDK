/*
 * Copyright (c) 2023 vitasystems GmbH and Hannover Medical School.
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
package org.ehrbase.aql.render;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nedap.archie.json.JacksonUtil;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.ehrbase.aql.dto.AqlDto;
import org.ehrbase.aql.dto.condition.SimpleValue;
import org.ehrbase.aql.dto.containment.Containment;
import org.ehrbase.aql.dto.containment.ContainmentDto;
import org.ehrbase.aql.dto.containment.ContainmentExpresionDto;
import org.ehrbase.aql.dto.path.AqlPath;
import org.ehrbase.aql.dto.path.predicate.PredicateHelper;
import org.ehrbase.aql.dto.select.SelectDto;
import org.ehrbase.aql.dto.select.SelectFieldDto;
import org.ehrbase.aql.dto.select.SelectPrimitiveDto;
import org.ehrbase.aql.dto.select.SelectStatementDto;
import org.ehrbase.util.exception.SdkException;

/**
 * @author Stefan Spiska
 */
public class AqlRender {

    private final AqlDto dto;

    private final Map<Integer, ContainmentDto> containmentByIdMap = new HashMap<>();

    public AqlRender(AqlDto dto) {
        this.dto = dto;
    }

    public String render() {

        StringBuilder sb = new StringBuilder();

        String from = buildFrom();

        renderSelect(sb, dto.getSelect());

        sb.append(from);

        return sb.toString();
    }

    private void renderSelect(StringBuilder sb, SelectDto dto) {

        sb.append("SELECT ");

        if (dto.isDistinct()) {
            sb.append("DISTINCT ");
        }

        sb.append(dto.getStatement().stream()
                .map(s -> {
                    StringBuilder sbInner = new StringBuilder();
                    renderSelectStatementDto(sbInner, s);
                    return sbInner.toString();
                })
                .collect(Collectors.joining(", ")));

        sb.append(" ");
    }

    private void renderSelectStatementDto(StringBuilder sb, SelectStatementDto dto) {

        if (dto instanceof SelectFieldDto) {
            renderSelectFieldDto(sb, (SelectFieldDto) dto);
        } else if (dto instanceof SelectPrimitiveDto) {
            renderSelectPrimitiveDto(sb, (SelectPrimitiveDto) dto);
        }

        if (dto.getName() != null) {
            sb.append(" AS ").append(dto.getName());
        }
    }

    private void renderSelectFieldDto(StringBuilder sb, SelectFieldDto dto) {

        ContainmentDto containmentExpresionDto = containmentByIdMap.get(dto.getContainmentId());

        if (containmentExpresionDto == null) {
            throw new SdkException("Select without corresponding contains");
        }

        sb.append(containmentExpresionDto.getIdentifier());

        sb.append(dto.getAqlPathDto().format(AqlPath.OtherPredicatesFormat.SHORTED, false));
    }

    private void renderSelectPrimitiveDto(StringBuilder sb, SelectPrimitiveDto dto) {

        sb.append(renderValue(dto.getSimpleValue()));
    }

    public String renderValue(SimpleValue simpleValue) {
        Object value = simpleValue.getValue();

        if (value == null) {

            return "NULL";
        }
        if (Long.class.isAssignableFrom(value.getClass()) || Integer.class.isAssignableFrom(value.getClass())) {
            return value.toString();
        } else if (Double.class.isAssignableFrom(value.getClass()) || Float.class.isAssignableFrom(value.getClass())) {
            return value.toString();
        }
        if (Boolean.class.isAssignableFrom(value.getClass())) {
            return value.toString();
        } else if (String.class.isAssignableFrom(value.getClass()) || UUID.class.isAssignableFrom(value.getClass())) {
            return StringUtils.wrap(value.toString(), "'");
        } else if (TemporalAccessor.class.isAssignableFrom(value.getClass())) {
            String valueAsString;
            try {
                valueAsString = JacksonUtil.getObjectMapper().writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new SdkException(e.getMessage(), e);
            }
            return StringUtils.wrap(valueAsString.replace("\"", ""), "'");
        } else {
            throw new SdkException(String.format("%s is not an valid AQL Value", value.getClass()));
        }
    }

    private String buildFrom() {
        StringBuilder sb = new StringBuilder();

        sb.append("FROM ");

        renderContainmentExpresionDto(sb, dto.getContains());

        return sb.toString();
    }

    private void renderContainmentExpresionDto(StringBuilder sb, ContainmentExpresionDto dto) {

        if (dto instanceof ContainmentDto) {
            renderContainmentDto(sb, (ContainmentDto) dto);
        }
    }

    private void renderContainmentDto(StringBuilder sb, ContainmentDto dto) {

        containmentByIdMap.put(dto.getId(), dto);

        Containment containment = dto.getContainment();

        sb.append(containment.getType()).append(" ");

        if (dto.getIdentifier() != null) {
            sb.append(dto.getIdentifier());
        }

        if (containment.getOtherPredicates() != null) {

            sb.append(PredicateHelper.format(containment.getOtherPredicates(), AqlPath.OtherPredicatesFormat.SHORTED));
        }

        if (dto.getContains() != null) {
            sb.append(" CONTAINS ");
            renderContainmentExpresionDto(sb, dto.getContains());
        }
    }
}
