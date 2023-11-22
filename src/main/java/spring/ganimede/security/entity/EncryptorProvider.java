package spring.ganimede.security.entity;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;

public class EncryptorProvider
{
    private static final String PBE_PASSWORD = "Seed23467.";

    private PBEStringEncryptor encryptor;
    private static EncryptorProvider encryptorProvider;

    public EncryptorProvider()
    {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(PBE_PASSWORD);
    }

    public static EncryptorProvider getInstance()
    {
        if(encryptorProvider == null)
        {
            encryptorProvider = new EncryptorProvider();
        }

        return encryptorProvider;
    }

    public String marshal(final String plainText) throws Exception
    {
        // This encrypts and adds the ENC(...)
        return PropertyValueEncryptionUtils.encrypt(plainText, encryptor);
    }

    public String unmarshal(final String cypherText) throws Exception
    {
        String returnValue = cypherText;

        // Perform decryption operation as needed and store the new values
        if (PropertyValueEncryptionUtils.isEncryptedValue(cypherText))
        {
            returnValue = PropertyValueEncryptionUtils.decrypt(cypherText, encryptor);
        }

        return returnValue;
    }

}
