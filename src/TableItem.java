enum Kind{
    CONSTANT,VARIABLE,PROCEDURE
}
public class TableItem {
    public String name;

    public Kind kind;

    public Integer level;

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getAdr() {
        return adr;
    }

    public void setAdr(Integer adr) {
        this.adr = adr;
    }
}
