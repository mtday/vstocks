package vstocks.rest;

import vstocks.db.DBFactory;
import vstocks.service.remote.RemoteStockServiceFactory;

public class Environment {
    private DBFactory dbFactory;
    private RemoteStockServiceFactory remoteStockServiceFactory;
    private boolean includeSecurity = true;
    private boolean includeBackgroundTasks = true;

    public Environment() {
    }

    public DBFactory getDBFactory() {
        return dbFactory;
    }

    public Environment setDBFactory(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
        return this;
    }

    public RemoteStockServiceFactory getRemoteStockServiceFactory() {
        return remoteStockServiceFactory;
    }

    public Environment setRemoteStockServiceFactory(RemoteStockServiceFactory remoteStockServiceFactory) {
        this.remoteStockServiceFactory = remoteStockServiceFactory;
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
