package spring.ganimede.security.entity;

public enum PermissionEnum
{
    AUTH_COMPANY_READ,
    AUTH_COMPANY_ADD,
    AUTH_EMPLOYEE_READ,
    AUTH_EMPLOYEE_ADD;

    public static String getCommaSeparatedAuthorityList()
    {
        String ret = "";
        for (PermissionEnum permission: PermissionEnum.values())
        {
            ret += permission.name() + ",";
        }

        // Remove last comma from string
        return ret.substring(0, ret.lastIndexOf(","));
    }
}
