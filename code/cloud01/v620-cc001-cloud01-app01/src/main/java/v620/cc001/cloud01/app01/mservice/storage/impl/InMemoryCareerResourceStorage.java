package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.CareerResourceCardDto;
import v620.cc001.cloud01.app01.mservice.storage.CareerResourceStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Empty in-memory resource storage. Runtime content must be created through the
 * administration boundary; this class never seeds cards that look published.
 */
public class InMemoryCareerResourceStorage implements CareerResourceStorage {

    private final List<CareerResourceCardDto> cards = new ArrayList<CareerResourceCardDto>();

    public InMemoryCareerResourceStorage() {
    }

    public InMemoryCareerResourceStorage(List<CareerResourceCardDto> cards) {
        if (cards != null) this.cards.addAll(cards);
    }

    public List<CareerResourceCardDto> listCards() {
        return new ArrayList<CareerResourceCardDto>(cards);
    }
}
