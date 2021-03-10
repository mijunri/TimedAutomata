package ta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clock {
    private String name;

    public Clock copy(){
        return new Clock(name);
    }
}
