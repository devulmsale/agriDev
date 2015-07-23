package models.base.enums;

/**
 * 性别.
 */
public enum Gender {
    MALE("M"),   //男
    FEMALE("F"), //女
    UNKNOWN("U"); //未知

    private String code;

    private Gender(String value) {
        code = value;
    }

    @Override
    public String toString() {
        return code;
    }
}
