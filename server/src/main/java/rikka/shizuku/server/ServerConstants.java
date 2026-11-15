package rikka.shizuku.server;

public class ServerConstants {

    public static final int MANAGER_APP_NOT_FOUND = 50;

    public static final String PERMISSION = "moe.shizuku.manager.permission.API_V23";

    // Action handled by the manager's REQUEST_PERMISSION activity in builds that were created
    // before the runtime action derivation was introduced (e.g. older hidden clones). Hidden
    // clones built from now on respond only to the runtime-derived action; the server falls
    // back to this one for compatibility. Do not use for anything else.
    public static final String REQUEST_PERMISSION_ACTION_LEGACY = "moe.shizuku.privileged.api.intent.action.REQUEST_PERMISSION";

    public static final int BINDER_TRANSACTION_getApplications = 10001;
}
