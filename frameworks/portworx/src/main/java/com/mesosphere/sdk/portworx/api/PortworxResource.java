package com.mesosphere.sdk.portworx.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.mesos.Protos;

import com.mesosphere.sdk.api.ResponseUtils;
import com.mesosphere.sdk.curator.*;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 *  Portworx Resource.
 */
@Path("/v1/px")
@Produces("application/json")
public class PortworxResource {
    private final Logger log = LoggerFactory.getLogger(PortworxResource.class);
    private final String frameworkName;
    private final CuratorStateStore store;

    public PortworxResource(String frameworkName) {
        this.frameworkName = frameworkName;
        log.info("Using framework name {}", frameworkName);
        store = new CuratorStateStore(frameworkName);
    }

    @GET
    @Path("/status")
    public Response listNodes() {
        try {
            // For each portworx task that has finished, try to get the list of
            // nodes. Stop when we get a successfull reply from any one node
            for (String taskName : store.fetchTaskNames()) {
                log.info("Checking taskname {}", taskName);
                if (taskName.contains("portworx")) {
                    Optional<Protos.TaskStatus> status = store.fetchStatus(taskName);
                    Protos.ContainerStatus containerStatus = status.get().getContainerStatus();
                    HttpClient client = HttpClientBuilder.create().build();
                    HttpGet request = new HttpGet(String.format("http://%s:9001/v1/cluster/enumerate",
                                containerStatus.getNetworkInfos(0).getIpAddresses(0).getIpAddress()));
                    HttpEntity entity = client.execute(request).getEntity();
                    return ResponseUtils.plainOkResponse(EntityUtils.toString(entity, "UTF-8"));
                }
            }
            return ResponseUtils.plainOkResponse("Portworx installation not found");
        } catch (Exception ex) {
            log.error("Failed to fetch Nodes with exception:", ex);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/volumes")
    public Response listVolumes() {
        try {
            // For each portworx task that has finished, try to get the list of
            // nodes. Stop when we get a successfull reply from any one node
            for (String taskName : store.fetchTaskNames()) {
                log.info("Checking taskname {}", taskName);
                if (taskName.contains("portworx")) {
                    Optional<Protos.TaskStatus> status = store.fetchStatus(taskName);
                    Protos.ContainerStatus containerStatus = status.get().getContainerStatus();
                    HttpClient client = HttpClientBuilder.create().build();
                    HttpGet request = new HttpGet(String.format("http://%s:9001/v1/osd-volumes",
                                containerStatus.getNetworkInfos(0).getIpAddresses(0).getIpAddress()));
                    HttpEntity entity = client.execute(request).getEntity();
                    return ResponseUtils.plainOkResponse(EntityUtils.toString(entity, "UTF-8"));
                }
            }
            return ResponseUtils.plainOkResponse("Portworx installation not found");
        } catch (Exception ex) {
            log.error("Failed to fetch Volumes with exception:", ex);
            return Response.serverError().build();
        }
    }
}
