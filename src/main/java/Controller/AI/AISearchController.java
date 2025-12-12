package Controller.AI;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import Support.GeminiSearchService;

@WebServlet("/AISearchController")
public class AISearchController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private GeminiSearchService geminiService;
    
    public AISearchController() {
        super();
        this.geminiService = new GeminiSearchService();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        
        String query = request.getParameter("query");
        String context = request.getParameter("context");
        
        JsonObject result = new JsonObject();
        PrintWriter out = response.getWriter();
        
        try {
            if (query == null || query.trim().isEmpty()) {
                result.addProperty("success", false);
                result.addProperty("message", "Query không được để trống");
                out.print(result.toString());
                return;
            }
            
            String enhancedQuery = geminiService.enhanceSearchQuery(query, context);
            
            result.addProperty("success", true);
            result.addProperty("originalQuery", query);
            result.addProperty("enhancedQuery", enhancedQuery);
            
        } catch (Exception e) {
            e.printStackTrace();
            result.addProperty("success", false);
            result.addProperty("message", e.getMessage());
            result.addProperty("enhancedQuery", query);
        }
        
        out.print(result.toString());
        out.flush();
    }
}