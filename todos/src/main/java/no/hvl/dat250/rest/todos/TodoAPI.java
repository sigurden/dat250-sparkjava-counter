package no.hvl.dat250.rest.todos;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.google.gson.Gson;

/**
 * Rest-Endpoint.
 */


public class TodoAPI {

	static List<Todo> todos = new ArrayList<Todo>();
	
    public static void main(String[] args) {
        if (args.length > 0) {
            port(Integer.parseInt(args[0]));
        } else {
            port(8080);
        }

        after((req, res) -> res.type("application/json"));
		
		get("/todos", (req, res) -> {
			Gson gson = new Gson();
			return gson.toJson(todos);
		});
		
		get("/todos/:id", (req, res) -> {
			Gson gson = new Gson();
			String id = req.params(":id");
			
			if(!isNumber(id)) {
				return "The id \"" + id + "\" is not a number!";
			}
			
			Todo filteredTodos = todos
					.stream()
					.filter(t -> isTodoId(t, id))
					.findFirst()
					.orElse(null);
			if (filteredTodos == null) {
				return "Todo with the id  \"" + id + "\" not found!";
			}
			return gson.toJson(filteredTodos);
		});
		
		post("/todos", (req, res) -> {
			Gson gson = new Gson();
			
			Todo newTodo = gson.fromJson(req.body(), Todo.class);
			todos.add(newTodo);
        	
        	return newTodo.toJson();
		});
		
		delete("/todos/:id", (req, res) -> {
			Gson gson = new Gson();
			String id = req.params(":id");

			if(!isNumber(id)) {
				return "The id \"" + id + "\" is not a number!";
			}
        	
        	List<Todo> deletedElements = todos.stream()
					.filter(t -> isTodoId(t, id))
					.collect(Collectors.toList());
        	todos = todos.stream()
        			.filter(t -> !isTodoId(t, id))
					.collect(Collectors.toList());
        	
        	return gson.toJson(deletedElements);
		});

        put("/todos/:id", (req,res) -> {
        	Gson gson = new Gson();
			String id = req.params(":id");
			if(!isNumber(id)) {
				return "The id \"" + id + "\" is not a number!";
			}
        	Todo newTodo = gson.fromJson(req.body(), Todo.class);
        	todos = todos.stream()
					.map(t -> {
						if(isTodoId(t, id)) {
							return newTodo;
						}
						return t;
					})
					.collect(Collectors.toList());
        	
        	return gson.toJson(newTodo);
        	
        });
        
    }
    
    private static boolean isNumber(String number) {
    	return number.matches("-*\\d*");
    }
    
    private static boolean isTodoId(Todo todo, String id) {
    	if (todo.getId() == null) {
    		return id.equals("null");
    	}
    	return todo.getId().toString().equals(id);
    }
}
