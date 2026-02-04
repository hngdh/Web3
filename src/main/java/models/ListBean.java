package models;

import data_services.ParametersChecker;
import data_services.PointDBServices;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.primefaces.PrimeFaces;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Named("listBean")
@ApplicationScoped
public class ListBean {
    
    @Inject 
    private PointDBServices svc;
    
    @Inject 
    @Named("paramsBean") 
    private ParamsBean params;
    
    @Inject 
    private PointSessionList pointSessionList;
    
    @Inject
    private ParametersChecker parametersChecker;

    @Getter
    private LazyDataModel<Point> model;

    private String sid() {
        return FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionId(false);
    }

    @PostConstruct
    public void init() {
        model = new LazyDataModel<Point>() {
            private static final long serialVersionUID = 1L;

            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                return (int) svc.countBySession(sid());
            }

            @Override
            public List<Point> load(int first, int pageSize, 
                                   Map<String, SortMeta> sortBy, 
                                   Map<String, FilterMeta> filterBy) {
                return svc.findRangeBySession(sid(), first, pageSize);
            }
        };
    }

    public void submit() {
        Point p = new Point();
        p.setX(params.getX());
        p.setY(params.getY());
        p.setR(params.getR());
        p.setSessionId(sid());

        long start = System.nanoTime();
        p.setHit(parametersChecker.checkParams(p.getX(), p.getY(), p.getR()));
        long end = System.nanoTime();
        
        Double calTime = (end - start) / 1000000.0;
        p.setCalTime(calTime);
        p.setReleaseTime(LocalDateTime.now());

        svc.save(p);

        pointSessionList.add(p.getX(), p.getY());

        PrimeFaces.current().ajax().addCallbackParam("hit", p.isHit());
        PrimeFaces.current().ajax().addCallbackParam("pointsJson", pointSessionList.getJson());
    }

    public void updateHistory() {
        PrimeFaces.current().ajax().addCallbackParam("pointsJson", pointSessionList.getJson());
    }
}
