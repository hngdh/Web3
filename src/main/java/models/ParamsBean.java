package models;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Named("paramsBean")
@SessionScoped
@Getter
@Setter
public class ParamsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private BigDecimal x;
    private BigDecimal y;
    private BigDecimal r;
}
