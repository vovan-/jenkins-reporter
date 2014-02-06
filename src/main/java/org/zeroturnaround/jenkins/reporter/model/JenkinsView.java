/**
 *    Copyright (C) 2013 ZeroTurnaround LLC <support@zeroturnaround.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.zeroturnaround.jenkins.reporter.model;

import static com.google.common.collect.Lists.newArrayList;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import org.zeroturnaround.jenkins.reporter.model.Job.BadJobPredicate;
import org.zeroturnaround.jenkins.reporter.model.Job.GoodJobPredicate;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class JenkinsView {
  private Collection<Job> jobs = newArrayList();
  private int jobsTotal;
  private String name;
  private URI url;

  public int getFailCount() {
    int failCount = 0;
    for (Job job : jobs) {
      failCount += job.getFailCount();
    }
    return failCount;
  }

  public Collection<Job> getFailedJobs() {
    return Lists.newArrayList(Iterables.filter(getJobs(), new BadJobPredicate()));
  }

  public float getFailureRate() {
    if (getTestsTotal() != 0) {
      return 100 * (float) getFailCount() / getTestsTotal();
    }
    else {
      return 0;
    }
  }

  public Collection<Job> getJobs() {
    return new ArrayList<Job>(jobs);
  }

  public int getJobsTotal() {
    return jobsTotal;
  }

  public String getName() {
    return name;
  }

  public Collection<Job> getPassedJobs() {
    return Lists.newArrayList(Iterables.filter(getJobs(), new GoodJobPredicate()));
  }

  public int getTestsTotal() {
    int totalCount = 0;
    for (Job job : jobs) {
      totalCount += job.getTotalCount();
    }
    return totalCount;
  }

  public URI getUrl() {
    return url;
  }


  public void setJobs(Collection<Job> jobs) {
    this.jobs = jobs;
  }

  public void setJobsTotal(int jobsTotal) {
    this.jobsTotal = jobsTotal;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUrl(URI url) {
    this.url = url;
  }
}