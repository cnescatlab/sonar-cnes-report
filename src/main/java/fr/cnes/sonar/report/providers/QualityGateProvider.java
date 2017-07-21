package fr.cnes.sonar.report.providers;

import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.exceptions.UnknownQualityGateException;
import fr.cnes.sonar.report.model.QualityGate;
import fr.cnes.sonar.report.input.Params;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides quality gates
 * @author begarco
 */
public class QualityGateProvider extends AbstractDataProvider {

    /**
     * Complete constructor
     * @param params Program's parameters
     * @param singleton RequestManager which does http request
     * @throws UnknownParameterException The program does not recognize the parameter
     */
    public QualityGateProvider(Params params, RequestManager singleton) throws UnknownParameterException {
        super(params, singleton);
    }

    /**
     * Get all the quality gates
     * @return Array containing all the issues
     * @throws IOException when connecting the server
     * @throws BadSonarQubeRequestException A request is not recognized by the server
     */
    public List<QualityGate> getQualityGates()
            throws IOException, BadSonarQubeRequestException {
        // result list
        final List<QualityGate> res = new ArrayList<>();

        // Get all quality gates
        String request = String.format(getRequest(GET_QUALITY_GATES_REQUEST), getUrl());
        // perform the request to the server
        JsonObject jo = request(request);

        // Get quality gates criteria
        String defaultQG = (getGson().fromJson(jo.get(DEFAULT), String.class));
        QualityGate[] tmp = (getGson().fromJson(jo.get(QUALITYGATES), QualityGate[].class));
        // for each quality gate
        for (QualityGate i : tmp) {
            // request the criteria
            request = String.format(getRequest(GET_QUALITY_GATES_DETAILS_REQUEST),
                    getUrl(), i.getName().replaceAll(" ", "%20"));
            // perform previous request
            jo = request(request);

            // put it in configuration field
            i.setConf(jo.toString());
            // check if it is the default quality gate
            if (i.getId().equals(defaultQG)) {
                i.setDefault(true);
            } else {
                i.setDefault(false);
            }
            // add the quality gate to the result list
            res.add(i);
        }

        return res;
    }

    /**
     * Return the quality gate corresponding to the project
     * @return The Quality Gate
     * @throws IOException when there are problem reading json
     * @throws UnknownQualityGateException when there is an error on a quality gate
     * @throws BadSonarQubeRequestException when the request is incorrect
     */
    public QualityGate getProjectQualityGate()
            throws IOException, UnknownQualityGateException, BadSonarQubeRequestException {
        QualityGate res = null;
        QualityGate tmp;
        boolean find = false;
        // get all the quality gates
        List<QualityGate> qualityGates = getQualityGates();

        // search for the good quality gate
        Iterator<QualityGate> iterator = qualityGates.iterator();
        while (iterator.hasNext() && !find) {
            tmp = iterator.next();
            if(tmp.getName().equals(getQualityGateName())) {
                res = tmp;
                find = true;
            }
        }

        // check if it was found
        if(!find) {
            throw new UnknownQualityGateException(getQualityGateName());
        }

        return res;
    }
}
