package V5.Ingsoft.controller.item.persone;

import V5.Ingsoft.controller.item.interfaces.Deletable;
import V5.Ingsoft.model.DBAbstractPersonaHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public abstract class Persona extends Deletable {
    @JsonIgnore
    private final PersonaType personaType;
    private final String username;
    private String psw;
    private boolean isNew;

    public Persona(String username, String psw, PersonaType personaType, boolean isNew, boolean hash) throws Exception {
        super(username);

        if (username == null || username.isBlank()) throw new Exception("Username can't be empty");
        if (psw == null || psw.isEmpty()) throw new Exception("Password can't be empty");

        this.username = username;
        this.psw = hash ? DBAbstractPersonaHelper.securePsw(username, psw) : psw;
        this.personaType = personaType;
        this.isNew = isNew;
    }

    public String getUsername()     { return this.username; }
    public String getPsw()          { return this.psw; }
    public void setPsw(String psw)  { this.psw = psw; }
    public void setAsNotNew()       { this.isNew = false; }
    public PersonaType getType()    { return this.personaType; }
    public int getPriority()        { return this.personaType.getPriority(); }
    public void setNew(boolean b)   { this.isNew = b; }
    
    // Jackson saves is[something] as [something]: return value...
    @JsonIgnore public boolean isNew() { return this.isNew; }
    @Override public String getMainInformation() { return this.username; }
    @Override public String toString() { return "Username: " + getUsername(); }


}
