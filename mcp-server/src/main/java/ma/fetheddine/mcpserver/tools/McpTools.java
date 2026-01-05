package ma.fetheddine.mcpserver.tools;

import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class McpTools {

    private VectorStore vectorStore;
    public McpTools(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }
    @McpTool(name = "getEmployee"
            , description = "Get information about a given employee by name")
    public Employee getEmployee(@McpArg(description = "The employee name") String name){
        return new Employee(name, 15000.0, 5);
    }
    @McpTool(name = "getAllEmployees"
            , description = "Get information about all employees")
    public List<Employee> getAllEmployees(){
        return List.of(
                new Employee("Aya", 12000.0, 3),
                new Employee("Mohamed", 18000.0, 7),
                new Employee("Leila", 15000.0, 5)
        );
    }

    @McpTool(description = "get informations needed from CV context")
    public List<String> getContext(@McpArg(description = "The users needed informations from the CV") String query){
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(query)
                .topK(4)
                .build());
        return documents.stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }

}
record Employee(String name, double salary, int seniority) {}
