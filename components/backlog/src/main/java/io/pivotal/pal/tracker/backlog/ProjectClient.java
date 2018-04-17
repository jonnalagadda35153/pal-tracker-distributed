package io.pivotal.pal.tracker.backlog;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class ProjectClient {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private Map<Long,ProjectInfo> concurrentMap = new ConcurrentHashMap<>();
    private final RestOperations restOperations;
    private final String endpoint;

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        //Retrieving the project based on the Id
        ProjectInfo project = restOperations.getForObject(endpoint+"/projects"+projectId, ProjectInfo.class);
        concurrentMap.put(projectId, project);
        return project;

    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting project with id {} from cache"+ projectId);
        //returning the Project Info based on the id passed
        return concurrentMap.get(projectId);
    }
}