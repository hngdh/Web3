package models;

import data_services.JsonService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;

@Named("pointSession")
@SessionScoped
public class PointSessionList implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private JsonService jsonService;

    @AllArgsConstructor
    @Getter
    public static final class PointDTO {
        public final BigDecimal x;
        public final BigDecimal y;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PointDTO other)) return false;
            return this.x.compareTo(other.getX()) == 0 && 
                   this.y.compareTo(other.getY()) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private final LinkedHashSet<PointDTO> points = new LinkedHashSet<>();

    public synchronized void add(BigDecimal x, BigDecimal y) {
        points.add(new PointDTO(x, y));
    }

    public synchronized String getJson() {
        return jsonService.toJson(points);
    }
}
