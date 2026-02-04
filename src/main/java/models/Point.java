package models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Point implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String sessionId;
    private BigDecimal x;
    private BigDecimal y;
    private BigDecimal r;
    private boolean hit;
    private Double calTime;
    private LocalDateTime releaseTime;
}
