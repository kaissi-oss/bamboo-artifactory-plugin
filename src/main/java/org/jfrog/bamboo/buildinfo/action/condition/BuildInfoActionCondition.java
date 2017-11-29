/*
 * Copyright (C) 2010 JFrog Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jfrog.bamboo.buildinfo.action.condition;

import com.atlassian.bamboo.plan.*;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.bamboo.build.Job;
import org.jfrog.bamboo.util.ConstantValues;

import java.util.List;
import java.util.Map;

/**
 * Condition class to determine if the Build info summary tab should be displayed
 *
 * @author Noam Y. Tenne
 */
public class BuildInfoActionCondition implements Condition {

    private PlanManager planManager;
    private ResultsSummaryManager resultsSummaryManager;

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        String buildNumber = context.get("buildNumber") == null ? null : (String)context.get("buildNumber");
        if (buildNumber == null) {
            return false;
        }

        List<Job> planJobs = planManager.getAllPlans(Job.class);
        for (Job job : planJobs) {
            PlanKey planKey = job.getPlanKey();
            PlanResultKey planResultKey = PlanKeys.getPlanResultKey(planKey, Integer.parseInt(buildNumber));
            ResultsSummary resultsSummary = resultsSummaryManager.getResultsSummary(planResultKey);

            if (resultsSummary != null && resultsSummary.isSuccessful()) {
                String buildInfoActivated = resultsSummary.getCustomBuildData().get(ConstantValues.BUILD_RESULT_COLLECTION_ACTIVATED_PARAM);
                if (Boolean.valueOf(buildInfoActivated)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }

    public void setResultsSummaryManager(ResultsSummaryManager resultsSummaryManager) {
        this.resultsSummaryManager = resultsSummaryManager;
    }
}
