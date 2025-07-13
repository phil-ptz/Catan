package de.philx.catan.Controllers;

import de.philx.catan.Players.Player;
import de.philx.catan.Players.Player.ResourceType;
import de.philx.catan.Players.PlayerManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller für das Handelssystem in Catan
 * Verwaltet Handel zwischen Spielern und mit der Bank
 */
public class TradeController {
    
    private final PlayerManager playerManager;
    private final StringProperty tradeMessageProperty;
    
    // Active trade proposal
    private TradeOffer currentOffer;
    
    // Bank trade ratios (4:1 standard, can be improved with harbors)
    private static final int STANDARD_BANK_RATIO = 4;
    private final Map<ResourceType, Integer> bankTradeRatios;
    
    public TradeController(PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.tradeMessageProperty = new SimpleStringProperty("Bereit zum Handeln");
        this.bankTradeRatios = new HashMap<>();
        initializeBankRatios();
    }
    
    private void initializeBankRatios() {
        // Standard 4:1 ratios for all resources
        for (ResourceType resource : ResourceType.values()) {
            bankTradeRatios.put(resource, STANDARD_BANK_RATIO);
        }
    }
    
    /**
     * Create a trade offer between players
     * @param offererPlayerId The player making the offer
     * @param targetPlayerId The player receiving the offer
     * @param offeredResources Resources being offered
     * @param requestedResources Resources being requested
     * @return true if offer was created successfully
     */
    public boolean createTradeOffer(int offererPlayerId, int targetPlayerId, 
                                   Map<ResourceType, Integer> offeredResources,
                                   Map<ResourceType, Integer> requestedResources) {
        
        Player offerer = playerManager.getPlayerById(offererPlayerId);
        Player target = playerManager.getPlayerById(targetPlayerId);
        
        if (offerer == null || target == null) {
            setTradeMessage("Ungültige Spieler für Handel!");
            return false;
        }
        
        // Check if offerer has the resources they want to trade
        if (!offerer.canAfford(offeredResources)) {
            setTradeMessage(offerer.getName() + " hat nicht genügend Ressourcen für diesen Handel!");
            return false;
        }
        
        // Check if target has the requested resources
        if (!target.canAfford(requestedResources)) {
            setTradeMessage(target.getName() + " hat nicht genügend der angeforderten Ressourcen!");
            return false;
        }
        
        currentOffer = new TradeOffer(offererPlayerId, targetPlayerId, offeredResources, requestedResources);
        setTradeMessage(String.format("%s bietet %s einen Handel an: %s für %s", 
            offerer.getName(), target.getName(), 
            formatResources(offeredResources), formatResources(requestedResources)));
        
        return true;
    }
    
    /**
     * Accept the current trade offer
     * @param acceptingPlayerId The player accepting the trade
     * @return true if trade was executed successfully
     */
    public boolean acceptTrade(int acceptingPlayerId) {
        if (currentOffer == null) {
            setTradeMessage("Kein Handelsangebot vorhanden!");
            return false;
        }
        
        if (acceptingPlayerId != currentOffer.getTargetPlayerId()) {
            setTradeMessage("Nur der Ziel-Spieler kann diesen Handel akzeptieren!");
            return false;
        }
        
        return executeTrade(currentOffer);
    }
    
    /**
     * Decline the current trade offer
     * @param decliningPlayerId The player declining the trade
     * @return true if decline was processed
     */
    public boolean declineTrade(int decliningPlayerId) {
        if (currentOffer == null) {
            setTradeMessage("Kein Handelsangebot vorhanden!");
            return false;
        }
        
        if (decliningPlayerId != currentOffer.getTargetPlayerId()) {
            setTradeMessage("Nur der Ziel-Spieler kann diesen Handel ablehnen!");
            return false;
        }
        
        Player decliner = playerManager.getPlayerById(decliningPlayerId);
        setTradeMessage(decliner.getName() + " hat den Handel abgelehnt.");
        currentOffer = null;
        return true;
    }
    
    /**
     * Execute a bank trade (4:1 or better ratio with harbors)
     * @param playerId The player making the trade
     * @param giveResource The resource to give to the bank
     * @param giveAmount The amount to give
     * @param wantResource The resource to receive from the bank
     * @return true if trade was successful
     */
    public boolean executeBankTrade(int playerId, ResourceType giveResource, int giveAmount, ResourceType wantResource) {
        Player player = playerManager.getPlayerById(playerId);
        if (player == null) {
            setTradeMessage("Ungültiger Spieler!");
            return false;
        }
        
        int requiredRatio = bankTradeRatios.get(giveResource);
        
        // Check if amount is correct for the ratio
        if (giveAmount % requiredRatio != 0) {
            setTradeMessage(String.format("Bank-Handel erfordert Vielfache von %d für %s!", 
                requiredRatio, getResourceName(giveResource)));
            return false;
        }
        
        int receiveAmount = giveAmount / requiredRatio;
        
        // Check if player has enough resources
        if (player.getResourceAmount(giveResource) < giveAmount) {
            setTradeMessage("Nicht genügend " + getResourceName(giveResource) + " für Bank-Handel!");
            return false;
        }
        
        // Execute the trade
        player.removeResource(giveResource, giveAmount);
        player.addResource(wantResource, receiveAmount);
        
        setTradeMessage(String.format("%s hat %d %s gegen %d %s mit der Bank getauscht!", 
            player.getName(), giveAmount, getResourceName(giveResource), 
            receiveAmount, getResourceName(wantResource)));
        
        return true;
    }
    
    /**
     * Execute the trade between players
     */
    private boolean executeTrade(TradeOffer offer) {
        Player offerer = playerManager.getPlayerById(offer.getOffererPlayerId());
        Player target = playerManager.getPlayerById(offer.getTargetPlayerId());
        
        // Double-check that both players still have the resources
        if (!offerer.canAfford(offer.getOfferedResources()) || 
            !target.canAfford(offer.getRequestedResources())) {
            setTradeMessage("Handel kann nicht ausgeführt werden - unzureichende Ressourcen!");
            return false;
        }
        
        // Execute the resource transfer
        // Remove resources from offerer and give to target
        for (Map.Entry<ResourceType, Integer> entry : offer.getOfferedResources().entrySet()) {
            offerer.removeResource(entry.getKey(), entry.getValue());
            target.addResource(entry.getKey(), entry.getValue());
        }
        
        // Remove resources from target and give to offerer
        for (Map.Entry<ResourceType, Integer> entry : offer.getRequestedResources().entrySet()) {
            target.removeResource(entry.getKey(), entry.getValue());
            offerer.addResource(entry.getKey(), entry.getValue());
        }
        
        setTradeMessage(String.format("Handel erfolgreich! %s und %s haben Ressourcen getauscht.", 
            offerer.getName(), target.getName()));
        
        currentOffer = null;
        return true;
    }
    
    /**
     * Get all possible players for trading (excluding the current player)
     * @param currentPlayerId The current player's ID
     * @return List of possible trade partners
     */
    public List<Player> getPossibleTradePartners(int currentPlayerId) {
        List<Player> partners = new ArrayList<>();
        for (Player player : playerManager.getAllPlayers()) {
            if (player.getPlayerId() != currentPlayerId) {
                partners.add(player);
            }
        }
        return partners;
    }
    
    /**
     * Format resources for display
     */
    private String formatResources(Map<ResourceType, Integer> resources) {
        if (resources.isEmpty()) {
            return "nichts";
        }
        
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getValue()).append("x ").append(getResourceName(entry.getKey()));
            first = false;
        }
        return sb.toString();
    }
    
    /**
     * Get German name for resource type
     */
    private String getResourceName(ResourceType resource) {
        switch (resource) {
            case WOOD: return "Holz";
            case CLAY: return "Lehm";
            case WOOL: return "Wolle";
            case GRAIN: return "Getreide";
            case ORE: return "Erz";
            default: return resource.name();
        }
    }
    
    /**
     * Improve bank trade ratio for a specific resource (harbor effect)
     * @param resource The resource to improve
     * @param newRatio The new trade ratio (typically 3:1 or 2:1)
     */
    public void improveBankTradeRatio(ResourceType resource, int newRatio) {
        bankTradeRatios.put(resource, newRatio);
    }
    
    /**
     * Get the current bank trade ratio for a resource
     * @param resource The resource to check
     * @return The current trade ratio
     */
    public int getBankTradeRatio(ResourceType resource) {
        return bankTradeRatios.getOrDefault(resource, STANDARD_BANK_RATIO);
    }
    
    // Property getters
    public StringProperty tradeMessageProperty() {
        return tradeMessageProperty;
    }
    
    public String getTradeMessage() {
        return tradeMessageProperty.get();
    }
    
    private void setTradeMessage(String message) {
        tradeMessageProperty.set(message);
    }
    
    public TradeOffer getCurrentOffer() {
        return currentOffer;
    }
    
    public boolean hasActiveOffer() {
        return currentOffer != null;
    }
    
    public void clearCurrentOffer() {
        currentOffer = null;
        setTradeMessage("Bereit zum Handeln");
    }
    
    /**
     * Inner class representing a trade offer
     */
    public static class TradeOffer {
        private final int offererPlayerId;
        private final int targetPlayerId;
        private final Map<ResourceType, Integer> offeredResources;
        private final Map<ResourceType, Integer> requestedResources;
        
        public TradeOffer(int offererPlayerId, int targetPlayerId, 
                         Map<ResourceType, Integer> offeredResources,
                         Map<ResourceType, Integer> requestedResources) {
            this.offererPlayerId = offererPlayerId;
            this.targetPlayerId = targetPlayerId;
            this.offeredResources = new HashMap<>(offeredResources);
            this.requestedResources = new HashMap<>(requestedResources);
        }
        
        // Getters
        public int getOffererPlayerId() { return offererPlayerId; }
        public int getTargetPlayerId() { return targetPlayerId; }
        public Map<ResourceType, Integer> getOfferedResources() { return new HashMap<>(offeredResources); }
        public Map<ResourceType, Integer> getRequestedResources() { return new HashMap<>(requestedResources); }
    }
}
