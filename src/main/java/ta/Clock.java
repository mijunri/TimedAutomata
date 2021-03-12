package ta;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class Clock {
    @Getter
    @Setter
    private String name;

    public Clock copy(){
        return new Clock(name);
    }
}
