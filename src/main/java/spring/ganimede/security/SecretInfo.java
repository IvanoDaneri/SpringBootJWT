package spring.ganimede.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import javax.crypto.SecretKey;

@Getter
public class SecretInfo
{
    public static final String TOKEN_PREFIX = "Bearer: ";
    private static final String TOKEN_ID = "MyTokenID";

    private final SecretKey secretKey;
    private static SecretInfo secretInfo;

    public static SecretInfo getInstance()
    {
        if(secretInfo == null)
        {
            secretInfo = new SecretInfo();
        }

        return secretInfo;
    }

    public SecretInfo()
    {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String getTOKEN_PREFIX() {
        return TOKEN_PREFIX;
    }

    public String getTOKEN_ID() {
        return TOKEN_ID;
    }
}
