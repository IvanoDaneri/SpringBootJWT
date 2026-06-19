package spring.ganimede.security.entity;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncryptorProvider
{
    private static final String PBE_PASSWORD = "Seed23467.";

    private final PBEStringEncryptor encryptor;
    private static EncryptorProvider encryptorProvider;

    public EncryptorProvider()
    {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(PBE_PASSWORD);
    }

    public static synchronized EncryptorProvider getInstance()
    {
        if(encryptorProvider == null)
        {
            encryptorProvider = new EncryptorProvider();
        }

        return encryptorProvider;
    }

    public String marshal(final String plainText) throws Exception
    {
        // Call the encryptor directly to get the raw encrypted string
        return encryptor.encrypt(plainText);
    }

    public String unmarshal(final String cypherText) throws Exception
    {
        if (cypherText == null)
        {
            return null;
        }

        // Call the encryptor directly to decrypt the raw encrypted string
        return encryptor.decrypt(cypherText);
    }

}
