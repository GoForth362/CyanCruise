package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.base.common.dto.career.AssessmentScaleDto;

import java.util.List;

public interface AssessmentCatalog {

    List<AssessmentScaleDto> listScales();

    AssessmentScaleDto loadScale(Long scaleId);
}
