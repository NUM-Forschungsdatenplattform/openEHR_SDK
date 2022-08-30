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
package org.ehrbase.client.classgenerator.examples.openereactcarecomposition.definition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datavalues.DvCodedText;
import org.ehrbase.client.aql.containment.Containment;
import org.ehrbase.client.aql.field.AqlFieldImp;
import org.ehrbase.client.aql.field.ListAqlFieldImp;
import org.ehrbase.client.aql.field.ListSelectAqlField;
import org.ehrbase.client.aql.field.SelectAqlField;

public class AbdomenSymptomClusterContainment extends Containment {
    public SelectAqlField<AbdomenSymptomCluster> ABDOMEN_SYMPTOM_CLUSTER = new AqlFieldImp<AbdomenSymptomCluster>(
            AbdomenSymptomCluster.class, "", "AbdomenSymptomCluster", AbdomenSymptomCluster.class, this);

    public SelectAqlField<DvCodedText> SYMPTOM_SIGN_NAME = new AqlFieldImp<DvCodedText>(
            AbdomenSymptomCluster.class, "/items[at0001.1]/value", "symptomSignName", DvCodedText.class, this);

    public ListSelectAqlField<Cluster> STRUCTURED_BODY_SITE = new ListAqlFieldImp<Cluster>(
            AbdomenSymptomCluster.class, "/items[at0147]", "structuredBodySite", Cluster.class, this);

    public ListSelectAqlField<Cluster> SPECIFIC_DETAILS = new ListAqlFieldImp<Cluster>(
            AbdomenSymptomCluster.class, "/items[at0153]", "specificDetails", Cluster.class, this);

    public ListSelectAqlField<Cluster> PREVIOUS_EPISODES = new ListAqlFieldImp<Cluster>(
            AbdomenSymptomCluster.class, "/items[at0146]", "previousEpisodes", Cluster.class, this);

    public ListSelectAqlField<Cluster> ASSOCIATED_SYMPTOM_SIGN = new ListAqlFieldImp<Cluster>(
            AbdomenSymptomCluster.class, "/items[at0063]", "associatedSymptomSign", Cluster.class, this);

    public SelectAqlField<PresenceDefiningCode> PRESENCE_DEFINING_CODE = new AqlFieldImp<PresenceDefiningCode>(
            AbdomenSymptomCluster.class,
            "/items[at0.1]/value|defining_code",
            "presenceDefiningCode",
            PresenceDefiningCode.class,
            this);

    public SelectAqlField<FeederAudit> FEEDER_AUDIT = new AqlFieldImp<FeederAudit>(
            AbdomenSymptomCluster.class, "/feeder_audit", "feederAudit", FeederAudit.class, this);

    private AbdomenSymptomClusterContainment() {
        super("openEHR-EHR-CLUSTER.symptom_sign-cvid.v0");
    }

    public static AbdomenSymptomClusterContainment getInstance() {
        return new AbdomenSymptomClusterContainment();
    }
}
