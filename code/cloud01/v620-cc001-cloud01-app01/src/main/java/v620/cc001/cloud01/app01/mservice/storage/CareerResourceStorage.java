package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.base.common.dto.career.CareerResourceCardDto;

import java.util.List;

/**
 * Storage boundary for curated CyanCruise resource cards.
 */
public interface CareerResourceStorage {

    List<CareerResourceCardDto> listCards();
}
