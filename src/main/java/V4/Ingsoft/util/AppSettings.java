package V4.Ingsoft.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import V4.Ingsoft.view.ViewSE;

/**
 * Simple class to hold application settings that need to be persisted.
 */
public class AppSettings {

    public static final String PATH = "settings.json"; // Define the settings file path

    private String ambitoTerritoriale;
    private int maxPrenotazioniPerPersona; // Default value if not loaded

    @JsonCreator
    public AppSettings(
            @JsonProperty("ambitoTerritoriale") String ambitoTerritoriale,
            @JsonProperty("maxPrenotazioniPerPersona") int maxPrenotazioniPerPersona) {
        this.ambitoTerritoriale = ambitoTerritoriale;
        // Ensure maxPrenotazioni is at least 1
        this.maxPrenotazioniPerPersona = Math.max(1, maxPrenotazioniPerPersona);

        if(ambitoTerritoriale != null)
            ViewSE.println("Imported settings from data: AmbitoTerritoriale=" + ambitoTerritoriale + " maxPrenotazioniPerPersona="+maxPrenotazioniPerPersona);
    }

    public AppSettings() {
    }

    // --- Getters ---

    public String getAmbitoTerritoriale() {
        return ambitoTerritoriale;
    }

    /**
     * Sets the ambito territoriale. Can only be set once.
     *
     * @param ambito The territorial scope string.
     */
    public void setAmbitoTerritoriale(String ambito) {
        if (isAmbitoSet() || ambito == null || ambito.isBlank()) {
            return; // Cannot set if already set or invalid input
        }

        this.ambitoTerritoriale = ambito.trim();

        if (isAmbitoSet())
            JsonStorage.saveObject(PATH, this);

    }

    public int getMaxPrenotazioniPerPersona() {
        return maxPrenotazioniPerPersona;
    }

    // --- Setters ---

    /**
     * Sets the maximum number of people per booking. Must be at least 1.
     *
     * @param max The maximum number.
     */
    public void setMaxPrenotazioniPerPersona(int max) {
        this.maxPrenotazioniPerPersona = Math.max(1, max);
        JsonStorage.saveObject(PATH, this);
    }

    @JsonIgnore
    public boolean isAmbitoSet() {
        return (ambitoTerritoriale != null) && !ambitoTerritoriale.isBlank();
    }

    public void clear() {
        setAmbitoTerritoriale(null);
        setMaxPrenotazioniPerPersona(1);
    }
}
