/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jenkinsci.plugins.workflowparam.cps;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.TextParameterDefinition;
import hudson.model.queue.QueueTaskFuture;
import jenkins.model.ParameterizedJobMixIn;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.concurrent.Future;

public class CpsParamFlowDefinitionTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void testRunScriptFromParameter() throws Exception {
        String parameterName = "pipeline_script";
        String defaultScriptMessage = "Hello from Parameter";
        String expectScriptMessage = "Test Message";

        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");

        TextParameterDefinition paramDefinition = new TextParameterDefinition(
                parameterName,
                "echo '" + defaultScriptMessage + "'",
                "pipeline script");

        p.addProperty(new ParametersDefinitionProperty(paramDefinition));

        p.setDefinition(new CpsParamFlowDefinition(parameterName));

        ParameterValue value = paramDefinition.createValue("echo '" + expectScriptMessage + "'");

        WorkflowRun b = buildAndAssertSuccess(p, new ParametersAction(value));

        String log = b.getLog();
        System.out.println(log);

        Assert.assertFalse(log.contains(defaultScriptMessage));
        Assert.assertTrue(log.contains(expectScriptMessage));
    }

    public WorkflowRun buildAndAssertSuccess(WorkflowJob job, Action... action) throws Exception {
        QueueTaskFuture f = new ParameterizedJobMixIn() {
            @Override
            protected Job asJob() {
                return job;
            }
        }.scheduleBuild2(0, action);
        @SuppressWarnings("unchecked") // no way to make this compile checked
                Future<WorkflowRun> f2 = f;
        return r.assertBuildStatusSuccess(f2);
    }
}
