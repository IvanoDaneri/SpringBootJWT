package spring.ganimede.entity;

public enum CompanyType
{
    HIGH_TECH,
    MECHANICS,
    CHEMICAL,
    LOGISTIC,
    FOOD_AND_BEVERAGE;

    public static CompanyType fromValue(final String value)
    {
        return valueOf(value);
    }

    public String value()
    {
        return name();
    }
}
