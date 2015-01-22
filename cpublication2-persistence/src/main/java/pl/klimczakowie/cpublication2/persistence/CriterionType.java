package pl.klimczakowie.cpublication2.persistence;

public enum CriterionType {
    EQUAL("="),
    NOT_EQUAL("!="),
    GRATER_OR_EQUAL_THAN(">="),
    LOWER_OR_EQUAL_THAN("<="),
    NOT_NULL("!=NULL"),
    NULL("=NULL"),
    LIKE("LIKE");

    private String value;

    private CriterionType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static CriterionType getCriterionType(String fromValue) {
        for (CriterionType crit : CriterionType.values()) {
            if (crit.value().equals(fromValue)) {
                return crit;
            }
        }
        return null;
    }
}
