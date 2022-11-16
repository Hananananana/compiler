enum Kind{
    CONSTANT,VARIABLE,PROCEDURE
}
public class TableItem {
    public String name;

    public Kind kind;

    public Integer level_val;

    public Integer adr;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public Integer getLevel_val() {
        return level_val;
    }

    public void setLevel_val(Integer level_val) {
        this.level_val = level_val;
    }

    public Integer getAdr() {
        return adr;
    }

    public void setAdr(Integer adr) {
        this.adr = adr;
    }

    public TableItem(String name, Kind kind, Integer level_val) {
        this.name = name;
        this.kind = kind;
        this.level_val = level_val;
    }

    public TableItem(String name, Kind kind, Integer level_val, Integer adr) {
        this.name = name;
        this.kind = kind;
        this.level_val = level_val;
        this.adr = adr;
    }
}
