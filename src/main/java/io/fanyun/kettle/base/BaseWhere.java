package io.fanyun.kettle.base;

/**
 * @program: kettle-manager
 * @description:
 * @author: fanyunxu
 * @create: 2019-05-20 10:37
 **/
public class BaseWhere {
    private Integer offset=0;
    private Integer limit=10;
    public BaseWhere(){

    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        if(offset !=null){
            this.offset = offset;
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        if(limit !=null){
            this.limit = limit;
        }
    }
    public Integer getPage(){
        return offset/limit+1;
    }
    public Integer getPageSize(){
        return limit;
    }
}
