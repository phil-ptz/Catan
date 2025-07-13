import de.philx.catan.GameField.GameField;
import de.philx.catan.GameField.Node;

public class NodeCountTest {
    public static void main(String[] args) {
        System.out.println("Creating GameField to count nodes...");
        GameField gameField = new GameField(50.0);
        
        System.out.println("Total nodes: " + gameField.getNodes().size());
        
        // Let's check for duplicate positions
        java.util.Set<String> positions = new java.util.HashSet<>();
        int duplicates = 0;
        
        for (Node node : gameField.getNodes()) {
            String posKey = String.format("%.1f,%.1f", node.getX(), node.getY());
            if (positions.contains(posKey)) {
                duplicates++;
                System.out.println("Duplicate position found: " + posKey);
            } else {
                positions.add(posKey);
            }
        }
        
        System.out.println("Unique positions: " + positions.size());
        System.out.println("Duplicates: " + duplicates);
        
        // Expected nodes in a standard Catan board: 54 nodes
        System.out.println("Expected nodes: 54");
    }
}
