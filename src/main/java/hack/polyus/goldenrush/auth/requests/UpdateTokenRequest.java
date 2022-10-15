package hack.polyus.goldenrush.auth.requests;

import lombok.Data;

@Data
public class UpdateTokenRequest {

    String refresh_token;
}

