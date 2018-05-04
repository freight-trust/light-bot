package com.networknt.bot.develop;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class DevelopBuildTaskTest {
    //@Test
    public void testDevelop() throws IOException, InterruptedException {
        DevelopBuildTask cmd = new DevelopBuildTask();
        int result = cmd.execute();
        if(result == 1) {
            System.out.println("none of the repo has been changed");
        } else {
            System.out.println("at least one repo is changed");
        }
    }

    //@Test
    public void testCheckout() throws IOException, InterruptedException {
        DevelopBuildTask cmd = new DevelopBuildTask();
        int result = cmd.checkout();
        if(result == 1) {
            System.out.println("none of the repo has been changed");
        } else {
            System.out.println("at least one repo is changed");
        }
    }

    //@Test
    public void testTest() throws IOException, InterruptedException {
        DevelopBuildTask cmd = new DevelopBuildTask();
        int result = cmd.test();
        Assert.assertEquals(0, result);
    }
}
