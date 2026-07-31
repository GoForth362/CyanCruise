package v620.cc001.cloud01.app01.mservice.furtherstudy;

import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookEntryDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookPageDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookQueryRequest;

/** Dedicated storage boundary for the postgraduate mistake book. */
public interface PostgraduateMistakeBookStorage {
    PostgraduateMistakeBookEntryDto save(String userId, PostgraduateMistakeBookEntryDto entry);
    PostgraduateMistakeBookPageDto list(String userId, PostgraduateMistakeBookQueryRequest request);
    PostgraduateMistakeBookEntryDto load(String userId, String mistakeId);
    boolean delete(String userId, String mistakeId);
}
