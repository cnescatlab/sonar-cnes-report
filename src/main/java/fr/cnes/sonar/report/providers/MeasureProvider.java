
package fr.cnes.sonar.report.providers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.model.Measure;
import fr.cnes.sonar.report.params.Params;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Provides issue items
 * @author begarco
 */
public class MeasureProvider implements IDataProvider {

    /**
     * Logger for the class
     */
    private static final Logger LOGGER = Logger.getLogger(MeasureProvider.class.getCanonicalName());

    /**
     * Params of the program itself
     */
    private Params params;

    public MeasureProvider(Params params) {
        this.setParams(params);
    }

    /**
     * Get all the measures of a project
     * @return Array containing all the measures
     */
    public List<Measure> getMeasures() throws IOException, UnknownParameterException {
        ArrayList<Measure> res = new ArrayList<>();

        Gson gson = new Gson();
        String url = getParams().get("sonar.url");
        String projectKey = getParams().get("sonar.project.id");

        String request = String.format("%s/api/measures/component?componentKey=%s&metricKeys=ncloc,duplicated_lines_density,coverage,sqale_rating,reliability_rating,security_rating,alert_status",
                url, projectKey);
        String raw = RequestManager.getInstance().get(request);
        JsonElement json = gson.fromJson(raw, JsonElement.class);
        JsonObject jo = json.getAsJsonObject();
        Measure [] tmp = (gson.fromJson(jo.get("component").getAsJsonObject().get("measures"), Measure[].class));
        res.addAll(Arrays.asList(tmp));

        return res;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }
}
