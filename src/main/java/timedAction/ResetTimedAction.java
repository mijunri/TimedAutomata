package timedAction;

import lombok.Data;

@Data
public class ResetTimedAction extends TimedAction {
    private boolean reset;

    public ResetTimedAction(String symbol, Double value, boolean reset) {
        super(symbol, value);
        this.reset = reset;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(")
                .append(getSymbol())
                .append(",")
                .append(getValue())
                .append(",")
                .append(isReset()?"r":"n")
                .append(")");
        return stringBuilder.toString();
    }
}
