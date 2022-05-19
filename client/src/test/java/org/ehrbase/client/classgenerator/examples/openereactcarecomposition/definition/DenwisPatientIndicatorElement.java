package org.ehrbase.client.classgenerator.examples.openereactcarecomposition.definition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import java.util.List;
import javax.annotation.processing.Generated;
import org.ehrbase.client.annotations.Choice;
import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.Path;
import org.ehrbase.client.classgenerator.interfaces.LocatableEntity;

@Entity
@Generated(
    value = "org.ehrbase.client.classgenerator.ClassGenerator",
    date = "2021-02-16T12:59:53.565781900+01:00",
    comments = "https://github.com/ehrbase/openEHR_SDK Version: null"
)
public class DenwisPatientIndicatorElement implements LocatableEntity {
  /**
   * Path: open_eREACT-Care/Assessment/DENWIS/Point in time/feeder_audit
   */
  @Path("/feeder_audit")
  private FeederAudit feederAudit;

  /**
   * Path: open_eREACT-Care/Assessment/DENWIS/Point in time/Patient indicator
   * Description: Nurse recorded patient indicators.
   */
  @Path("/value")
  @Choice
  private List<DenwisPatientIndicatorChoice> value;

  public void setFeederAudit(FeederAudit feederAudit) {
     this.feederAudit = feederAudit;
  }

  public FeederAudit getFeederAudit() {
     return this.feederAudit ;
  }

  public void setValue(List<DenwisPatientIndicatorChoice> value) {
     this.value = value;
  }

  public List<DenwisPatientIndicatorChoice> getValue() {
     return this.value ;
  }
}
