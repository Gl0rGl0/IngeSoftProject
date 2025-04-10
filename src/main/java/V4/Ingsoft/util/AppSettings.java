package V4.Ingsoft.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple class to hold application settings that need to be persisted.
 */
public class AppSettings {

    public static final String PATH = "settings.json"; // Define the settings file path

    private String ambitoTerritoriale = null;
    private int maxPrenotazioniPerPersona = 1; // Default value if not loaded

    // Default constructor for Jackson deserialization or initial creation
    public AppSettings() {}

    @JsonCreator
    public AppSettings(
            @JsonProperty("ambitoTerritoriale") String ambitoTerritoriale,
            @JsonProperty("maxPrenotazioniPerPersona") int maxPrenotazioniPerPersona) {
        this.ambitoTerritoriale = ambitoTerritoriale;
        // Ensure maxPrenotazioni is at least 1
        this.maxPrenotazioniPerPersona = Math.max(1, maxPrenotazioniPerPersona);
    }

    // --- Getters ---

    public String getAmbitoTerritoriale() {
        return ambitoTerritoriale;
    }

    public int getMaxPrenotazioniPerPersona() {
        return maxPrenotazioniPerPersona;
    }

    @JsonIgnore
    public boolean isAmbitoSet() {
        return (ambitoTerritoriale != null) && !ambitoTerritoriale.isBlank();
    }

    // --- Setters ---

    /**
     * Sets the ambito territoriale. Can only be set once.
     * @param ambito The territorial scope string.
     * @return true if the ambito was set successfully, false if it was already set.
     */
    public boolean setAmbitoTerritoriale(String ambito) {
        if (isAmbitoSet() || ambito == null || ambito.isBlank()) {
            return false; // Cannot set if already set or invalid input
        }

        this.ambitoTerritoriale = ambito.trim();

        if(isAmbitoSet())
            JsonStorage.saveObject(PATH, this);

        return true;
    }

    /**
     * Sets the maximum number of people per booking. Must be at least 1.
     * @param max The maximum number.
     */
    public void setMaxPrenotazioniPerPersona(int max) {
        this.maxPrenotazioniPerPersona = Math.max(1, max);
        JsonStorage.saveObject(PATH, this);
    }
}
