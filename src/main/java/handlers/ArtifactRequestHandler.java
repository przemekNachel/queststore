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

    private static final String POST = "POST";
    private static final String ADD_ARTIFACT_PATH = "/artifact/add";
    private static final String REMOVE_ARTIFACT_PATH = "/artifact/remove";
    private static final String EDIT_ARTIFACT_PATH = "/artifact/edit";

    private static final String ARTIFACT_NAME = "name";
    private static final String ARTIFACT_TYPE = "type";
    private static final String ARTIFACT_DESCRIPTION = "description";
    private static final String ARTIFACT_PRICE = "price";
    private static final String PREVIOUS_NAME = "previousname";

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
        String requestMethod = httpExchange.getRequestMethod();

        if (requestMethod.equals(POST) && URIPath.equals(ADD_ARTIFACT_PATH)) {
            handleAddArtifact(httpExchange);
        } else if (requestMethod.equals(POST) && URIPath.equals(REMOVE_ARTIFACT_PATH)) {
            handleRemoveArtifact(httpExchange);
        } else if (requestMethod.equals(POST) && URIPath.equals(EDIT_ARTIFACT_PATH)) {
            handleEditArtifact(httpExchange);
        }
        redirector.redirect(httpExchange, user);
    }

    private void handleEditArtifact(HttpExchange httpExchange) throws IOException {
        String unparsedPostData = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody())).readLine();
        Map<String, String> parameters = ParametersUtil.parseParameters(unparsedPostData);
        System.out.println(unparsedPostData);
        editArtifact(parameters);
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
        addArtifactToDatabase(artifact, parameters.get(ARTIFACT_TYPE));
    }

    private void removeArtifact(Map<String, String> parameters) {
        String name = parameters.get(ARTIFACT_NAME);
        ArtifactDao dao = new ArtifactDaoImpl();
        try {
            ArtifactModel artifact = dao.getArtifactByName(name);
            dao.deleteArtifact(artifact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editArtifact(Map<String, String> parameters) {
        String oldName = parameters.get(PREVIOUS_NAME);
        ArtifactDao dao = new ArtifactDaoImpl();

        try {
            ArtifactModel artifact = dao.getArtifactByName(oldName);
            setArtifactToUpdate(artifact, parameters);
            dao.updateArtifact(oldName, artifact);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArtifactModel createArtifact(Map<String, String> parameters) {
        String name = parameters.get(ARTIFACT_NAME);
        String price = parameters.get(ARTIFACT_PRICE);
        String description = parameters.get(ARTIFACT_DESCRIPTION);
        return new ArtifactModel(name, description, Integer.parseInt(price));
    }

    private void addArtifactToDatabase(ArtifactModel artifact, String type) {
        ArtifactDao dao = new ArtifactDaoImpl();
        type = convertType(type);
        try {
            dao.addArtifact(artifact);
            dao.addArtifactAdherence(artifact.getName(), type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setArtifactToUpdate(ArtifactModel artifact, Map<String, String> parameters) {
        String newName = parameters.get(ARTIFACT_NAME);
        String newPrice = parameters.get(ARTIFACT_PRICE);
        String newDescription = parameters.get(ARTIFACT_DESCRIPTION);

        artifact.setDescription(newDescription);
        artifact.setName(newName);
        artifact.setPrice(Integer.valueOf(newPrice));
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
