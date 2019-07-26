package golyu.com.smart;

/**
 * Created by pk001 on 2017/11/18.
 */

public class CodeBean {
    private String code; //代码
    private String description;//说明
    private boolean type; //类型true,业务办理,false,业务查询

    public CodeBean(String code, String description, boolean type) {
        this.code = code;
        this.description = description;
        this.type = type;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
