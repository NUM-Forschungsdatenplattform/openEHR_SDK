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

package org.ehrbase.webtemplate.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.xmlbeans.XmlException;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.groups.Tuple;
import org.ehrbase.test_data.operationaltemplate.OperationalTemplateTestData;
import org.ehrbase.test_data.webtemplate.WebTemplateTestData;
import org.ehrbase.webtemplate.filter.Filter;
import org.ehrbase.webtemplate.model.WebTemplate;
import org.ehrbase.webtemplate.model.WebTemplateInput;
import org.ehrbase.webtemplate.model.WebTemplateInputValue;
import org.ehrbase.webtemplate.model.WebTemplateNode;
import org.junit.Test;
import org.openehr.schemas.v1.OPERATIONALTEMPLATE;
import org.openehr.schemas.v1.TemplateDocument;

public class OPTParserTest {

  @Test
  public void parseCoronaAnamnese() throws IOException, XmlException {
    OPERATIONALTEMPLATE template =
        TemplateDocument.Factory.parse(OperationalTemplateTestData.CORONA_ANAMNESE.getStream())
            .getTemplate();

    OPTParser cut = new OPTParser(template);
    WebTemplate actual = cut.parse();
    actual = new Filter().filter(actual);
    assertThat(actual).isNotNull();

    ObjectMapper objectMapper = new ObjectMapper();
    WebTemplate expected =
        objectMapper.readValue(
            IOUtils.toString(WebTemplateTestData.CORONA.getStream(), StandardCharsets.UTF_8),
            WebTemplate.class);

    List<String> errors = compareWebTemplate(actual, expected);

    checkErrors(errors, new String[] {}, new String[] {}, new String[] {});
  }

  @Test
  public void parseAltEvents() throws IOException, XmlException {
    OPERATIONALTEMPLATE template =
        TemplateDocument.Factory.parse(OperationalTemplateTestData.ALT_EVENTS.getStream())
            .getTemplate();

    OPTParser cut = new OPTParser(template);
    WebTemplate actual = cut.parse();
    actual = new Filter().filter(actual);
    assertThat(actual).isNotNull();

    ObjectMapper objectMapper = new ObjectMapper();
    WebTemplate expected =
        objectMapper.readValue(
            IOUtils.toString(
                WebTemplateTestData.ALTERNATIVE_EVENTS.getStream(), StandardCharsets.UTF_8),
            WebTemplate.class);

    List<String> errors = compareWebTemplate(actual, expected);

    // Nodes wich are generated by the parser but are not in the example
    checkErrors(errors, new String[] {}, new String[] {}, new String[] {});
  }

  @Test
  public void parseMultiOccurrence() throws IOException, XmlException {
    OPERATIONALTEMPLATE template =
        TemplateDocument.Factory.parse(OperationalTemplateTestData.MULTI_OCCURRENCE.getStream())
            .getTemplate();

    OPTParser cut = new OPTParser(template);
    WebTemplate actual = cut.parse();
    actual = new Filter().filter(actual);
    assertThat(actual).isNotNull();

    ObjectMapper objectMapper = new ObjectMapper();
    WebTemplate expected =
        objectMapper.readValue(
            IOUtils.toString(
                WebTemplateTestData.MULTI_OCCURRENCE.getStream(), StandardCharsets.UTF_8),
            WebTemplate.class);

    List<String> errors = compareWebTemplate(actual, expected);
    checkErrors(errors, new String[] {}, new String[] {}, new String[] {});
  }

  @Test
  public void parseInitialAssessment() throws IOException, XmlException {
    OPERATIONALTEMPLATE template =
        TemplateDocument.Factory.parse(OperationalTemplateTestData.INITIAL_ASSESSMENT.getStream())
            .getTemplate();

    OPTParser cut = new OPTParser(template);
    WebTemplate actual = cut.parse();
    actual = new Filter().filter(actual);
    assertThat(actual).isNotNull();

    ObjectMapper objectMapper = new ObjectMapper();
    WebTemplate expected =
        objectMapper.readValue(
            IOUtils.toString(
                WebTemplateTestData.INITIAL_ASSESSMENT.getStream(), StandardCharsets.UTF_8),
            WebTemplate.class);

    List<String> errors = compareWebTemplate(actual, expected);
    checkErrors(errors, new String[] {}, new String[] {}, new String[] {});
  }

  @Test
  public void parseConstrainTest() throws IOException, XmlException {
    OPERATIONALTEMPLATE template =
        TemplateDocument.Factory.parse(OperationalTemplateTestData.CONSTRAIN_TEST.getStream())
            .getTemplate();

    OPTParser cut = new OPTParser(template);
    WebTemplate actual = cut.parse();
    actual = new Filter().filter(actual);
    assertThat(actual).isNotNull();

    ObjectMapper objectMapper = new ObjectMapper();
    WebTemplate expected =
        objectMapper.readValue(
            IOUtils.toString(
                WebTemplateTestData.CONSTRAIN_TEST.getStream(), StandardCharsets.UTF_8),
            WebTemplate.class);

    List<String> errors = compareWebTemplate(actual, expected);
    checkErrors(
        errors,
        new String[] {},
        new String[] {},
        new String[] {
          "Validation not equal WebTemplateValidation{precision=null, range=WebTemplateValidationInterval{min=10, minOp=GT, max=15, maxOp=LT_EQ}, pattern='null'} != WebTemplateValidation{precision=null, range=WebTemplateValidationInterval{min=11, minOp=GT_EQ, max=15, maxOp=LT_EQ}, pattern='null'} in input.suffix:null id=total_pain_score aql=/content[openEHR-EHR-OBSERVATION.abbey_pain_scale.v0]/data[at0001]/events[at0002]/data[at0003]/items[at0014]/value",
          "Validation not equal null != WebTemplateValidation{precision=null, range=WebTemplateValidationInterval{min=100.0, minOp=GT_EQ, max=100.0, maxOp=LT_EQ}, pattern='null'} in input.suffix:denominator id=total_body_surface_area_tbsa_affected aql=/content[openEHR-EHR-OBSERVATION.affected_body_surface_area.v0]/data[at0001]/events[at0002]/data[at0003]/items[at0014]/value",
          "Validation not equal null != WebTemplateValidation{precision=null, range=WebTemplateValidationInterval{min=100.0, minOp=GT_EQ, max=100.0, maxOp=LT_EQ}, pattern='null'} in input.suffix:denominator id=total_body_surface_area_affected aql=/content[openEHR-EHR-OBSERVATION.affected_body_surface_area.v0]/data[at0001]/events[at0002]/data[at0003]/items[at0015]/items[at0017]/value",
          "Validation not equal null != WebTemplateValidation{precision=null, range=WebTemplateValidationInterval{min=100.0, minOp=GT_EQ, max=100.0, maxOp=LT_EQ}, pattern='null'} in input.suffix:denominator id=affected_site_surface_area aql=/content[openEHR-EHR-OBSERVATION.affected_body_surface_area.v0]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[at0011]/items[at0013]/value",
          "Validation not equal null != WebTemplateValidation{precision=null, range=WebTemplateValidationInterval{min=100.0, minOp=GT_EQ, max=100.0, maxOp=LT_EQ}, pattern='null'} in input.suffix:denominator id=affected_site_surface_area aql=/content[openEHR-EHR-OBSERVATION.affected_body_surface_area.v0]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[at0010]/value",
          "Validation not equal null != WebTemplateValidation{precision=null, range=WebTemplateValidationInterval{min=100.0, minOp=GT_EQ, max=100.0, maxOp=LT_EQ}, pattern='null'} in input.suffix:denominator id=site_surface_area aql=/content[openEHR-EHR-OBSERVATION.affected_body_surface_area.v0]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[at0008]/value"
        });
  }

  @Test
  public void parseAllTypes() throws IOException, XmlException {
    OPERATIONALTEMPLATE template =
        TemplateDocument.Factory.parse(OperationalTemplateTestData.ALL_TYPES.getStream())
            .getTemplate();

    OPTParser cut = new OPTParser(template);
    WebTemplate actual = cut.parse();
    actual = new Filter().filter(actual);
    assertThat(actual).isNotNull();

    ObjectMapper objectMapper = new ObjectMapper();
    WebTemplate expected =
        objectMapper.readValue(
            IOUtils.toString(WebTemplateTestData.ALL_TYPES.getStream(), StandardCharsets.UTF_8),
            WebTemplate.class);

    List<WebTemplateNode> dvOrdinalList =
        actual.getTree().findMatching(n -> n.getRmType().equals("DV_ORDINAL"));
    assertThat(dvOrdinalList).size().isEqualTo(1);
    assertThat(dvOrdinalList.get(0).getInputs())
        .flatExtracting(WebTemplateInput::getList)
        .extracting(
            WebTemplateInputValue::getLabel,
            WebTemplateInputValue::getValue,
            WebTemplateInputValue::getOrdinal)
        .containsExactlyInAnyOrder(
            new Tuple("ord1", "at0014", 0),
            new Tuple("ord1", "at0015", 1),
            new Tuple("ord3", "at0016", 2));

    List<WebTemplateNode> dvQuantityList =
        actual.getTree().findMatching(n -> n.getRmType().equals("DV_QUANTITY"));
    assertThat(dvQuantityList)
        .flatExtracting(WebTemplateNode::getInputs)
        .flatExtracting(WebTemplateInput::getList)
        .extracting(WebTemplateInputValue::getLabel, WebTemplateInputValue::getValue)
        .containsExactlyInAnyOrder(
            new Tuple("mg", "mg"),
            new Tuple("kg", "kg"),
            new Tuple("mm[H20]", "mm[H20]"),
            new Tuple("mm[Hg]", "mm[Hg]"));
    List<WebTemplateNode> dvCodedTextList =
        actual.getTree().findMatching(n -> n.getRmType().equals("DV_CODED_TEXT"));
    assertThat(dvCodedTextList)
        .flatExtracting(WebTemplateNode::getInputs)
        .extracting(
            WebTemplateInput::getTerminology,
            i ->
                i.getList().stream()
                    .map(v -> v.getValue() + ":" + v.getLabel())
                    .collect(Collectors.joining(";")))
        .containsExactlyInAnyOrder(
            new Tuple("openehr", "433:event"),
            new Tuple("local", "at0006:value1;at0007:value2;at0008:value3"),
            new Tuple("local", ""),
            new Tuple("local", ""),
            new Tuple("SNOMED-CT", ""),
            new Tuple("SNOMED-CT", ""),
            new Tuple("openehr", "at0003:Planned;at0004:Active;at0005:Completed"),
            new Tuple("openehr", "526:planned;245:active;532:completed"),
            new Tuple(null, ""),
            new Tuple(null, ""),
            new Tuple(null, ""),
            new Tuple(null, ""));

    List<String> errors = compareWebTemplate(actual, expected);

    checkErrors(errors, new String[] {}, new String[] {}, new String[] {});
  }

  public void checkErrors(
      List<String> errors, String[] extras, String[] missing, String[] wrongValidations) {

    SoftAssertions softAssertions = new SoftAssertions();

    // Nodes wich are generated by the parser but are not in the example
    softAssertions
        .assertThat(errors)
        .filteredOn(s -> s.startsWith("Extra Node"))
        .containsExactlyInAnyOrder(extras);

    // Inputs wich are generated by the parser but are not in the example
    softAssertions
        .assertThat(errors)
        .filteredOn(s -> s.startsWith("Extra Input"))
        .containsExactlyInAnyOrder();

    // Nodes wich are in the example but are not generated by the parser
    softAssertions
        .assertThat(errors)
        .filteredOn(s -> s.startsWith("Missing Node"))
        .containsExactlyInAnyOrder(missing);

    // Inputs wich are in the example but are not generated by the parser
    softAssertions
        .assertThat(errors)
        .filteredOn(s -> s.startsWith("Missing Input"))
        .containsExactlyInAnyOrder();

    // Non equal validations
    softAssertions
        .assertThat(errors)
        .filteredOn(s -> s.startsWith("Validation not equal"))
        .containsExactlyInAnyOrder(wrongValidations);

    softAssertions.assertAll();
  }

  public List<String> compareWebTemplate(WebTemplate actual, WebTemplate expected) {
    return new ArrayList<>(compareNode(actual.getTree(), expected.getTree()));
  }

  public List<String> compareNodes(List<WebTemplateNode> actual, List<WebTemplateNode> expected) {
    List<String> errors = new ArrayList<>();
    Map<ImmutablePair<String, String>, WebTemplateNode> actualMap =
        actual.stream()
            .collect(
                Collectors.toMap(
                    n -> new ImmutablePair<>(n.getAqlPath(true), n.getId()), Function.identity()));
    Map<ImmutablePair<String, String>, WebTemplateNode> expectedMap =
        expected.stream()
            .collect(
                Collectors.toMap(
                    n -> new ImmutablePair<>(n.getAqlPath(true), n.getId()), Function.identity()));

    for (ImmutablePair<String, String> pair : actualMap.keySet()) {
      if (expectedMap.containsKey(pair)) {
        errors.addAll(compareNode(actualMap.get(pair), expectedMap.get(pair)));
      } else {
        errors.add(String.format("Extra Node id=%s aql=%s", pair.getRight(), pair.getLeft()));
      }
    }
    for (ImmutablePair<String, String> pair : expectedMap.keySet()) {
      if (!actualMap.containsKey(pair)) {
        errors.add(String.format("Missing Node id=%s aql=%s", pair.getRight(), pair.getLeft()));
      }
    }
    return errors;
  }

  public List<String> compareNode(WebTemplateNode actual, WebTemplateNode expected) {

    List<String> errors = new ArrayList<>();
    errors.addAll(compareNodes(actual.getChildren(), expected.getChildren()));

    errors.addAll(compereInputs(actual, actual.getInputs(), expected.getInputs()));
    return errors;
  }

  private Collection<String> compereInputs(
      WebTemplateNode node, List<WebTemplateInput> actual, List<WebTemplateInput> expected) {
    List<String> errors = new ArrayList<>();

    Map<String, WebTemplateInput> actualMap =
        actual.stream()
            .collect(
                Collectors.toMap(
                    webTemplateInput ->
                        webTemplateInput.getSuffix() + ":" + webTemplateInput.getType(),
                    Function.identity()));
    Map<String, WebTemplateInput> expectedMap =
        expected.stream()
            .collect(
                Collectors.toMap(
                    webTemplateInput ->
                        webTemplateInput.getSuffix() + ":" + webTemplateInput.getType(),
                    Function.identity()));

    for (Map.Entry<String, WebTemplateInput> entry : actualMap.entrySet()) {
      if (!expectedMap.containsKey(entry.getKey())) {
        errors.add(
            String.format(
                "Extra Input %s in id=%s aql=%s", entry.getKey(), node.getId(), node.getAqlPath()));
      } else {
        errors.addAll(compareInput(node, entry.getValue(), expectedMap.get(entry.getKey())));
      }
    }

    for (Map.Entry<String, WebTemplateInput> entry : expectedMap.entrySet()) {
      if (!actualMap.containsKey(entry.getKey())) {
        errors.add(
            String.format(
                "Missing Input %s in id=%s aql=%s",
                entry.getKey(), node.getId(), node.getAqlPath()));
      }
    }

    return errors;
  }

  private List<String> compareInput(
      WebTemplateNode node, WebTemplateInput actual, WebTemplateInput expected) {
    List<String> errors = new ArrayList<>();

    if (!Objects.equals(actual.getValidation(), expected.getValidation())) {
      errors.add(
          String.format(
              "Validation not equal %s != %s in input.suffix:%s id=%s aql=%s",
              actual.getValidation(),
              expected.getValidation(),
              actual.getSuffix(),
              node.getId(),
              node.getAqlPath()));
    }
    return errors;
  }
}
