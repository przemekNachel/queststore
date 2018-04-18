package handlers;

import artifact.ArtifactDao;
import artifact.ArtifactDaoImpl;
import artifact.ArtifactModel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import user.user.User;
import utils.ParametersUtil;
import utils.RequestRedirector;
import utils.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map;

public class ArtifactRequestHandler implements HttpHandler {


    private final SessionManager sessionManager;
    private final RequestRedirector redirector;

    public ArtifactRequestHandler(SessionManager sessionManager, RequestRedirector redirector) {
        this.sessionManager = sessionManager;
        this.redirector = redirector;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        User user = sessionManager.getUserFromSession(httpExchange);
        String URIPath = httpExchange.getRequestURI().getPath();
        if (httpExchange.getRequestMethod().equals("POST") && URIPath.equals("/artifact/add")) {
            handleAddArtifact(httpExchange);
        } else if (httpExchange.getRequestMethod().equals("POST") && URIPath.equals("/artifact/remove")) {
            handleRemoveArtifact(httpExchange);
        }
        redirector.redirect(httpExchange, user);
    }

    private void handleRemoveArtifact(HttpExchange httpExchange) throws IOException {
        String unparsedPostData = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody())).readLine();

        Map<String, String> parameters = ParametersUtil.parseParameters(unparsedPostData);
        removeArtifact(parameters);
    }

    private void handleAddArtifact(HttpExchange httpExchange) throws IOException {
        String unparsedPostData = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody())).readLine();

        Map<String, String> parameters = ParametersUtil.parseParameters(unparsedPostData);

        ArtifactModel artifact = createArtifact(parameters);
        addArtifactToDatabase(artifact, parameters.get("type"));
    }

    private void removeArtifact(Map<String, String> parameters) {
        String name = parameters.get("name");
        ArtifactDao dao = new ArtifactDaoImpl();
        try {
            ArtifactModel artifact = dao.getArtifactByName(name);
            dao.deleteArtifact(artifact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArtifactModel createArtifact(Map<String, String> parameters) {
        String name = parameters.get("name");
        String price = parameters.get("price");
        String description = parameters.get("description");
        return new ArtifactModel(name, description, Integer.parseInt(price));
    }

    private void addArtifactToDatabase(ArtifactModel artifact, String type) {
        ArtifactDao dao = new ArtifactDaoImpl();
        type = convertType(type);
        try {
            dao.addArtifact(artifact);
            dao.addArtifactAdherence(artifact.getName(), type);
            System.out.println("Success");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String convertType(String type) {
        switch (type) {
            case "normal":
                return "artifact_basic";
            case "magic":
                return "artifact_magic";
            default:
                return "unknown";
        }
    }
}
