package ma.fetheddine.emsibot.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AITools {
    @Tool(name = "getEmployee"
            , description = "Get information about a given employee by name")
    public Employee getEmployee(@ToolParam(description = "The employee name") String name){
        return new Employee(name, 15000.0, 5);
    }
    @Tool(name = "getAllEmployees"
            , description = "Get information about all employees")
    public List<Employee> getAllEmployees(){
        return List.of(
                new Employee("Aya", 12000.0, 3),
                new Employee("Mohamed", 18000.0, 7),
                new Employee("Leila", 15000.0, 5)
        );
    }

}
record Employee(String name, double salary, int seniority) {}
