package v620.cc001.cloud01.app01.mservice.datamodel;

public final class DatamodelOwnershipGuard {

    private DatamodelOwnershipGuard() {
    }

    public static void requireUser(String expectedUserId, CosmicDatamodelRecord record, String objectName) {
        if (record == null) {
            throw new IllegalArgumentException(objectName + " not found");
        }
        String actual = DatamodelFieldMapper.asString(record.get(CyanCruiseDatamodelObjects.USER_ID));
        if (!DatamodelFieldMapper.same(expectedUserId, actual)) {
            throw new IllegalArgumentException(objectName + " does not belong to user " + expectedUserId);
        }
    }
}
