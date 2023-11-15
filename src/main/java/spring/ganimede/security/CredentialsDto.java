package spring.ganimede.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class CredentialsDto implements Serializable
{
    @NotEmpty
    private final String user;
    @NotEmpty
    private final String password;
}
