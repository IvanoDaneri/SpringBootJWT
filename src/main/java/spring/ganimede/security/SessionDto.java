package spring.ganimede.security;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto implements Serializable
{
    @NotEmpty
    private String user;
    @NotEmpty
    private String role;
    @NotEmpty
    private String token;
}
