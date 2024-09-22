import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FlowLogTagger {

    public static void main(String[] args) {
        String lookupFilePath = "Lookup.csv";
        String flowLogFilePath = "FlowLogData.txt";
        String outputFilePath = "output.txt";
        Map<String, String> protocolMappings = new HashMap<>();
            protocolMappings.put("tcp", "6");
            protocolMappings.put("udp", "17");
            protocolMappings.put("icmp","1");// mapping the protocols with its corresponding number representation
        Map<String, String> tagMappings = loadTagMappings(lookupFilePath, protocolMappings);
        Map<String, Integer> tagCounts = new HashMap<>();
        Map<String, Integer> portProtocolCounts = new HashMap<>();

        parseFlowLogs(flowLogFilePath, tagMappings, tagCounts, portProtocolCounts,protocolMappings);
        writeOutput(outputFilePath, tagCounts, portProtocolCounts);
    }

    // Load the tag mappings from the CSV file
    private static Map<String, String> loadTagMappings(String filePath, Map<String,String> protocolMappings) {
        Map<String, String> tagMappings = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip the first line
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 3) {
                    String protocol= parts[1].trim().toLowerCase();
                    if(protocolMappings.containsKey(protocol)){
                        protocol= protocolMappings.get(protocol);
                    }
                    String key = parts[0].trim() + ":" + protocol; // dstport:protocol
                   // System.out.println(key);
                    String tag = parts[2].trim().toLowerCase();
                    tagMappings.put(key, tag);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading lookup file: " + e.getMessage());
        }
        return tagMappings;
    }

    // Parse the flow log file and map to tags
    private static void parseFlowLogs(String filePath, Map<String, String> tagMappings,
                                      Map<String, Integer> tagCounts, Map<String, Integer> portProtocolCounts, Map<String,String> protocolMappings) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String protocolString= "";
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length > 7) { // Check if the line has enough words
                    String dstPort = parts[6];// as per aws documentation
                    String protocol = parts[7].toLowerCase(); // as per aws documentation

                    String key = dstPort + ":" + protocol;

                    String tag = tagMappings.getOrDefault(key, "untagged");
                    tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
                    for(Map.Entry<String, String> protcolMap: protocolMappings.entrySet()){
                        if(protcolMap.getValue().equalsIgnoreCase(protocol)){
                            protocolString= protcolMap.getKey();
                        }

                    }
                    portProtocolCounts.put(dstPort + ":" + protocolString, portProtocolCounts.getOrDefault(key, 0) + 1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading flow log file: " + e.getMessage());
        }
    }

    // Write the output to a file
    private static void writeOutput(String filePath, Map<String, Integer> tagCounts, Map<String, Integer> portProtocolCounts) {
        try (BufferedWriter bw = Files.newBufferedWriter(filePath.toPath())) {
            bw.write("Tag Counts:\n");
            bw.write("Tag,Count\n");
            for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue() + "\n");
            }

            bw.write("\nPort/Protocol Combination Counts:\n");
            bw.write("Port,Protocol,Count\n");
            System.out.println(portProtocolCounts);
            for (Map.Entry<String, Integer> entry : portProtocolCounts.entrySet()) {
                String[] parts = entry.getKey().split(":");
                bw.write(parts[0] + "," + parts[1] + "," + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }
}
