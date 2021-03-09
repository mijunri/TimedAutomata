package dfa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DfaTransition {
    private DfaLocation sourceLocation;
    private DfaLocation targetLocation;
    private String symbol;
}
