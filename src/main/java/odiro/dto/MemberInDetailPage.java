package odiro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberInDetailPage {
    private String userId;
    private String name;
    private String email;
    private String profileImage;
}
