package com.app.IVAS.dto.filters;


import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QueryResultsPojo<T>  {
    private Long limit;
    private Long offset;
    private Long total;
    private boolean empty;
    private List<T> results;
    private Map<String, Object> meta;
    private Double totalAmount;


    public QueryResultsPojo(List<T> results, Long limit, Long offset, Long total, boolean empty,  Map<String, Object> meta, Double totalAmount){
        this.empty = empty;
        this.limit = limit;
        this.offset = offset;
        this.results = results;
        this.total = total;
        this.meta = meta;
        this.totalAmount = totalAmount;
    }
}
