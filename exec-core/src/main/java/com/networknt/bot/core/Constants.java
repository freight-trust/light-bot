package com.networknt.bot.core;

public class Constants {
    // develop
    public static final String WORKSPACE = "workspace";
    public static final String CHECKOUT = "checkout";
    public static final String BRANCH = "branch";
    public static final String REPOSITORY = "repository";
    public static final String BUILD = "build";
    public static final String PROJECT = "project";
    public static final String SKIP_TEST = "skipTest";
    public static final String SKIP_E2ETEST = "skipE2ETest";
    public static final String TEST = "test";
    public static final String SERVER = "server";
    public static final String REQUEST = "request";
    public static final String PATH = "path";
    public static final String CMD = "cmd";
    public static final String HOST = "host";
    public static final String METHOD = "method";
    public static final String RESPONSE = "response";
    public static final String STATUS = "status";
    public static final String HEADER = "header";
    public static final String BODY = "body";

    // version
    public static final String OLD_VERSION = "old_version";
    public static final String NEW_VERSION = "new_version";
    public static final String UPGRADE = "upgrade";
    public static final String MATCH = "match";
    public static final String MAVEN = "maven";
    public static final String VERSION = "version";

    // release
    public static final String ORGANIZATION = "organization";
    public static final String RELEASE = "release";

    // dockerhub
    public static final String MERGE = "merge";
    public static final String DOCKER = "docker";

    // regex-replace
    public static final String REPLACE = "replace";
    public static final String OLD_VALUE = "old_value";
    public static final String NEW_VALUE = "new_value";
    public static final String SKIP_CHECKOUT = "skip_checkout";
    public static final String SKIP_REPLACE = "skip_replace";
    public static final String SKIP_CHECKIN = "skip_checkin";
    public static final String GLOB = "glob";

    // this result code indicate not repository has been change
    // when pulling from remote. need to skip the build and test
    public static final int NO_REPO_CHANGE = 100;
}
