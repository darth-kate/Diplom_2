package serial;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserRegisterSerial {
    private String email;
    private String password;
    private String name;
}

