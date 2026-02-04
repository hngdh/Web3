package data_services;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class ParametersChecker {

    public boolean checkParams(BigDecimal x, BigDecimal y, BigDecimal r) {
        return checkSquare(x, y, r) || checkCircle(x, y, r) || checkTriangle(x, y, r);
    }

    private boolean checkSquare(BigDecimal x, BigDecimal y, BigDecimal r) {
        return x.compareTo(BigDecimal.ZERO) <= 0
                && y.compareTo(BigDecimal.ZERO) >= 0
                && x.compareTo(r.negate().divide(BigDecimal.valueOf(2))) >= 0
                && y.compareTo(r) <= 0;
    }

    private boolean checkCircle(BigDecimal x, BigDecimal y, BigDecimal r) {
        return x.compareTo(BigDecimal.ZERO) >= 0
                && y.compareTo(BigDecimal.ZERO) <= 0
                && (x.multiply(x).add(y.multiply(y)).compareTo(r.multiply(r)) <= 0);
    }

    private boolean checkTriangle(BigDecimal x, BigDecimal y, BigDecimal r) {
        return x.compareTo(BigDecimal.ZERO) >= 0
                && y.compareTo(BigDecimal.ZERO) >= 0
                && (x.compareTo(y.multiply(BigDecimal.valueOf(-2)).add(r)) <= 0);
    }
}
