package ta;

import lombok.*;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaLocation {
    private String id;
    private String name;
    private boolean init;
    private boolean accept;

}
