package vstocks.rest;

import vstocks.achievement.AchievementService;
import vstocks.db.ServiceFactory;
import vstocks.rest.security.JwtSecurity;
import vstocks.service.remote.RemoteStockServiceFactory;

public class Environment {
    private ServiceFactory serviceFactory;
    private RemoteStockServiceFactory remoteStockServiceFactory;
    private AchievementService achievementService;
    private JwtSecurity jwtSecurity;
    private boolean includeSecurity = true;
    private boolean includeBackgroundTasks = true;

    public Environment() {
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public Environment setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        return this;
    }

    public RemoteStockServiceFactory getRemoteStockServiceFactory() {
        return remoteStockServiceFactory;
    }

    public Environment setRemoteStockServiceFactory(RemoteStockServiceFactory remoteStockServiceFactory) {
        this.remoteStockServiceFactory = remoteStockServiceFactory;
        return this;
    }

    public AchievementService getAchievementService() {
        return achievementService;
    }

    public Environment setAchievementService(AchievementService achievementService) {
        this.achievementService = achievementService;
        return this;
    }

    public JwtSecurity getJwtSecurity() {
        return jwtSecurity;
    }

    public Environment setJwtSecurity(JwtSecurity jwtSecurity) {
        this.jwtSecurity = jwtSecurity;
        return this;
    }

    public boolean isIncludeSecurity() {
        return includeSecurity;
    }

    public Environment setIncludeSecurity(boolean includeSecurity) {
        this.includeSecurity = includeSecurity;
        return this;
    }

    public boolean isIncludeBackgroundTasks() {
        return includeBackgroundTasks;
    }

    public Environment setIncludeBackgroundTasks(boolean includeBackgroundTasks) {
        this.includeBackgroundTasks = includeBackgroundTasks;
        return this;
    }
}
