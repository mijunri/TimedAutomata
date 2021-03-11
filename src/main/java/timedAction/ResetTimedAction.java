package timedAction;

import lombok.Data;
import lombok.Getter;

import java.util.Objects;

public class ResetTimedAction extends TimedAction {
    @Getter
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof ResetTimedAction)) return false;
        if (!super.equals(o)) return false;

        ResetTimedAction that = (ResetTimedAction) o;

        return isReset() == that.isReset();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isReset() ? 1 : 0);
        return result;
    }
}
