package fr.cnes.sonar.report.providers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.QualityGate;
import fr.cnes.sonar.report.params.Params;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Provides quality gates
 * @author begarco
 */
public class QualityGateProvider implements IDataProvider {

    private Params params;
    private static final Logger LOGGER = Logger.getLogger(QualityGateProvider.class.getCanonicalName());

    public QualityGateProvider(Params params) {
        this.setParams(params);
    }

    /**
     * Get all the quality gates
     * @return Array containing all the issues
     */
    public List<QualityGate> getQualityGates() throws IOException, UnknownParameterException {
        ArrayList<QualityGate> res = new ArrayList<>();
        Gson gson = new Gson();
        String url = getParams().get("sonar.url");

        // Get all quality gates
        String request = String.format("%s/api/qualitygates/list", url);
        String raw = RequestManager.getInstance().get(request);
        JsonElement json = gson.fromJson(raw, JsonElement.class);
        JsonObject jo = json.getAsJsonObject();

        // Get quality gates criteria
        Integer defaultQG = (gson.fromJson(jo.get("default"), Integer.class));
        QualityGate [] tmp = (gson.fromJson(jo.get("qualitygates"), QualityGate[].class));
        for (QualityGate i : tmp) {
            request = String.format("%s/api/qualitygates/show?name=%s", url, i.getName().replaceAll(" ", "%20"));
            raw = RequestManager.getInstance().get(request);
            i.setConf(raw);
            if(i.getId().equals(defaultQG)) {
                i.setDefault(true);
            } else {
                i.setDefault(false);
            }
            res.add(i);
        }

        return res;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    /**
     * Return the quality gate corresponding to the project
     * @return The Quality Gate
     * @throws IOException when there are problem reading json
     * @throws UnknownParameterException when a parameter is incorrect
     * @throws UnknownQualityGateException when there is an error on a quality gate
     */
    public QualityGate getProjectQualityGate() throws IOException, UnknownParameterException, UnknownQualityGateException {
        QualityGate res = null;
        QualityGate tmp;
        Boolean find = false;
        List<QualityGate> qualityGates = getQualityGates();
        String qgName = getParams().get("sonar.project.quality.gate");

        Iterator<QualityGate> iterator = qualityGates.iterator();
        while (iterator.hasNext() && !find) {
            tmp = iterator.next();
            if(tmp.getName().equals(qgName)) {
                res = tmp;
                find = true;
            }
        }

        if(!find) {
            throw new UnknownQualityGateException(qgName);
        }

        return res;
    }
}
